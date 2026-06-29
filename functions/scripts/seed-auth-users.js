/**
 * Seeds Firebase Authentication with users that exactly mirror the Firestore
 * dummy data created by `FirebaseTestDataInitializer.kt`.
 *
 * It MUST run with the Admin SDK because:
 *   - Each Auth account needs a FIXED uid (`seed-<batch>-<dept>-<roll>`) so that
 *     `users/{auth.uid}` in Firestore lines up with the seeded profile docs.
 *   - The Android client (createUserWithEmailAndPassword) cannot set a custom uid
 *     and would sign in as each created account, corrupting the session.
 *
 * Result: 4 depts x 4 batches x 10 students = 160 Auth users.
 *   - email:    uXXYYZZZ@student.cuet.ac.bd   (matches StudentIdentity.parse)
 *   - password: 123456   (same for everyone, per request)
 *   - uid:      seed-<batch>-<deptNumeric>-<roll>
 *
 * Usage (production) — pass the downloaded service-account key as an argument:
 *   # PowerShell, from the `functions/` folder:
 *   node scripts/seed-auth-users.js "C:\path\to\serviceAccountKey.json"
 *
 *   (Alternatively set $env:GOOGLE_APPLICATION_CREDENTIALS to the key path and
 *    run `node scripts/seed-auth-users.js` with no argument.)
 *
 * Usage (Auth emulator):
 *   $env:FIREBASE_AUTH_EMULATOR_HOST = "localhost:9099"
 *   node scripts/seed-auth-users.js
 *
 * The project id is read from the service-account key, so the users are always
 * created in the same project the Android app talks to.
 */

const admin = require("firebase-admin");
const path = require("path");

// Fallback project id (the one app/google-services.json points at) — only used
// for the emulator, where no key is supplied.
const DEFAULT_PROJECT_ID = "nestify-nehal1458112";
const PASSWORD = "123456";

const deptNumeric = { CSE: "04", EEE: "08", CE: "01", ME: "02" };
const batches = ["21", "22", "23", "24"];
const studentNames = [
  "Tanvir Ahmed", "Sadia Islam", "Rakibul Hasan", "Nusrat Jahan", "Mahir Tajwar",
  "Farhana Akter", "Sabbir Rahman", "Maliha Chowdhury", "Imran Kabir", "Tasnim Ferdous",
];

function initAdmin() {
  if (process.env.FIREBASE_AUTH_EMULATOR_HOST) {
    // Emulator needs no real credentials.
    const projectId = process.env.GCLOUD_PROJECT || DEFAULT_PROJECT_ID;
    admin.initializeApp({ projectId });
    console.log(`Using Auth emulator at ${process.env.FIREBASE_AUTH_EMULATOR_HOST} (project ${projectId})`);
    return;
  }

  const keyPath = process.argv[2] || process.env.GOOGLE_APPLICATION_CREDENTIALS;
  if (!keyPath) {
    console.error(
      "No service-account key provided.\n" +
      'Run: node scripts/seed-auth-users.js "C:\\path\\to\\serviceAccountKey.json"'
    );
    process.exit(1);
  }

  const serviceAccount = require(path.resolve(keyPath));
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    projectId: serviceAccount.project_id,
  });
  console.log(`Using production project ${serviceAccount.project_id} (key: ${path.basename(keyPath)})`);
}

/** Build the full list of users, identical to the Firestore seeder's formulas. */
function buildUsers() {
  const users = [];
  for (const dept of Object.keys(deptNumeric)) {
    const numeric = deptNumeric[dept];
    for (const batch of batches) {
      for (let roll = 1; roll <= 10; roll++) {
        const roll3 = String(roll).padStart(3, "0");
        users.push({
          uid: `seed-${batch}-${numeric}-${roll3}`,
          email: `u${batch}${numeric}${roll3}@student.cuet.ac.bd`,
          password: PASSWORD,
          displayName: studentNames[roll - 1],
          role: roll <= 4 ? "cr" : "student",
        });
      }
    }
  }
  return users;
}

/** Create the user, or update password/email if the uid already exists. */
async function upsertUser(u) {
  const auth = admin.auth();
  try {
    await auth.createUser({
      uid: u.uid,
      email: u.email,
      emailVerified: true,
      password: u.password,
      displayName: u.displayName,
      disabled: false,
    });
    return "created";
  } catch (e) {
    if (e.code === "auth/uid-already-exists" || e.code === "auth/email-already-exists") {
      await auth.updateUser(u.uid, {
        email: u.email,
        emailVerified: true,
        password: u.password,
        displayName: u.displayName,
      });
      return "updated";
    }
    throw e;
  }
}

async function main() {
  initAdmin();
  const users = buildUsers();
  console.log(`Seeding ${users.length} Auth users (password = "${PASSWORD}")...`);

  let created = 0;
  let updated = 0;
  let failed = 0;

  for (const u of users) {
    try {
      const result = await upsertUser(u);
      if (result === "created") created++;
      else updated++;
      console.log(`  ✓ ${result.padEnd(7)} ${u.uid}  ${u.email}  [${u.role}]`);
    } catch (e) {
      failed++;
      console.error(`  ✗ FAILED  ${u.uid}  ${u.email}: ${e.message}`);
    }
  }

  console.log(
    `\nDone. created=${created} updated=${updated} failed=${failed} (total ${users.length}).`
  );
  process.exit(failed > 0 ? 1 : 0);
}

main().catch((e) => {
  console.error("Fatal:", e);
  process.exit(1);
});
