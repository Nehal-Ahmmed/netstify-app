import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();

// Hardcoded Super Admins list (UIDs or Emails)
const SUPER_ADMIN_EMAILS = [
  "nehal.cuet@gmail.com",
  "nehal.ahmmed.cuet@gmail.com",
  "nehal.ahmmed@student.cuet.ac.bd"
];

// Department Code Map (CUET Code -> Department Name)
const DEPT_MAP: { [key: string]: string } = {
  "04": "CSE",
  "08": "EEE",
  "01": "CE",
  "02": "ME",
  "03": "IPE"
};

/**
 * Triggered on User Creation. Parses email, creates user profile,
 * sets custom claims, and binds roll number in the class roster.
 */
export const onUserCreate = functions.auth.user().onCreate(async (user) => {
  const uid = user.uid;
  const email = user.email;

  if (!email) {
    console.error(`User ${uid} has no email address.`);
    return;
  }

  // 1. Check if the email belongs to CUET student domain
  const emailRegex = /^u(\d{2})(\d{2})(\d{3})@student\.cuet\.ac\.bd$/i;
  const match = email.match(emailRegex);

  let role = "student";
  let isSuperAdmin = false;

  // Check if this email is in our super admin list
  if (SUPER_ADMIN_EMAILS.includes(email.toLowerCase())) {
    role = "superadmin";
    isSuperAdmin = true;
  }

  if (!match && !isSuperAdmin) {
    console.warn(`User ${email} does not have a valid CUET student email. Disabling account.`);
    // Disable user to prevent unauthorized access
    await admin.auth().updateUser(uid, { disabled: true });
    return;
  }

  let batchYear = "";
  let departmentCode = "";
  let rollNumber = "";
  let classGroupId = "GLOBAL";

  if (match) {
    const batchShort = match[1]; // e.g. "23"
    const deptCode = match[2];  // e.g. "04"
    const roll = match[3];      // e.g. "097"

    batchYear = `20${batchShort}`;
    departmentCode = DEPT_MAP[deptCode] || deptCode;
    rollNumber = roll;
    classGroupId = `CUET-${departmentCode}-${batchShort}`;
  } else if (isSuperAdmin) {
    // Super Admin fallback profile details
    batchYear = "2020";
    departmentCode = "CSE";
    rollNumber = "000";
    classGroupId = "CUET-CSE-20";
  }

  const userRef = db.collection("users").doc(uid);
  const rosterRef = db.collection("classGroups").doc(classGroupId)
    .collection("roster").doc(rollNumber);

  try {
    await db.runTransaction(async (transaction) => {
      // 2. Roster collision check
      if (!isSuperAdmin) {
        const rosterDoc = await transaction.get(rosterRef);
        if (rosterDoc.exists) {
          const existingData = rosterDoc.data();
          if (existingData && existingData.uid !== uid) {
            // Collision detected! Roll number already claimed.
            console.error(`Roll number ${rollNumber} in group ${classGroupId} is already claimed by ${existingData.uid}`);
            
            // Write user profile as pending review
            transaction.set(userRef, {
              uid,
              email,
              displayName: user.displayName || "",
              photoUrl: user.photoURL || "",
              role: "pending",
              pendingReview: true,
              studentId: `${batchYear.slice(-2)}${departmentCode}${rollNumber}`,
              batchYear,
              departmentCode,
              rollNumber,
              classGroupId,
              createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
            return;
          }
        }
      }

      // 3. No collision: create profile and bind roster
      transaction.set(userRef, {
        uid,
        email,
        displayName: user.displayName || "",
        photoUrl: user.photoURL || "",
        role: role,
        pendingReview: false,
        studentId: match ? `${match[1]}${match[2]}${match[3]}` : "0000000",
        batchYear,
        departmentCode,
        rollNumber,
        classGroupId,
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });

      // Bind to roster
      transaction.set(rosterRef, {
        uid,
        displayName: user.displayName || "",
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
    });

    // 4. Set custom claims if not pending review
    const userDoc = await userRef.get();
    const userData = userDoc.data();
    if (userData && userData.role !== "pending") {
      await admin.auth().setCustomUserClaims(uid, {
        role: role,
        classGroupId: classGroupId,
        deptCode: departmentCode
      });
      console.log(`Successfully set claims for ${email}: role=${role}, classGroupId=${classGroupId}`);
    }

  } catch (error) {
    console.error("Error in onUserCreate transaction:", error);
  }
});

/**
 * Callable function to set a user's role.
 * Accessible only by Admins and Super Admins.
 */
export const setUserRole = functions.https.onCall(async (request) => {
  const auth = request.auth;
  if (!auth) {
    throw new functions.https.HttpsError(
      "unauthenticated",
      "The function must be called while authenticated."
    );
  }

  const callerRole = auth.token.role;
  if (callerRole !== "admin" && callerRole !== "superadmin") {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Only Admins and Super Admins can assign roles."
    );
  }

  const { targetUid, newRole } = request.data;
  if (!targetUid || !newRole) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Must provide targetUid and newRole."
    );
  }

  const allowedRoles = ["student", "cr", "admin"];
  if (!allowedRoles.includes(newRole)) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      `Invalid role: ${newRole}. Allowed roles: ${allowedRoles.join(", ")}`
    );
  }

  // Prevent self-demotion/promotion
  if (auth.uid === targetUid) {
    throw new functions.https.HttpsError(
      "permission-denied",
      "You cannot change your own role."
    );
  }

  const targetUserRef = db.collection("users").doc(targetUid);
  const targetUserDoc = await targetUserRef.get();
  if (!targetUserDoc.exists) {
    throw new functions.https.HttpsError(
      "not-found",
      "Target user profile not found."
    );
  }

  const targetUserData = targetUserDoc.data();
  if (!targetUserData) {
    throw new functions.https.HttpsError(
      "internal",
      "Target user profile is empty."
    );
  }

  // Super Admin check
  if (targetUserData.role === "superadmin" && callerRole !== "superadmin") {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Only a Super Admin can modify another Super Admin."
    );
  }

  // Update Firestore
  await targetUserRef.update({
    role: newRole,
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  });

  // Update Auth Custom Claims
  await admin.auth().setCustomUserClaims(targetUid, {
    role: newRole,
    classGroupId: targetUserData.classGroupId,
    deptCode: targetUserData.departmentCode
  });

  return { success: true, message: `Role of user ${targetUid} updated to ${newRole}` };
});

