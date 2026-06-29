"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
const rules_unit_testing_1 = require("@firebase/rules-unit-testing");
const firestore_1 = require("firebase/firestore");
const fs = __importStar(require("fs"));
const path = __importStar(require("path"));
describe("Firestore Security Rules", () => {
    let testEnv;
    before(async () => {
        let rulesPath = "firestore.rules";
        if (!fs.existsSync(rulesPath)) {
            if (fs.existsSync("../firestore.rules")) {
                rulesPath = "../firestore.rules";
            }
            else if (fs.existsSync("../../firestore.rules")) {
                rulesPath = "../../firestore.rules";
            }
            else if (fs.existsSync("../../../firestore.rules")) {
                rulesPath = "../../../firestore.rules";
            }
        }
        testEnv = await (0, rules_unit_testing_1.initializeTestEnvironment)({
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
    function getStudentDb(uid, classGroupId, deptCode) {
        return testEnv.authenticatedContext(uid, {
            role: "student",
            classGroupId,
            deptCode,
        }).firestore();
    }
    // Helper to create a CR context
    function getCrDb(uid, classGroupId, deptCode) {
        return testEnv.authenticatedContext(uid, {
            role: "cr",
            classGroupId,
            deptCode,
        }).firestore();
    }
    describe("Users Collection", () => {
        it("allows any signed-in user to read user profiles", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            const profileDoc = (0, firestore_1.doc)(db, "users/bob");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.getDoc)(profileDoc));
        });
        it("prevents students from creating their own profile (managed by function)", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            const profileDoc = (0, firestore_1.doc)(db, "users/alice");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.setDoc)(profileDoc, { displayName: "Alice", role: "student" }));
        });
        it("allows users to update non-identity fields on their own profile", async () => {
            // Pre-seed the user doc using admin context
            await testEnv.withSecurityRulesDisabled(async (context) => {
                const adminDb = context.firestore();
                await (0, firestore_1.setDoc)((0, firestore_1.doc)(adminDb, "users/alice"), {
                    uid: "alice",
                    email: "ualice@student.cuet.ac.bd",
                    displayName: "Alice",
                    role: "student",
                    classGroupId: "CUET-CSE-23",
                    departmentCode: "CSE",
                });
            });
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            const profileDoc = (0, firestore_1.doc)(db, "users/alice");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.updateDoc)(profileDoc, { displayName: "Alice Updated" }));
        });
        it("prevents users from updating their role or classGroupId", async () => {
            await testEnv.withSecurityRulesDisabled(async (context) => {
                const adminDb = context.firestore();
                await (0, firestore_1.setDoc)((0, firestore_1.doc)(adminDb, "users/alice"), {
                    uid: "alice",
                    email: "ualice@student.cuet.ac.bd",
                    displayName: "Alice",
                    role: "student",
                    classGroupId: "CUET-CSE-23",
                    departmentCode: "CSE",
                });
            });
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            const profileDoc = (0, firestore_1.doc)(db, "users/alice");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.updateDoc)(profileDoc, { role: "admin" }));
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.updateDoc)(profileDoc, { classGroupId: "CUET-EEE-23" }));
        });
    });
    describe("Class Test (CT) Marks", () => {
        const ctPath = "classGroups/CUET-CSE-23/ctMarks/L2T2/alice";
        beforeEach(async () => {
            await testEnv.withSecurityRulesDisabled(async (context) => {
                const adminDb = context.firestore();
                await (0, firestore_1.setDoc)((0, firestore_1.doc)(adminDb, ctPath), {
                    "CSE-221": { ct1: 15, ct2: 18 }
                });
            });
        });
        it("allows a student to read their own CT marks", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.getDoc)((0, firestore_1.doc)(db, ctPath)));
        });
        it("prevents a student from reading another student's CT marks", async () => {
            const db = getStudentDb("charlie", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.getDoc)((0, firestore_1.doc)(db, ctPath)));
        });
        it("allows a CR of the same class group to read all CT marks", async () => {
            const db = getCrDb("bob", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.getDoc)((0, firestore_1.doc)(db, ctPath)));
        });
        it("prevents a student from writing CT marks", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, ctPath), { "CSE-221": { ct1: 20 } }));
        });
        it("allows a CR of the same class group to write/edit CT marks", async () => {
            const db = getCrDb("bob", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, ctPath), { "CSE-221": { ct1: 20 } }));
        });
    });
    describe("Merge Requests", () => {
        const mrPath = "classGroups/CUET-CSE-23/mergeRequests/mr-1";
        it("allows any student in the group to submit a pending MR with their own UID", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, mrPath), {
                type: "subject",
                target: "CSE-221",
                semesterId: "L2T2",
                departmentCode: "CSE",
                data: { name: "Database Systems" },
                status: "pending",
                submittedBy: "alice",
                submittedAt: (0, firestore_1.serverTimestamp)(),
            }));
        });
        it("prevents a student from submitting an MR with 'accepted' status", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, mrPath), {
                type: "subject",
                target: "CSE-221",
                semesterId: "L2T2",
                departmentCode: "CSE",
                data: { name: "Database Systems" },
                status: "accepted",
                submittedBy: "alice",
                submittedAt: (0, firestore_1.serverTimestamp)(),
            }));
        });
        it("allows a CR of the group to accept/resolve an MR", async () => {
            await testEnv.withSecurityRulesDisabled(async (context) => {
                const adminDb = context.firestore();
                await (0, firestore_1.setDoc)((0, firestore_1.doc)(adminDb, mrPath), {
                    type: "subject",
                    target: "CSE-221",
                    status: "pending",
                    submittedBy: "alice",
                });
            });
            const db = getCrDb("bob", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.updateDoc)((0, firestore_1.doc)(db, mrPath), {
                status: "accepted",
                reviewedBy: "bob",
                reviewedAt: (0, firestore_1.serverTimestamp)(),
            }));
        });
    });
    describe("Shared Academic Content", () => {
        const subjectPath = "departments/CSE/semesters/L2T2/subjects/CSE-221";
        it("allows any authenticated user to read subjects", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.getDoc)((0, firestore_1.doc)(db, subjectPath)));
        });
        it("prevents normal students from directly modifying subjects", async () => {
            const db = getStudentDb("alice", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, subjectPath), { name: "Database Systems" }));
        });
        it("allows a CR of the same department to modify subjects", async () => {
            const db = getCrDb("bob", "CUET-CSE-23", "CSE");
            await (0, rules_unit_testing_1.assertSucceeds)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, subjectPath), { name: "Database Systems", credits: 3.0 }));
        });
        it("prevents a CR of another department from modifying subjects", async () => {
            const db = getCrDb("charlie", "CUET-EEE-23", "EEE");
            await (0, rules_unit_testing_1.assertFails)((0, firestore_1.setDoc)((0, firestore_1.doc)(db, subjectPath), { name: "Database Systems" }));
        });
    });
});
//# sourceMappingURL=rules.test.js.map