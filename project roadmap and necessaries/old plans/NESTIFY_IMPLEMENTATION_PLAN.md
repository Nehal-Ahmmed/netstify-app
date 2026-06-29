# 🎓 Nestify — Full Implementation Plan
## The Complete Build Manual (Production-Grade, Code-Level)

> This is the **executable** plan. Every wave lists exact files (new + modified), the code or schema to write, the DI wiring, the tests, and the acceptance gate. Code matches the repo's real conventions: package `com.nhbhuiyan.nestify`, Hilt `object` modules with `@Provides @Singleton`, DataStore companion-key pattern, `Route` sealed class, Room 2.6 + KAPT.
>
> Companion docs: `NESTIFY_MASTER_PLAN.md` (architecture & rationale). Read that for *why*; read this for *how*.

---

## Conventions used in this document
- **`[NEW]`** = create file · **`[MOD]`** = edit existing file.
- Each wave ends with a **✅ Gate** — do not advance until it passes.
- Cloud Functions are **TypeScript** in a new top-level `functions/` dir.
- Package roots: `data/` (datastore, di, local, remote, repository), `domain/` (manager, model, repository, usecases), `presentation/` (navigation, ui, viewModel).

---

# WAVE 0 — Quick Wins & Data Safety (0.5 day, zero new dependencies)

Stop active bugs and data-loss risk before building anything new.

### 0.1 Fix the Google Drive folder-name bug `[MOD]`
**File:** `presentation/ui/screens/ExamPlanner/GoogleDriveSyncManager.kt`
- Root folder constant resolves to `"Nestify"` (line ~246) but `uploadToDrive()` (line ~54) searches `"Nestify Backups"`. Unify to one constant.
```kotlin
companion object { const val ROOT_FOLDER = "Nestify" }   // single source of truth
```
Replace every literal `"Nestify"` / `"Nestify Backups"` with `ROOT_FOLDER`.

### 0.2 Kill destructive Room migration `[MOD]`
**File:** `data/di/DataModule.kt` (line 38) — currently `.fallbackToDestructiveMigration()`. Every version bump **wipes student grades, notes, CT marks.**
1. **File:** `app/build.gradle.kts` — add schema export so migrations are diffable:
```kotlin
kapt { arguments { arg("room.schemaLocation", "$projectDir/schemas") } }
android { defaultConfig { /* … */ } }   // ensure schemas/ committed to git
```
2. **File:** `data/local/AppDataBase.kt` — set `exportSchema = true`.
3. **File:** `data/di/DataModule.kt` — replace `.fallbackToDestructiveMigration()` with explicit migrations:
```kotlin
.addMigrations(MIGRATION_18_19)   // PYQEntity table add; one per future change
```
4. **File:** `[NEW] data/local/migrations/Migrations.kt`
```kotlin
val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
          CREATE TABLE IF NOT EXISTS pyq_entity (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            topicId INTEGER NOT NULL,
            questionText TEXT, questionImagePath TEXT,
            answerText TEXT, answerImagePath TEXT,
            nbFormulas TEXT, nbTheories TEXT, nbConstants TEXT, nbExtras TEXT,
            repeatCount INTEGER NOT NULL DEFAULT 1,
            yearsSeen TEXT NOT NULL DEFAULT '', marks TEXT,
            FOREIGN KEY(topicId) REFERENCES syllabus_topic(id) ON DELETE CASCADE
          )""".trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS index_pyq_topicId ON pyq_entity(topicId)")
    }
}
```
> Bump `AppDataBase` version to **19** and verify the generated `schemas/…/19.json` matches `PYQEntity`. Confirm actual table/column names against the real `@Entity` annotations before running.

**✅ Gate W0:** Drive backup writes & restores from a single `Nestify` folder. App upgrades from an installed v18 build **without data loss** (manually verify: install old, add a note, upgrade, note survives).

---

# WAVE 1 — Foundations: Firebase Backend + Security Substrate (1.5 day)

Nothing role-aware can be trusted until this exists. **This wave builds the server, not the app.**