/**
 * Triggers when a Merge Request is updated.
 * If status changes to 'accepted', it atomically writes/merges the MR data
 * into the main academic department tree.
 */
export const onMergeRequestResolved = functions.firestore
  .document("classGroups/{groupId}/mergeRequests/{mrId}")
  .onUpdate(async (change, context) => {
    const beforeData = change.before.data();
    const afterData = change.after.data();

    if (!beforeData || !afterData) return;

    // Check if status transitioned to 'accepted'
    if (beforeData.status !== "accepted" && afterData.status === "accepted") {
      const { type, target, semesterId, departmentCode, data, action } = afterData;
      const groupId = context.params.groupId;

      if (!type || !target) {
        console.error("Merge Request is missing required fields (type, target).");
        return;
      }

      const isDelete = action === "delete";

      try {
        if (type === "subject") {
          if (!departmentCode || !semesterId) {
            console.error("Missing departmentCode or semesterId for subject MR.");
            return;
          }
          const subjectRef = db.collection("departments").doc(departmentCode)
            .collection("semesters").doc(semesterId)
            .collection("subjects").doc(target);
          if (isDelete) {
            await subjectRef.delete();
          } else {
            await subjectRef.set(data, { merge: true });
          }

        } else if (type === "topic") {
          if (!departmentCode || !semesterId) {
            console.error("Missing departmentCode or semesterId for topic MR.");
            return;
          }
          const [subjectCode, topicId] = target.split(":");
          const topicRef = db.collection("departments").doc(departmentCode)
            .collection("semesters").doc(semesterId)
            .collection("subjects").doc(subjectCode)
            .collection("topics").doc(topicId);
          if (isDelete) {
            await topicRef.delete();
          } else {
            await topicRef.set(data, { merge: true });
          }

        } else if (type === "pyq") {
          if (!departmentCode || !semesterId) {
            console.error("Missing departmentCode or semesterId for pyq MR.");
            return;
          }
          const [subjectCode, topicId, pyqId] = target.split(":");
          const pyqRef = db.collection("departments").doc(departmentCode)
            .collection("semesters").doc(semesterId)
            .collection("subjects").doc(subjectCode)
            .collection("topics").doc(topicId)
            .collection("pyqs").doc(pyqId);
          if (isDelete) {
            await pyqRef.delete();
          } else {
            await pyqRef.set(data, { merge: true });
          }

        } else if (type === "examDate") {
          if (!departmentCode || !semesterId) {
            console.error("Missing departmentCode or semesterId for examDate MR.");
            return;
          }
          const subjectRef = db.collection("departments").doc(departmentCode)
            .collection("semesters").doc(semesterId)
            .collection("subjects").doc(target);
          if (isDelete) {
            await subjectRef.update({ examDate: "" });
          } else {
            const examDateStr = data.examDate || "";
            await subjectRef.update({ examDate: examDateStr });
          }

        } else if (type === "ctMark-correction") {
          if (!semesterId) {
            console.error("Missing semesterId for ctMark-correction MR.");
            return;
          }
          const [studentId, subjectCode] = target.split(":");
          const ctMarksRef = db.collection("classGroups").doc(groupId)
            .collection("ctMarks").doc(semesterId)
            .collection(studentId).doc("marks");
          if (isDelete) {
            const updateObj: { [key: string]: any } = {};
            updateObj[subjectCode] = admin.firestore.FieldValue.delete();
            await ctMarksRef.update(updateObj);
          } else {
            const updateObj: { [key: string]: any } = {};
            updateObj[subjectCode] = data;
            await ctMarksRef.set(updateObj, { merge: true });
          }

        } else if (type === "roster-fix") {
          const rosterRef = db.collection("classGroups").doc(groupId)
            .collection("roster").doc(target);
          if (isDelete) {
            await rosterRef.delete();
          } else {
            await rosterRef.set(data, { merge: true });
          }

        } else if (type === "role-change") {
          const userRef = db.collection("users").doc(target);
          if (isDelete) {
            await userRef.delete();
          } else {
            const newRole = data.role;
            if (newRole) {
              await userRef.update({ role: newRole });
              const userDoc = await userRef.get();
              const userData = userDoc.data();
              if (userData) {
                await admin.auth().setCustomUserClaims(target, {
                  role: newRole,
                  classGroupId: userData.classGroupId || "",
                  deptCode: userData.departmentCode || ""
                });
              }
            }
          }
        }
        console.log(`Successfully processed MR ${context.params.mrId} of type ${type} (${action || 'merge'})`);
      } catch (err) {
        console.error(`Failed to process MR ${context.params.mrId}:`, err);
      }
    }
  });
