import {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} from "@firebase/rules-unit-testing";
import { doc, getDoc, setDoc, updateDoc, serverTimestamp } from "firebase/firestore";
import * as fs from "fs";
import * as path from "path";

describe("Firestore Security Rules", () => {
  let testEnv: any;

  before(async () => {
    let rulesPath = "firestore.rules";
    if (!fs.existsSync(rulesPath)) {
      if (fs.existsSync("../firestore.rules")) {
        rulesPath = "../firestore.rules";
      } else if (fs.existsSync("../../firestore.rules")) {
        rulesPath = "../../firestore.rules";
      } else if (fs.existsSync("../../../firestore.rules")) {
        rulesPath = "../../../firestore.rules";
      }
    }
    
    testEnv = await initializeTestEnvironment({
      projectId: "nestify-cuet",
      firestore: {
        rules: fs.readFileSync(path.resolve(rulesPath), "utf8"),
        host: "127.0.0.1",
        port: 8080,
      },
    });
  });

  beforeEach(async () => {
    await testEnv.clearFirestore();
  });

  after(async () => {
    await testEnv.cleanup();
  });

  // Helper to create a student context
  function getStudentDb(uid: string, classGroupId: string, deptCode: string) {
    return testEnv.authenticatedContext(uid, {
      role: "student",
      classGroupId,
      deptCode,
    }).firestore();
  }

  // Helper to create a CR context
  function getCrDb(uid: string, classGroupId: string, deptCode: string) {
    return testEnv.authenticatedContext(uid, {
      role: "cr",
      classGroupId,
      deptCode,
    }).firestore();
  }

  describe("Users Collection", () => {
    it("allows any signed-in user to read user profiles", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      const profileDoc = doc(db, "users/bob");
      await assertSucceeds(getDoc(profileDoc));
    });

    it("prevents students from creating their own profile (managed by function)", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      const profileDoc = doc(db, "users/alice");
      await assertFails(setDoc(profileDoc, { displayName: "Alice", role: "student" }));
    });

    it("allows users to update non-identity fields on their own profile", async () => {
      // Pre-seed the user doc using admin context
      await testEnv.withSecurityRulesDisabled(async (context: any) => {
        const adminDb = context.firestore();
        await setDoc(doc(adminDb, "users/alice"), {
          uid: "alice",
          email: "ualice@student.cuet.ac.bd",
          displayName: "Alice",
          role: "student",
          classGroupId: "CUET-CSE-23",
          departmentCode: "CSE",
        });
      });

      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      const profileDoc = doc(db, "users/alice");
      await assertSucceeds(updateDoc(profileDoc, { displayName: "Alice Updated" }));
    });

    it("prevents users from updating their role or classGroupId", async () => {
      await testEnv.withSecurityRulesDisabled(async (context: any) => {
        const adminDb = context.firestore();
        await setDoc(doc(adminDb, "users/alice"), {
          uid: "alice",
          email: "ualice@student.cuet.ac.bd",
          displayName: "Alice",
          role: "student",
          classGroupId: "CUET-CSE-23",
          departmentCode: "CSE",
        });
      });

      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      const profileDoc = doc(db, "users/alice");
      await assertFails(updateDoc(profileDoc, { role: "admin" }));
      await assertFails(updateDoc(profileDoc, { classGroupId: "CUET-EEE-23" }));
    });
  });

  describe("Class Test (CT) Marks", () => {
    const ctPath = "classGroups/CUET-CSE-23/ctMarks/L2T2/alice";

    beforeEach(async () => {
      await testEnv.withSecurityRulesDisabled(async (context: any) => {
        const adminDb = context.firestore();
        await setDoc(doc(adminDb, ctPath), {
          "CSE-221": { ct1: 15, ct2: 18 }
        });
      });
    });

    it("allows a student to read their own CT marks", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertSucceeds(getDoc(doc(db, ctPath)));
    });

    it("prevents a student from reading another student's CT marks", async () => {
      const db = getStudentDb("charlie", "CUET-CSE-23", "CSE");
      await assertFails(getDoc(doc(db, ctPath)));
    });

    it("allows a CR of the same class group to read all CT marks", async () => {
      const db = getCrDb("bob", "CUET-CSE-23", "CSE");
      await assertSucceeds(getDoc(doc(db, ctPath)));
    });

    it("prevents a student from writing CT marks", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertFails(setDoc(doc(db, ctPath), { "CSE-221": { ct1: 20 } }));
    });

    it("allows a CR of the same class group to write/edit CT marks", async () => {
      const db = getCrDb("bob", "CUET-CSE-23", "CSE");
      await assertSucceeds(setDoc(doc(db, ctPath), { "CSE-221": { ct1: 20 } }));
    });
  });

  describe("Merge Requests", () => {
    const mrPath = "classGroups/CUET-CSE-23/mergeRequests/mr-1";

    it("allows any student in the group to submit a pending MR with their own UID", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertSucceeds(setDoc(doc(db, mrPath), {
        type: "subject",
        target: "CSE-221",
        semesterId: "L2T2",
        departmentCode: "CSE",
        data: { name: "Database Systems" },
        status: "pending",
        submittedBy: "alice",
        submittedAt: serverTimestamp(),
      }));
    });

    it("prevents a student from submitting an MR with 'accepted' status", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertFails(setDoc(doc(db, mrPath), {
        type: "subject",
        target: "CSE-221",
        semesterId: "L2T2",
        departmentCode: "CSE",
        data: { name: "Database Systems" },
        status: "accepted",
        submittedBy: "alice",
        submittedAt: serverTimestamp(),
      }));
    });

    it("allows a CR of the group to accept/resolve an MR", async () => {
      await testEnv.withSecurityRulesDisabled(async (context: any) => {
        const adminDb = context.firestore();
        await setDoc(doc(adminDb, mrPath), {
          type: "subject",
          target: "CSE-221",
          status: "pending",
          submittedBy: "alice",
        });
      });

      const db = getCrDb("bob", "CUET-CSE-23", "CSE");
      await assertSucceeds(updateDoc(doc(db, mrPath), {
        status: "accepted",
        reviewedBy: "bob",
        reviewedAt: serverTimestamp(),
      }));
    });
  });

  describe("Shared Academic Content", () => {
    const subjectPath = "departments/CSE/semesters/L2T2/subjects/CSE-221";

    it("allows any authenticated user to read subjects", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertSucceeds(getDoc(doc(db, subjectPath)));
    });

    it("prevents normal students from directly modifying subjects", async () => {
      const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
      await assertFails(setDoc(doc(db, subjectPath), { name: "Database Systems" }));
    });

    it("allows a CR of the same department to modify subjects", async () => {
      const db = getCrDb("bob", "CUET-CSE-23", "CSE");
      await assertSucceeds(setDoc(doc(db, subjectPath), { name: "Database Systems", credits: 3.0 }));
    });

    it("prevents a CR of another department from modifying subjects", async () => {
      const db = getCrDb("charlie", "CUET-EEE-23", "EEE");
      await assertFails(setDoc(doc(db, subjectPath), { name: "Database Systems" }));
    });
  });
});