### 1.1 Add Firebase dependencies `[MOD]`
**File:** `gradle/libs.versions.toml`
```toml
[libraries]
firebase-firestore   = { group = "com.google.firebase", name = "firebase-firestore-ktx" }
firebase-storage     = { group = "com.google.firebase", name = "firebase-storage-ktx" }
firebase-messaging   = { group = "com.google.firebase", name = "firebase-messaging-ktx" }
firebase-functions   = { group = "com.google.firebase", name = "firebase-functions-ktx" }
firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics-ktx" }
firebase-config      = { group = "com.google.firebase", name = "firebase-config-ktx" }
```
**File:** `app/build.gradle.kts` (in `dependencies`, under the existing Firebase BOM):
```kotlin
implementation(libs.firebase.firestore)
implementation(libs.firebase.storage)
implementation(libs.firebase.messaging)
implementation(libs.firebase.functions)
implementation(libs.firebase.crashlytics)
implementation(libs.firebase.config)
```
Add Crashlytics Gradle plugin to root + app `plugins {}` (`com.google.firebase.crashlytics`).

### 1.2 Enable Firestore offline persistence `[MOD]`
**File:** the `@HiltAndroidApp Application` class (find under root package).
```kotlin
override fun onCreate() {
    super.onCreate()
    FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings {
        setLocalCacheSettings(persistentCacheSettings { })   // disk cache = offline for shared data
    }
}
```

### 1.3 Cloud Functions project `[NEW] functions/`
Init `firebase init functions` (TypeScript). Implement four functions:

```ts
// functions/src/index.ts
import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";
admin.initializeApp();
const db = admin.firestore();

const SUPER_ADMIN_UID = "REPLACE_WITH_YOUR_UID";
const DOMAIN = "@student.cuet.ac.bd";

// Email → identity (server-side authority; client cannot fake)
function parse(email: string) {
  const m = /^u(\d{2})(\d{2})(\d{3})@student\.cuet\.ac\.bd$/.exec(email.toLowerCase());
  if (!m) return null;
  const [, batch, dept, roll] = m;
  return { batchYear: +batch, departmentCode: dept, rollNumber: +roll,
           classGroupId: `CUET-${dept}-${batch}`, studentId: `${batch}${dept}${roll}` };
}

// 1) On sign-up: create authoritative profile + claim + roster bind
export const onUserCreate = functions.auth.user().onCreate(async (user) => {
  const email = user.email ?? "";
  if (!email.endsWith(DOMAIN)) {            // domain enforcement
    await admin.auth().deleteUser(user.uid); return;
  }
  const id = parse(email);
  if (!id) { await admin.auth().setCustomUserClaims(user.uid, { role: "pending" }); return; }

  const role = user.uid === SUPER_ADMIN_UID ? "superadmin" : "student";
  await admin.auth().setCustomUserClaims(user.uid, {
    role, classGroupId: id.classGroupId, departmentCode: id.departmentCode,
  });

  const rosterRef = db.doc(`classGroups/${id.classGroupId}/roster/${id.rollNumber}`);
  await db.runTransaction(async (tx) => {
    const snap = await tx.get(rosterRef);
    const collision = snap.exists && snap.get("uid") !== user.uid;
    tx.set(db.doc(`users/${user.uid}`), {
      email, role, ...id, displayName: user.displayName ?? "",
      pendingReview: collision, createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
    if (!collision) tx.set(rosterRef, { uid: user.uid, displayName: user.displayName ?? "" });
  });
});

// 2) Privileged role change (only admins may call; clients can never self-promote)
export const setUserRole = functions.https.onCall(async (data, ctx) => {
  const caller = ctx.auth?.token;
  if (!caller || !["admin", "superadmin"].includes(caller.role))
    throw new functions.https.HttpsError("permission-denied", "Not an admin");
  const { targetUid, role } = data as { targetUid: string; role: string };
  if (role === "superadmin") throw new functions.https.HttpsError("permission-denied", "No");
  const target = (await db.doc(`users/${targetUid}`).get()).data();
  await admin.auth().setCustomUserClaims(targetUid, {
    role, classGroupId: target?.classGroupId, departmentCode: target?.departmentCode });
  await db.doc(`users/${targetUid}`).update({ role, forceRefresh: true });
  return { ok: true };
});

// 3) Merge-request accept = privileged atomic copy (client only flips status→accepted)
export const onMergeRequestResolved = functions.firestore
  .document("classGroups/{gid}/mergeRequests/{mrId}")
  .onUpdate(async (chg) => {
    const before = chg.before.data(), after = chg.after.data();
    if (before.status !== "pending" || after.status !== "accepted") return;
    const d = after.data, dept = after.departmentCode, sem = after.semesterId;
    // idempotent: deterministic id from MR
    const topicPath = `departments/${dept}/semesters/${sem}/subjects/${after.targetSubjectCode}/topics`;
    if (after.type === "topic")
      await db.doc(`${topicPath}/${chg.after.id}`).set(d, { merge: true });
    else
      await db.doc(`${topicPath}/${after.targetTopicId}/pyqs/${chg.after.id}`).set(d, { merge: true });
    // FCM notify submitter handled by 4)
  });

// 4) FCM fan-out on MR resolve + announcement create (server-triggered only)
export const advanceSemester = functions.https.onCall(async (data, ctx) => {
  // CR+ archives prior CT marks then clears them; client triggers, server does privileged delete
  // (full body in Wave 6)
  return { ok: true };
});
```

