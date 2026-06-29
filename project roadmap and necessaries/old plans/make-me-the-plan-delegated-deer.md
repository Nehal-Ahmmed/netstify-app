# Nestify — University Student Ecosystem: Master Implementation Plan

## Context

Nestify today is a single-user productivity app: Jetpack Compose + Hilt + Room (v18) + DataStore, with **Firebase Auth and Google Sign-In already working** (`AuthViewModel` uses `FirebaseAuth.getInstance()`, `google-services.json` present) and a comprehensive Google Drive backup engine. The goal is to turn it into a **role-based academic platform** for ~132 CUET classmates, splitting data into two worlds: *personal* (Room + Drive, stays yours) and *shared institutional* (Firebase, governed by Student → CR → Admin → Super Admin roles).

The base plan (the user's `implementation_plan.md`) captures the product vision well but has three engineering gaps that separate a demo from a real multi-tenant system:

1. **No server-side enforcement.** The RBAC matrix is described as UI gating only. With the Firestore SDK, any student could write any document. The keystone — **Security Rules + Auth Custom Claims + Cloud Functions** — is missing.
2. **A caching contradiction.** "Firebase + keep Room as cache" for the *same* shared data = two stores that drift. Each data class needs exactly one source of truth.
3. **No identity bootstrapping.** Nothing decides who the first admin is, or stops user #2 from claiming Roll 097.

This plan keeps the vision intact and fills those gaps. Decisions confirmed with the user: **Firestore** (not RTDB); **address Room migrations now**; **restrict to `@student.cuet.ac.bd`**; **super admin = hardcoded UID, non-transferable initially**; **FCM via server triggers**. Backend tier is **not yet decided**, so every Cloud-Function-dependent feature below carries a **🟢 Spark fallback** (free-tier, client-side, less secure) alongside the **🔵 Blaze** path (recommended, server-enforced).

Audited file facts this plan builds on:
- `data/di/DataModule.kt:38` uses `.fallbackToDestructiveMigration()` → **every DB bump wipes student data.**
- `GoogleDriveSyncManager.kt`: root folder `"Nestify"` but `uploadToDrive()` searches `"Nestify Backups"` → confirmed bug.
- `ExamPlannerViewModel.kt:82-130`: hardcoded subjects are **seed-on-empty only**, sourced from Room via `ExamPlannerDao` — migration is a source-swap, not a rewrite.
- `PYQEntity.kt` is new/untracked; DB is at v18 with `exportSchema=false`.
- Bottom nav has 5 tabs (Home/Gallery/Library/Services/Profile) → Management belongs **inside Services**, not as a 6th tab.

---

## Three Architectural Pillars (the foundation everything rests on)

**Pillar A — Server-side authority.** Role lives in `users/{uid}.role` (for UI/queries) *and* as a Firebase Auth **custom claim** (for rules — free, in the JWT, unfakeable). Only a Cloud Function may write the claim, after verifying the caller is admin+. Firestore **Security Rules** enforce the entire permission matrix using `request.auth.token.role`. *Client-side `RoleGate` is cosmetic; rules are the real guard.*
> 🟢 **Spark fallback:** no custom claims. Rules read role from `get(/users/$uid).data.role` (costs one read per rule eval) and super-admin is a hardcoded UID. Self-promotion is blocked by a rule forbidding any `role` field change by the user themselves. Weaker (a determined user with the SDK can still race some writes) but workable.

**Pillar B — One source of truth per data class.** No data row lives in both Room and Firestore.
- **Personal** (Notes, Links, Schedules, Projects, Profile, **CGPA/Grades**, syllabus *progress* `isCompleted/isRevised/priority`, CT archive) → **Room** + Drive backup. Never on Firestore.
- **Shared** (Subjects, Exam Plans, Topics, PYQ content, CT marks, Announcements, MRs) → **Firestore** with the SDK's built-in offline persistence as the cache. **Not** mirrored into Room.
- The only overlay: *my personal progress on a shared topic* lives in Room keyed by the Firestore `topicId`.

**Pillar C — Identity bootstrapping.** Super Admin = your UID, hardcoded in rules + seeded once. On first sign-up, the email is parsed **server-side**, `users/{uid}` is created with `role:"student"`, the claim is set, and a `classGroups/{gid}/roster/{rollNumber}` doc binds the roll number (collision → `pendingReview`). Domain `@student.cuet.ac.bd` enforced server-side. Super Admin promotes Admins → Admins promote CRs.

---

## Corrected Firestore Data Model

Standardizes the base plan's path inconsistency (`departments/{dept}/subjects/{semId}` vs `…/semesters/{semId}/subjects`) and keys docs deterministically for idempotent seeding:

```
users/{uid}                     email, studentId, batchYear, departmentCode, rollNumber,
                                 classGroupId, role, displayName, photoUrl, fcmToken,
                                 pendingReview, createdAt
classGroups/{groupId}           currentLevel, currentTerm, crList[], adminList[]   (single doc → 1 listener/class)
  roster/{rollNumber}           { uid, displayName }                               (squatting guard + class list)
  ctMarks/{semesterId}/{studentId}  { CSE-221: {ct1..ct4, attendance} }            (keyed by student → rules guard own-row)
  mergeRequests/{mrId}          type, target*, semesterId, departmentCode, data, submittedBy,
                                 submitterName, status, reviewedBy, reviewNote, submittedAt, reviewedAt
  announcements/{id}            title, body, createdBy, createdAt, priority
departments/{deptCode}          name, totalStudents
  semesters/{semesterId}        ("L2T2")
    subjects/{subjectCode}      name, credits, examDate                            (keyed by code → no dup subjects)
    subjects/{subjectCode}/topics/{topicId}     title, section
      .../pyqs/{pyqId}          questionText, answerText, *ImageUrl, repeatCount, yearsSeen, marks, contributedBy
```
Firebase Storage mirrors this: `pyq_images/{deptCode}/{semesterId}/{topicId}/{pyqId}_{q|a}.webp` (compress to WebP client-side before upload). **Grades never touch Firestore** (Pillar B).

---

## Implementation Waves

Each wave is independently shippable and ends with a **✅ Gate** that must pass before advancing. Code matches repo conventions (package `com.nhbhuiyan.nestify`, Hilt `object` modules with `@Provides @Singleton`, DataStore companion-key pattern, `Route` sealed class).

### WAVE 0 — Quick wins & data safety (0.5 day, no new deps)
- **Drive folder bug** `[MOD] GoogleDriveSyncManager.kt`: unify to one `const val ROOT_FOLDER = "Nestify"`; replace every `"Nestify"`/`"Nestify Backups"` literal.
- **Kill destructive migration** `[MOD] DataModule.kt` (remove `.fallbackToDestructiveMigration()`), `[MOD] AppDataBase.kt` (`exportSchema=true`, bump to v19), `[MOD] app/build.gradle.kts` (kapt `room.schemaLocation`), `[NEW] data/local/migrations/Migrations.kt` with `MIGRATION_18_19` creating the `pyq_entity` table + index. Verify column names against the real `@Entity` before writing SQL.
- **✅ Gate:** Drive round-trips one `Nestify` folder; upgrading an installed v18 build preserves a pre-existing note.

### WAVE 1 — Firebase backend + security substrate (1.5 day, builds the server not the app)
- **Deps** `[MOD] libs.versions.toml` + `app/build.gradle.kts`: `firebase-firestore-ktx, -storage-ktx, -messaging-ktx, -functions-ktx, -crashlytics-ktx, -config-ktx`. Crashlytics plugin in root + app.
- **Offline persistence** `[MOD] <Application>.kt`: `persistentCacheSettings`.
- **Security Rules** `[NEW] firestore.rules` + `[NEW] storage.rules`: full ruleset enforcing the permission matrix (helpers `role()`, `myGroup()`, `myDept()`, `isCRplus()`, `changed(keys)`; users can't edit own `role`; students read only own CT row; MR create-only with `status==pending`; dept writes gated to own dept). 🔵 reads role from `request.auth.token.role`; 🟢 reads from `get(/users/$uid)`.
- **Emulator Suite** `[NEW] firebase.json` (auth/firestore/functions/storage emulators) — the only sane way to test rules.
- **🔵 Cloud Functions** `[NEW] functions/` (TypeScript): `onUserCreate` (parse email, create profile, set claim, bind roster, collision check, domain enforce), `setUserRole` (admin-only callable, blocks self-promotion & superadmin grant), `onMergeRequestResolved` (atomic idempotent copy into `departments/…` on status→accepted), `advanceSemester` (archive+delete CT marks). 🟢 **Spark fallback:** `onUserCreate` logic runs client-side after first auth (write profile + roster txn from the app); role assignment writes `users/{uid}.role` directly guarded by rules; MR accept is a client `WriteBatch`; CT archive is client-side delete after Drive backup.
- **Rules tests** `[NEW] functions/test/rules.test.ts`: assert student can't escalate role, can't write others' CT marks, can read own; CR can write own dept, not another dept.
- **✅ Gate:** `firebase emulators:exec "npm test"` green; super-admin claim/doc seeded; every cross-role write rejected.

### WAVE 2 — Identity & session (1.5 day)
- `[NEW] domain/model/StudentIdentity.kt`: pure regex parser `u(\d2)(\d2)(\d3)@student.cuet.ac.bd` → batch/dept/roll/classGroupId/studentId. Unit-tested hard.
- `[NEW] domain/model/UserRole.kt` (enum + `rank` for `>=` checks), `UserSession.kt`.
- `[MOD] data/datastore/SettingDatastore.kt`: add `USER_ROLE_KEY, CLASS_GROUP_ID_KEY, STUDENT_ID_KEY, DEPT_CODE_KEY` following the existing companion/`map`/suspend-setter pattern (cold-start role before listener fires).
- `[NEW] domain/manager/UserSessionManager.kt` (Hilt singleton): `StateFlow<UserSession>` from a `users/{uid}` snapshot listener; on `forceRefresh` flag → `getIdToken(true)` to pull fresh claims; `bind()`/`unbind()`.
- `[NEW] data/di/FirebaseModule.kt`: provide `FirebaseAuth/Firestore/Functions/Storage` + `UserSessionManager`.
- `[MOD] AuthViewModel.kt`: extract `[NEW] domain/repository/AuthRepository.kt` + `[NEW] data/remote/AuthRepositoryImpl.kt` (makes auth testable); call `sessionManager.bind()` on `Authenticated`; client-side email-format validation on sign-up.
- `[NEW] test/StudentIdentityTest.kt`: ~10 cases (valid, wrong domain, short roll, uppercase, group-id correctness).
- **✅ Gate:** new sign-up creates correct `users/{uid}`; session emits right role; cached role shows instantly on cold start, then live role.

### WAVE 3 — RBAC engine ∥ Firestore repos (2 days, two parallel tracks)
- **Track A** `[NEW] domain/manager/RolePermissionManager.kt` (`Permission` sealed interface + `hasPermission(role, permission)`), `[NEW] presentation/ui/components/RoleGate.kt` (rank-gated composable). Unit-tested across 4 roles × 8 permissions.
- **Track B** repos: `[NEW]` interfaces in `domain/repository/` + impls in `data/remote/` for `UserRepository, ClassGroupRepository, DepartmentRepository, MergeRequestRepository, AnnouncementRepository`. Realtime data via `callbackFlow { addSnapshotListener(...) ; awaitClose { reg.remove() } }` (mandatory cleanup = leak/cost guard). One config listener per class; `get()` one-shots for static dept data; `limit()`+pagination on lists. Wire all in `FirebaseModule.kt`.
- **✅ Gate:** permission tests pass; a repo `Flow` emits on console edit and stops reading when the collector cancels (verify in emulator UI).

### WAVE 4 — Exam Planner source-swap + RBAC navigation (2.5 days, highest user value)
- `[NEW] domain/repository/ExamDataRepository.kt`: single seam merging shared (Firestore: subjects, exam plan, topics, pyqs) + personal (Room: grades, attendance, topic progress).
- `[MOD] ExamPlannerViewModel.kt`: **delete seed-on-empty** (lines ~82-130); source shared data from Firestore keyed by `session.classGroupId` + `L{level}T{term}`; keep grades/progress in Room overlay; **keep existing `StateFlow` shapes** so tabs barely change. Expose `currentRole`.
- `[MOD]` tabs: `SubjectsDetailsTab` (student read-only / CR edit→Firestore), `ExamPlanTab` (read-only / CR edit), `ClassTestMarksTab` (student read **own** + PDF / CR edit all), `ExamResultsTab` **unchanged — 100% Room+Drive**. Wrap edit affordances in `RoleGate(required=CR)`.
- `[MOD] Route.kt / InAppNav.kt / NavGraph.kt`: add `Management, MergeRequests, RoleManagement, Announcements, ClassSettings` routes; show Management entry **inside Services** only when `role.rank >= CR`; collect `UserSessionManager.session` in nav.
- **✅ Gate:** student sees read-only data + no Management; CR edit appears live on a second client; CGPA tab still fully offline.

### WAVE 5 — Data seeding (1 day)
- `[NEW] functions/seed/seedDept04.ts` (or an admin-only in-app screen on Spark): idempotent `subjectCode`-keyed seed of all 8 CSE semesters + exam-plan templates. Run against emulator → export → deploy → seed prod once.
- `[NEW]` move dept-code registry to **Firebase Remote Config** (or `config/departments` doc) so new departments need no app release.
- **✅ Gate:** a fresh `CUET-04-23` account sees seeded L2T2 subjects with zero local seeding.

### WAVE 6 — Reading Room + Merge Requests (2.5 days)
- `[MOD] ReadingRoomScreen.kt`: topics/PYQs from `DepartmentRepository` (offline cache), paginated, Coil images from Storage.
- `[NEW] domain/model/MergeRequest.kt` (type/status enums + payload).
- Submit flow (student): local 🔴 draft → `MergeRequestRepository.submit()` (rules: create-only, `status==pending`, `submittedBy==uid`). Status chip 🔴→🟡→✅/❌.
- `[NEW] presentation/ui/screens/management/MergeRequestScreen.kt` (CR+): Pending/Accepted/Rejected tabs, diff view. 🔵 accept = client flips status → `onMergeRequestResolved` does privileged atomic copy. 🟢 accept = client `WriteBatch` copies into `departments/…` (guarded by dept rules).
- CT auto-archive: 🔵 `advanceSemester` Function archives+deletes; 🟢 client deletes after `PackagingSyncTab` backs up to Drive.
- **✅ Gate:** student MR → CR accept → topic appears for all students, MR ✅, submitter notified; re-running accept doesn't duplicate; reject keeps local + notifies.

### WAVE 7 — Management, announcements, FCM, Drive hardening (2 days)
- `[NEW] management/ManagementScreen.kt` (hub, gated ≥CR), `RoleManagementScreen.kt` (roster + assign CR via 🔵 `setUserRole` callable / 🟢 direct guarded write), `AnnouncementsScreen.kt` + `[MOD]` Home banner.
- `[NEW] presentation/service/NestifyMessagingService.kt`: store `fcmToken`; 🔵 server-triggered notifications for MR-resolved/announcements; 🟢 in-app polling of `mergeRequests`/`announcements` instead.
- `[MOD] GoogleDriveSyncManager.kt / PackagingSyncTab.kt`: structured layout (`personal/` + `academic/{semester}.zip`), retry/backoff, partial-sync resume; keep existing Zip-Slip guard.
- **✅ Gate:** admin promotes student→CR (claim/role refreshes, Management appears); urgent announcement reaches class; structured Drive backup/restore round-trips.

---

## Cross-cutting (continuous, not a wave)
- **Crashlytics + Analytics** from W1: sign-up parse-fail, MR funnel, sync errors, role changes.
- **Read-cost discipline:** 1 config listener/class, `get()` for static data, pagination on lists, `awaitClose{reg.remove()}` everywhere; budget vs 50k reads/day free tier.
- **CI:** GitHub Action `./gradlew assembleDebug testDebugUnitTest` + `firebase emulators:exec "npm test"`.
- **Feature flags** (Remote Config `firestore_enabled`, `management_enabled`) to dark-launch waves.

---

## Key Files

**New (Android):** `domain/model/{StudentIdentity,UserRole,UserSession,MergeRequest,Announcement}.kt`; `domain/manager/{UserSessionManager,RolePermissionManager}.kt`; `domain/repository/{Auth,User,ClassGroup,Department,MergeRequest,Announcement,ExamData}Repository.kt`; `data/remote/*Impl.kt`; `data/di/FirebaseModule.kt`; `data/local/migrations/Migrations.kt`; `presentation/ui/components/RoleGate.kt`; `presentation/ui/screens/management/{Management,MergeRequest,RoleManagement,Announcements,ClassSettings}Screen.kt`; `presentation/service/NestifyMessagingService.kt`; `test/{StudentIdentity,RolePermissionManager}Test.kt`.

**New (backend):** `firebase.json`, `firestore.rules`, `storage.rules`, `.firebaserc`, `functions/src/index.ts`, `functions/seed/seedDept04.ts`, `functions/test/rules.test.ts`.

**Modified:** root + `app/build.gradle.kts`, `libs.versions.toml`, `<Application>.kt`, `AuthViewModel.kt`, `AuthScreen.kt`, `data/datastore/SettingDatastore.kt`, `data/di/DataModule.kt`, `data/local/AppDataBase.kt`, `Route.kt`, `InAppNav.kt`, `NavGraph.kt`, `ExamPlannerViewModel.kt`, `SubjectsDetailsTab.kt`, `ExamPlanTab.kt`, `ClassTestMarksTab.kt`, `ReadingRoomScreen.kt`, `GoogleDriveSyncManager.kt`, `PackagingSyncTab.kt`, `AcademicArchiveSyncEngine.kt`, Home screen.

---

## Verification

**Per-wave gates** above are the primary checks. End-to-end before any production deploy:
1. **Build:** `./gradlew assembleDebug testDebugUnitTest`.
2. **Rules (highest ROI):** `firebase emulators:exec "npm test"` — proves wrong-role writes are rejected.
3. **Two-client realtime:** run two emulator-backed clients (one student, one CR); CR sets level/term → student data cascades; CR edit appears live.
4. **Identity:** sign up `u2304097@student.cuet.ac.bd` → correct parsed profile + `CUET-04-23` group; wrong-domain email rejected.
5. **MR loop:** student submits → CR accepts → merges for all, notifies; reject keeps local.
6. **Offline:** airplane-mode read from cache; queued write flushes on reconnect.
7. **Data safety:** install v18 build, add data, upgrade → data survives (Room migration).
8. **Drive:** structured backup/restore round-trip.

**Definition of Done (every shared-data feature):** rules cover it + an emulator test proves rejection of a wrong-role write; privileged paths run server-side (🔵) or are rules-guarded (🟢); offline verified; read cost bounded; Crashlytics/Analytics event added.

---

## Execution Summary

| Wave | Theme | Effort | Blocks |
|------|-------|--------|--------|
| W0 | Drive fix + Room migrations | 0.5d | — |
| W1 | Firebase backend + rules + emulator (+Functions 🔵) | 1.5d | W0 |
| W2 | Identity + session + auth refactor | 1.5d | W1 |
| W3 | RBAC engine ∥ Firestore repos | 2d | W2 |
| W4 | Exam Planner swap + RBAC nav | 2.5d | W3 |
| W5 | Seeding + Remote Config | 1d | W4 |
| W6 | Reading Room + Merge Requests | 2.5d | W2, W5 |
| W7 | Management + FCM + Drive hardening | 2d | W6 |

**Critical path:** W0 → W1 → W2 → W4. **Total ≈ 13.5 focused days.** The true blocker is W1 (rules + emulator), not auth — which already works.

> **Open decision deferred to implementation:** Blaze vs Spark. Plan is written Blaze-primary (🔵) with a Spark fallback (🟢) noted on every affected feature. Confirm the tier before starting W1, since it determines whether Cloud Functions or client-side equivalents get built.