### 1.4 Firestore Security Rules `[NEW] firestore.rules`
The keystone. Full ruleset (from master plan §3) — implement verbatim, expand `onlyAffects`/`diff` helpers:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{db}/documents {
    function role()        { return request.auth.token.role; }
    function myGroup()     { return request.auth.token.classGroupId; }
    function myDept()      { return request.auth.token.departmentCode; }
    function isCRplus()    { return role() in ['cr','admin','superadmin']; }
    function isAdminPlus() { return role() in ['admin','superadmin']; }
    function changed(keys) { return request.resource.data.diff(resource.data).affectedKeys().hasOnly(keys); }

    match /users/{uid} {
      allow read:   if request.auth != null;
      allow create: if request.auth.uid == uid;
      allow update: if request.auth.uid == uid
                    && !('role' in request.resource.data.diff(resource.data).affectedKeys());
    }
    match /classGroups/{gid} {
      allow read:   if myGroup() == gid;
      allow update: if myGroup() == gid && isCRplus() && changed(['currentLevel','currentTerm']);
      match /roster/{roll} { allow read: if myGroup() == gid; allow write: if false; } // server only
      match /ctMarks/{sem}/{studentId} {
        allow read:  if myGroup() == gid && (isCRplus() || request.auth.uid == studentId);
        allow write: if myGroup() == gid && isCRplus();
      }
      match /mergeRequests/{mrId} {
        allow create: if myGroup() == gid
                      && request.resource.data.submittedBy == request.auth.uid
                      && request.resource.data.status == 'pending';
        allow read:   if myGroup() == gid;
        allow update: if myGroup() == gid && isCRplus() && changed(['status','reviewedBy','reviewNote','reviewedAt']);
      }
      match /announcements/{id} {
        allow read:  if myGroup() == gid;
        allow write: if myGroup() == gid && isCRplus();
      }
    }
    match /departments/{dept}/{rest=**} {
      allow read:  if request.auth != null;
      allow write: if isCRplus() && myDept() == dept;
    }
  }
}
```
**File:** `[NEW] storage.rules` — mirror: PYQ images read by group members, write by CR+ of the dept.

### 1.5 Emulator Suite `[NEW] firebase.json`
```json
{ "firestore": { "rules": "firestore.rules" },
  "storage":   { "rules": "storage.rules" },
  "functions": { "source": "functions" },
  "emulators": { "auth": {"port":9099}, "firestore": {"port":8080},
                 "functions": {"port":5001}, "storage": {"port":9199}, "ui": {"enabled":true} } }
```

### 1.6 Rules unit tests `[NEW] functions/test/rules.test.ts`
Highest-ROI tests in the whole project — they prove the security model.
```ts
// student cannot escalate own role
await assertFails(setDoc(studentDb.doc("users/U1"), { role: "cr" }, { merge: true }));
// student cannot write another student's CT marks
await assertFails(setDoc(studentDb.doc("classGroups/CUET-04-23/ctMarks/L2T2/OTHER"), {}));
// student CAN read own CT marks
await assertSucceeds(getDoc(studentDb.doc("classGroups/CUET-04-23/ctMarks/L2T2/U1")));
// CR CAN write subjects in own dept
await assertSucceeds(setDoc(crDb.doc("departments/04/semesters/L2T2/subjects/CSE-221"), {...}));
// CR CANNOT write another dept
await assertFails(setDoc(crDb.doc("departments/01/semesters/L2T2/subjects/EEE-101"), {...}));
```

**✅ Gate W1:** `firebase emulators:exec "npm test"` green. Super-admin claim seeded. Rules reject every cross-role write in the test matrix. **No app code depends on this wave yet — it's the foundation.**

---

# WAVE 2 — Identity & Session (1.5 day)

Auth already works (`AuthViewModel` uses `FirebaseAuth`). Build **identity + session + role caching** on top, and make auth testable.

### 2.1 Pure identity parser `[NEW] domain/model/StudentIdentity.kt`
```kotlin
package com.nhbhuiyan.nestify.domain.model

data class StudentIdentity(
    val batchYear: Int, val departmentCode: String, val rollNumber: Int,
    val classGroupId: String, val studentId: String,
) {
    companion object {
        private val PATTERN = Regex("""^u(\d{2})(\d{2})(\d{3})@student\.cuet\.ac\.bd$""")
        fun parse(email: String): StudentIdentity? {
            val m = PATTERN.matchEntire(email.trim().lowercase()) ?: return null
            val (b, d, r) = m.destructured
            return StudentIdentity(b.toInt(), d, r.toInt(), "CUET-$d-$b", "$b$d$r")
        }
    }
}
```
### 2.2 Role + session models `[NEW] domain/model/UserRole.kt`, `UserSession.kt`
```kotlin
enum class UserRole { STUDENT, CR, ADMIN, SUPER_ADMIN, PENDING;
    companion object { fun from(s: String?) = when (s) {
        "cr" -> CR; "admin" -> ADMIN; "superadmin" -> SUPER_ADMIN; "student" -> STUDENT; else -> PENDING } }
    val rank get() = ordinal   // STUDENT<CR<ADMIN<SUPER_ADMIN for `>=` checks
}
data class UserSession(
    val uid: String, val role: UserRole, val classGroupId: String?,
    val departmentCode: String?, val identity: StudentIdentity?, val isLoading: Boolean = false,
) { companion object { val Guest = UserSession("", UserRole.PENDING, null, null, null, true) } }
```
### 2.3 DataStore role cache `[MOD] data/datastore/SettingDatastore.kt`
Add keys + accessors following the existing companion/`map` pattern:
```kotlin
val USER_ROLE_KEY      = stringPreferencesKey("user_role")
val CLASS_GROUP_ID_KEY = stringPreferencesKey("class_group_id")
val STUDENT_ID_KEY     = stringPreferencesKey("student_id")
val DEPT_CODE_KEY      = stringPreferencesKey("dept_code")
// + suspend setters and Flow getters mirroring setDefaultLevel/defaultLevel
```
Purpose: cold-start UI shows correct role before the Firestore listener fires.

### 2.4 Session manager `[NEW] domain/manager/UserSessionManager.kt` (Hilt singleton)
```kotlin
@Singleton
class UserSessionManager @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore,
    private val settings: SettingDatastore,
) {
    private val _session = MutableStateFlow(UserSession.Guest)
    val session: StateFlow<UserSession> = _session.asStateFlow()
    private var reg: ListenerRegistration? = null

    fun bind() {  // call from Application / first authed screen
        val uid = auth.currentUser?.uid ?: run { _session.value = UserSession.Guest; return }
        reg?.remove()
        reg = firestore.document("users/$uid").addSnapshotListener { snap, _ ->
            val role = UserRole.from(snap?.getString("role"))
            val gid  = snap?.getString("classGroupId")
            _session.value = UserSession(uid, role, gid, snap?.getString("departmentCode"),
                StudentIdentity.parse(snap?.getString("email") ?: ""))
            if (snap?.getBoolean("forceRefresh") == true)
                auth.currentUser?.getIdToken(true)   // pull fresh custom claims after role change
        }
    }
    fun unbind() { reg?.remove(); reg = null }
}
```
DI: add `@Provides FirebaseAuth/FirebaseFirestore/FirebaseFunctions/FirebaseStorage` in a new `[NEW] data/di/FirebaseModule.kt`; provide `UserSessionManager` in `DataModule` or `FirebaseModule`.

### 2.5 Make auth testable `[MOD] AuthViewModel.kt`
Extract `[NEW] domain/repository/AuthRepository.kt` + `[NEW] data/remote/AuthRepositoryImpl.kt` (wraps `FirebaseAuth`). Inject it instead of `FirebaseAuth.getInstance()`. On `Authenticated`, call `sessionManager.bind()`. On sign-up, validate `StudentIdentity.parse(email) != null` client-side (server re-validates).

### 2.6 Tests `[NEW] test/StudentIdentityTest.kt`
Valid email → correct fields; wrong domain → null; short roll → null; uppercase → normalized; `CUET-04-23` group id correctness. **~10 cases.**

**✅ Gate W2:** New sign-up creates `users/{uid}` via the Function with correct parsed fields + claim. `UserSessionManager.session` emits the right `UserRole`. Parser tests pass. Killing the app and reopening shows cached role instantly (DataStore) then live role (listener).

---

# WAVE 3 — RBAC Engine + Firestore Repos (2 days, two parallel tracks)

### Track A — Permission framework (UX layer)
`[NEW] domain/manager/RolePermissionManager.kt`
```kotlin
sealed interface Permission {
    data object EditLevelTerm: Permission;     data object EditSubjectDetails: Permission
    data object EditExamPlan: Permission;       data object EditCTMarks: Permission
    data object ReviewMergeRequests: Permission;data object AssignRoles: Permission
    data object ManageAnnouncements: Permission;data object FullAppAccess: Permission
}
object RolePermissionManager {
    fun hasPermission(role: UserRole, p: Permission): Boolean = when (p) {
        Permission.AssignRoles, Permission.FullAppAccess -> role.rank >= UserRole.ADMIN.rank
        Permission.EditExamPlan, Permission.EditCTMarks, Permission.EditSubjectDetails,
        Permission.EditLevelTerm, Permission.ReviewMergeRequests, Permission.ManageAnnouncements
            -> role.rank >= UserRole.CR.rank
    }
}
```
`[NEW] presentation/ui/components/RoleGate.kt`
```kotlin
@Composable fun RoleGate(required: UserRole, current: UserRole,
    fallback: @Composable () -> Unit = {}, content: @Composable () -> Unit) {
    if (current.rank >= required.rank) content() else fallback()
}
```
> **Reminder banner in code comments:** RoleGate is cosmetic; authority is `firestore.rules` (W1).

### Track B — Firestore repositories
Interfaces in `domain/repository/`, impls in `data/remote/`. Expose realtime data as cold `Flow` via `callbackFlow`, **always removing the listener in `awaitClose`** (cost/leak guard).

`[NEW] domain/repository/ClassGroupRepository.kt` (+ Impl):
```kotlin
interface ClassGroupRepository {
    fun observeConfig(gid: String): Flow<ClassGroupConfig>          // 1 listener / whole class
    suspend fun setLevelTerm(gid: String, level: Int, term: Int)    // CR+ ; rules enforce
    fun observeCtMarks(gid: String, sem: String, studentId: String): Flow<CtMarks?>
    suspend fun upsertCtMarks(gid: String, sem: String, studentId: String, marks: CtMarks)
}
```
Impl pattern:
```kotlin
override fun observeConfig(gid: String) = callbackFlow {
    val reg = firestore.document("classGroups/$gid").addSnapshotListener { s, e ->
        if (e != null) { close(e); return@addSnapshotListener }
        trySend(s.toClassGroupConfig()) }
    awaitClose { reg.remove() }     // <-- mandatory
}
```
Also create: `[NEW] DepartmentRepository` (subjects/exam-plan/topics/pyqs — **`get()` one-shots**, not perpetual listeners), `[NEW] MergeRequestRepository`, `[NEW] AnnouncementRepository`, `[NEW] UserRepository`.
DI: wire all in `FirebaseModule.kt`.

**✅ Gate W3:** `RolePermissionManager` unit-tested across all 4 roles × 8 permissions. A repo `Flow` emits live on Firestore console edit and **stops reading** (verify in emulator UI) when the collector cancels.

---

# WAVE 4 — Exam Planner Source Swap + RBAC Navigation (2.5 days)

Highest user value. The trick: **keep `ExamPlannerViewModel`'s `StateFlow` shapes identical**; only change what feeds them.

### 4.1 Merge repository `[NEW] domain/repository/ExamDataRepository.kt`
Single seam combining shared (Firestore) + personal (Room overlay):
```kotlin
interface ExamDataRepository {
    fun observeSubjects(gid: String, dept: String, sem: String): Flow<List<Subject>> // Firestore
    fun observePersonalProgress(): Flow<Map<String, TopicProgress>>                  // Room, by topicId
    fun observeGrades(): Flow<Map<String, String>>                                   // Room ONLY (personal)
    suspend fun setGrade(subjectCode: String, grade: String)                         // Room ONLY
}
```
### 4.2 ViewModel rewire `[MOD] ExamPlannerViewModel.kt`
- **Delete** the seed-on-empty hardcoded subjects (lines ~82-130).
- Source `subjects`, `examPlan`, `syllabusTopics`, `pyqs` from `ExamDataRepository`/Firestore keyed by `UserSessionManager.session.classGroupId` + current `L{level}T{term}`.
- Keep `finalGrade`, `attendance`, topic `isCompleted/isRevised/priority` in **Room** (personal overlay), merged in the VM.
- Expose `currentRole` from `UserSessionManager` so tabs can gate edit controls.

### 4.3 Tab edits (read-only vs editable by role) `[MOD]`
| File | Student | CR+ |
|------|---------|-----|
| `SubjectsDetailsTab.kt` | read-only list | edit → `DepartmentRepository.upsertSubject` (rules enforce dept) |
| `ExamPlanTab.kt` | read-only (auto from Firestore) | edit exam dates/schedule |
| `ClassTestMarksTab.kt` | read **own** marks + PDF download | edit all students → `upsertCtMarks` |
| `ExamResultsTab.kt` (CGPA) | **unchanged** — 100% Room + Drive, never Firestore | same |
Wrap edit affordances in `RoleGate(required = UserRole.CR, current = role)`.

### 4.4 RBAC navigation `[MOD] Route.kt / InAppNav.kt / NavGraph.kt`
Add routes (no 6th bottom tab — Management lives **inside Services**, preserving thumb-reach UX):
```kotlin
object Management:      Route("management")
object MergeRequests:   Route("management/mergeRequests")
object RoleManagement:  Route("management/roles")
object Announcements:   Route("management/announcements")
object ClassSettings:   Route("management/classSettings")
```
In `InAppNav.kt`, collect `UserSessionManager.session`; show the "Management" entry in Services only when `role.rank >= UserRole.CR.rank`. In `NavGraph.kt`, role-check before composing restricted destinations (defense-in-depth; rules are the real guard).

**✅ Gate W4:** Student sees subjects/exam-plan/CT-marks **read-only** and **no** Management entry. CR sees edit controls; a CR edit appears on a student's device in realtime (two emulator clients). CGPA tab still works fully offline (Room).

---

# WAVE 5 — Data Seeding (1 day)

### 5.1 Seed script `[NEW] functions/seed/seedDept04.ts`
Idempotent (`subjectCode`-keyed `set`, re-runnable). Seeds all 8 semesters of CSE subjects + exam-plan templates into `departments/04/semesters/{L_T_}`.
```ts
const L2T2 = [{ code:"CSE-221", name:"Database Systems", credits:3.0 }, /* … */];
for (const s of L2T2) await db.doc(`departments/04/semesters/L2T2/subjects/${s.code}`).set(s, {merge:true});
```
### 5.2 Flow
Run against **emulator** first (`firebase emulators:exec`), export the data set, then `firebase deploy` the rules + functions and run the seed once against prod with the super-admin credential.
### 5.3 Dept registry to Remote Config `[NEW]`
Move the dept-code map (`01→EEE … 04→CSE`) into Firebase Remote Config (or `config/departments` doc) so adding a department needs **no app release**.

**✅ Gate W5:** A fresh student account in batch `CUET-04-23` opens Exam Planner and sees the seeded L2T2 subjects with zero local seeding.

---

# WAVE 6 — Reading Room + Merge Requests (2.5 days)

### 6.1 Reading Room reads Firestore `[MOD] ReadingRoomScreen.kt`
Topics + PYQs from `DepartmentRepository` (offline cache covers reading). Paginate PYQs (`limit(20)` + `startAfter`). Images via Coil from Storage URLs.

### 6.2 Merge-request model `[NEW] domain/model/MergeRequest.kt`
```kotlin
enum class MergeRequestType { TOPIC, PYQ_QUESTION, PYQ_ANSWER, PYQ_BOTH }
enum class MergeRequestStatus { PENDING, ACCEPTED, REJECTED }
data class MergeRequest(
    val id: String, val type: MergeRequestType, val targetSubjectCode: String,
    val targetTopicId: String?, val semesterId: String, val departmentCode: String,
    val data: Map<String, Any>, val submittedBy: String, val submitterName: String,
    val status: MergeRequestStatus, val reviewedBy: String? = null,
    val reviewNote: String? = null, val submittedAt: Timestamp? = null, val reviewedAt: Timestamp? = null,
)
```
### 6.3 Submit flow (Student) `[MOD] ReadingRoomScreen.kt`
Local draft marked 🔴 → "Submit for Review" → `MergeRequestRepository.submit()` creates `mergeRequests/{mrId}` (rules allow create-only, `status==pending`, `submittedBy==uid`). Status chip: 🔴 Local → 🟡 Pending → ✅/❌.

### 6.4 Review flow (CR+) `[NEW] presentation/ui/screens/management/MergeRequestScreen.kt`
Tabs: Pending / Accepted / Rejected. Diff view (proposed vs existing). Accept = client flips `status→accepted` (rules: CR+ only, only status fields) → **`onMergeRequestResolved` Function does the privileged atomic copy** into `departments/...` (idempotent via deterministic doc id). Reject = `status→rejected` + `reviewNote`. Both FCM-notify submitter.

### 6.5 CT auto-archive `[MOD] functions advanceSemester` + `AcademicArchiveSyncEngine.kt`
When CR advances level/term: client calls `advanceSemester` Function → archives prior `ctMarks/{sem}` (the existing ZIP/Drive packaging handles the local archive) then server deletes the Firestore CT subtree. Client-side `PackagingSyncTab` backs the archive to Drive first.

**✅ Gate W6:** Student submits a topic MR → CR accepts → topic appears in `departments/04/.../topics` for **all** students, MR shows ✅, submitter gets FCM. Re-running the accept (idempotency) does not duplicate. Reject path notifies + keeps local.

---

# WAVE 7 — Management, Announcements, FCM, Drive Hardening (2 days)

### 7.1 Management dashboard `[NEW] presentation/ui/screens/management/ManagementScreen.kt`
Hub linking MergeRequests / RoleManagement / Announcements / ClassSettings. Entire subtree gated `role >= CR`.

### 7.2 Role management `[NEW] RoleManagementScreen.kt`
Class roster (`classGroups/{gid}/roster`); assign CR via `FirebaseFunctions.getInstance().getHttpsCallable("setUserRole")`. Admin+ only (`RoleGate` + rules + function-side check = triple guard).

### 7.3 Announcements `[NEW] AnnouncementsScreen.kt` + `[MOD]` Home banner
`classGroups/{gid}/announcements` (CR+ write, all read). Home shows urgent banner. Create → FCM fan-out via Function.

### 7.4 FCM `[NEW] presentation/service/NestifyMessagingService.kt`
Store `fcmToken` on `users/{uid}`; handle MR-resolved / announcement notifications. Channels for "Academic" and "Urgent".

### 7.5 Drive hardening `[MOD] GoogleDriveSyncManager.kt / PackagingSyncTab.kt`
Structured layout (`personal/` + `academic/{semester}.zip`), retry with backoff, partial-sync resume. Keep existing Zip-Slip guard.

**✅ Gate W7:** Admin promotes a student to CR (claim refreshes, Management appears for them). Urgent announcement reaches all class devices via FCM. Drive backup/restore round-trips the structured layout.

---

# Cross-Cutting (do continuously, not a separate wave)

| Concern | Where it lands |
|---------|----------------|
| **Crashlytics + Analytics** | W1 init; events for sign-up parse-fail, MR funnel, sync errors, role changes. |
| **Read-cost discipline** | 1 config listener/class; `get()` for static dept data; `limit()`+pagination on PYQ/MR lists; `awaitClose{reg.remove()}` everywhere. Budget vs 50k reads/day free tier. |
| **CI** | GitHub Action: `./gradlew assembleDebug testDebugUnitTest` + `firebase emulators:exec "npm test"` (rules). Block merge on red. |
| **Feature flags** | Remote Config: `firestore_enabled`, `management_enabled` — dark-launch waves without breaking current users. |
| **Offline verification** | Each shared feature: airplane-mode read from cache + queued write on reconnect. |

---

# File Manifest (complete)

### New files (Android)
```
domain/model/StudentIdentity.kt, UserRole.kt, UserSession.kt, MergeRequest.kt, Announcement.kt
domain/manager/UserSessionManager.kt, RolePermissionManager.kt
domain/repository/AuthRepository.kt, UserRepository.kt, ClassGroupRepository.kt,
                  DepartmentRepository.kt, MergeRequestRepository.kt, AnnouncementRepository.kt, ExamDataRepository.kt
data/remote/AuthRepositoryImpl.kt, UserRepositoryImpl.kt, ClassGroupRepositoryImpl.kt,
            DepartmentRepositoryImpl.kt, MergeRequestRepositoryImpl.kt, AnnouncementRepositoryImpl.kt, ExamDataRepositoryImpl.kt
data/di/FirebaseModule.kt
data/local/migrations/Migrations.kt
presentation/ui/components/RoleGate.kt
presentation/ui/screens/management/ManagementScreen.kt, MergeRequestScreen.kt, RoleManagementScreen.kt, AnnouncementsScreen.kt, ClassSettingsScreen.kt
presentation/service/NestifyMessagingService.kt
test/StudentIdentityTest.kt, RolePermissionManagerTest.kt
```
### New files (backend)
```
firebase.json, firestore.rules, storage.rules, .firebaserc
functions/src/index.ts, functions/seed/seedDept04.ts, functions/test/rules.test.ts
```
### Modified files (Android)
```
build.gradle.kts (root), app/build.gradle.kts, gradle/libs.versions.toml
<Application>.kt, AuthViewModel.kt, AuthScreen.kt
data/datastore/SettingDatastore.kt, data/di/DataModule.kt, data/local/AppDataBase.kt
presentation/navigation/Components/Route.kt, InAppNav.kt, NavGraph.kt
ExamPlannerViewModel.kt, SubjectsDetailsTab.kt, ExamPlanTab.kt, ClassTestMarksTab.kt
ReadingRoomScreen.kt, GoogleDriveSyncManager.kt, PackagingSyncTab.kt, AcademicArchiveSyncEngine.kt
HomeScreen (announcement banner)
```

---

# Execution Summary

| Wave | Theme | Blocks | Effort | Gate |
|------|-------|--------|--------|------|
| **W0** | Drive fix + Room migrations | — | 0.5d | No data loss on upgrade |
| **W1** | Firebase backend + **rules** + emulator + functions | W0 | 1.5d | Rules tests green |
| **W2** | Identity + session + auth refactor | W1 | 1.5d | Profile auto-created, role cached |
| **W3** | RBAC engine ∥ Firestore repos | W2 | 2d | Permission tests + live Flow |
| **W4** | Exam Planner swap + RBAC nav | W3 | 2.5d | Student read-only, CR live edit |
| **W5** | Seeding + Remote Config | W4 | 1d | Fresh account sees seeded data |
| **W6** | Reading Room + Merge Requests | W2,W5 | 2.5d | MR accept merges + notifies |
| **W7** | Management + FCM + Drive hardening | W6 | 2d | Role promote + announcements live |

**Critical path:** W0 → W1 → W2 → W4. **Total ≈ 13.5 focused days.** The true gate is W1 (rules + emulator), not auth.

### Definition of Done — every shared-data feature
1. Security rules cover it **+** an emulator test proves a wrong-role write is rejected.
2. Privileged paths (role assign, MR accept, CT archive) run in a **Cloud Function**, never a client batch.
3. Offline behavior verified (cache read + queued write).
4. Read cost bounded (no unbounded listener; pagination on lists; `awaitClose` cleanup).
5. Crashlytics breadcrumb + Analytics event added.
