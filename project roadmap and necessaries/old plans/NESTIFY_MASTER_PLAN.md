# 🎓 Nestify — University Student Ecosystem
## Master Architecture & Implementation Plan (v2 — Production-Grade)

> Supersedes the v1 vision doc. This version is **grounded in the actual codebase state** (audited 2026-06-28) and adds the layers v1 was missing: **server-side enforcement, offline-first data flow, security rules, identity bootstrapping, cost/scale, and testing**. The product vision is unchanged; the engineering is hardened.

---

## 0. Ground Truth — What Already Exists (Audited)

Before planning new work, here is the **verified current state**. Several v1 assumptions were wrong; planning against reality saves days.

| Area | v1 Assumed | Actual State | Implication |
|------|-----------|--------------|-------------|
| **Firebase Auth** | "to be added" | ✅ **Live.** `AuthViewModel` uses `FirebaseAuth.getInstance()`, supports email/password + Google Sign-In. `google-services.json` present. | Module 1 auth plumbing is ~50% done. Focus shifts to **identity parsing + profile creation + role bootstrapping**, not auth itself. |
| **Firestore** | "add dependency" | ❌ **Not added.** Only `firebase-auth-ktx` + Drive API libs are present. | Real first step of the data layer. |
| **Firebase Storage** | implied (PDFs) | ❌ Not added. | Needed for PYQ images / CT PDFs. |
| **Cloud Functions** | implied (archive, claims) | ❌ Not set up. | **Required** for custom claims + secure role assignment + CT auto-archive. |
| **Security Rules** | ⚠️ **Never mentioned** | ❌ Default (locked or test-mode). | **This is the #1 risk.** Without rules, the entire RBAC matrix is decorative — any student can write any document via the SDK. |
| **Hilt DI** | "to be wired" | ✅ Fully wired. `DataModule`, `DomainModule`, `ViewmodelModule`. | New repos slot in cleanly. |
| **Room DB** | v? | ✅ **v18**, `exportSchema = false`, 18 entities incl. new `PYQEntity`. No migration files. | Migration debt is real (see §9). |
| **ExamPlanner data** | "hardcoded subjects" | Room-backed via `ExamPlannerDao` (21 methods). Hardcoded values are **seed-on-empty only**. | Migration to Firebase = swap the source behind the repo, not a rewrite. |
| **Drive sync** | folder mismatch bug | ✅ **Confirmed bug.** Root folder = `"Nestify"`, but `uploadToDrive()` searches `"Nestify Backups"`. Comprehensive sync exists (Moshi JSON + ZIP packaging + Zip-Slip guard). | 10-min fix, do first. |
| **DataStore keys** | add role keys | Has `DARK_THEME, BIOMETRIC, SYNC, FONT_SIZE, DEFAULT_LEVEL, DEFAULT_TERM`. | Add identity/role cache keys here. |
| **Navigation** | 5-tab bottom nav | Confirmed: Home / Gallery / Library / Services / Profile. `NavGraph` already gates on `FirebaseAuth`. | Management entry should be a **Services sub-item or Profile section**, not a 6th bottom tab (keeps thumb-reach UX). |

---

## 1. The Three Architectural Pillars v1 Was Missing

These are the load-bearing decisions that turn the vision into a real system. Everything else depends on them.

### Pillar A — Server-Side Authority via Custom Claims + Security Rules

> **Client-side role checks (`RoleGate`, hidden tabs) are UX only. They do not secure anything.** Security must live where the client cannot tamper with it.

**Mechanism:**
1. Role is stored in **two** places: `users/{uid}.role` (for UI/queries) **and** as a **Firebase Auth custom claim** (for security rules — read with zero extra Firestore reads).
2. A **Cloud Function** (`setUserRole`) is the *only* way to write a custom claim. It verifies the caller is `admin`/`superadmin` before granting. Clients can **never** self-promote.
3. **Firestore Security Rules** enforce the entire permission matrix server-side using `request.auth.token.role` and `request.auth.token.classGroupId`.

**Why custom claims, not just a Firestore role doc:** reading the role doc inside a rule costs a `get()` (latency + billing + recursion limits). Claims are in the JWT — free, instant, and can't be faked.

**Trade-off:** claims propagate on token refresh (~1 hr, or force-refresh on role change). Acceptable — role changes are rare. On promotion, the Function sets the claim and writes a `forceRefresh: true` flag the client listens for to call `getIdToken(true)`.

### Pillar B — Offline-First with a Single Source of Truth (no double-caching)

> v1 said "Firebase + keep Room as cache." Doing both naively means **two caches that disagree**. Pick one cache per data class.

**The rule:** *Personal data* and *Shared data* use **different** persistence strategies — never the same row in two databases.

| Data class | Source of truth | Cache / offline | Why |
|-----------|-----------------|-----------------|-----|
| Notes, Links, Schedules, Projects, Profile, **CGPA/Grades**, syllabus *progress* (isCompleted/isRevised/priority) | **Room** (local) | Drive backup | Personal. Never leaves device except as backup. |
| Subjects, Exam Plans, Topics, PYQ content, CT marks, Announcements, MRs | **Firestore** | **Firestore SDK offline persistence** (`setPersistenceEnabled(true)`) | Shared. Let the SDK handle the cache — do **not** also mirror into Room. |

This eliminates the merge-conflict class entirely. Room stays for personal; Firestore's built-in disk cache covers offline for shared. The only "overlay" is **personal progress on shared topics** (e.g. *I* completed topic X) — that stays in Room keyed by the Firestore `topicId`.

### Pillar C — Identity & Role Bootstrapping (the chicken-and-egg v1 ignored)

> "CR sets the level/term," "Admin assigns CR" — but **who creates the first admin?** And what stops user #2 from claiming Roll 097?

**Bootstrapping sequence:**
1. **Super Admin** = your UID, hardcoded in **Security Rules** (`request.auth.uid == "YOUR_UID"`) *and* seeded with the claim via a one-time admin script. Not transferable by default (answer to Q2).
2. On first sign-up, a Cloud Function (`onUserCreate` Auth trigger) parses the email **server-side**, creates `users/{uid}` with `role: "student"`, and sets the student claim. **Client-supplied role is ignored.**
3. **Roll-number squatting guard:** the function checks `classGroups/{groupId}/roster/{rollNumber}` — if already bound to a different uid, sign-up is flagged `pendingReview` (collision). Email domain `@student.cuet.ac.bd` is enforced in the function *and* in rules.
4. Super Admin promotes the first General Admins → they promote CRs. All via `setUserRole` Function.

---

## 2. Corrected Firestore Data Model

v1 had a path inconsistency (`departments/{dept}/subjects/{semId}` vs `departments/{dept}/semesters/{semId}/subjects`). Standardized below. Also **denormalized for read-cost** (132 students × realtime listeners = read amplification).

```
users/{uid}
  ├─ email, studentId, batchYear, departmentCode, rollNumber
  ├─ classGroupId, role, displayName, photoUrl
  ├─ fcmToken, createdAt, updatedAt
  └─ pendingReview: bool            // roll collision flag

classGroups/{groupId}              // e.g. "CUET-04-23"
  ├─ currentLevel, currentTerm     // single doc → 1 listener feeds whole class
  ├─ crList: [uid...], adminList: [uid...]
  ├─ roster/{rollNumber} → { uid, displayName }   // squatting guard + class list
  ├─ ctMarks/{semesterId}/{studentId} → { CSE-221: {ct1..ct4, attendance} }
  ├─ mergeRequests/{mrId} → { type, target*, data, submittedBy, submitterName,
  │                            status, reviewedBy, reviewNote, submittedAt, reviewedAt }
  └─ announcements/{id} → { title, body, createdBy, createdAt, priority }

departments/{deptCode}             // e.g. "04"
  ├─ name, totalStudents
  └─ semesters/{semesterId}        // "L2T2"
       ├─ subjects/{subjectCode} → { name, credits, examDate }
       └─ subjects/{subjectCode}/topics/{topicId}
            ├─ title, section
            └─ pyqs/{pyqId} → { questionText, answerText, *ImageUrl,
                                 repeatCount, yearsSeen, marks, contributedBy }
```

**Key modeling decisions:**
- **`classGroups/{groupId}` config is a single document.** One snapshot listener per client drives the whole level/term cascade. Cheap.
- **CT marks keyed by `studentId` subdocument** so rules can enforce "read only your own row" cheaply.
- **Subjects keyed by `subjectCode`** (not auto-id) — deterministic, idempotent seeding, no dup subjects.
- **Storage layout** mirrors Firestore: `pyq_images/{deptCode}/{semesterId}/{topicId}/{pyqId}_{q|a}.webp`. Compress to WebP client-side before upload (bandwidth + cost).

---

## 3. Firebase Security Rules (the missing keystone — design)

This is **new work v1 omitted entirely.** Skeleton to implement in Phase 0/1:

```
// helpers
function role()        { return request.auth.token.role; }
function myGroup()     { return request.auth.token.classGroupId; }
function isCRplus()    { return role() in ['cr','admin','superadmin']; }
function isAdminPlus() { return role() in ['admin','superadmin']; }

match /users/{uid} {
  allow read: if request.auth != null;                 // class roster visible
  allow create: if request.auth.uid == uid;            // own doc only
  allow update: if request.auth.uid == uid             // edit own profile fields…
                && !('role' in request.resource.data.diff(resource.data).affectedKeys()); // …but NOT role
  // role changes only via setUserRole Cloud Function (admin SDK bypasses rules)
}

match /classGroups/{gid} {
  allow read: if myGroup() == gid;
  allow update: if myGroup() == gid && isCRplus()      // level/term cascade
                && onlyAffects(['currentLevel','currentTerm']);

  match /ctMarks/{sem}/{studentId} {
    allow read:  if myGroup() == gid && (isCRplus() || request.auth.uid == studentId);
    allow write: if myGroup() == gid && isCRplus();
  }
  match /mergeRequests/{mrId} {
    allow create: if myGroup() == gid && request.resource.data.submittedBy == request.auth.uid
                  && request.resource.data.status == 'pending';   // can't self-accept
    allow read:   if myGroup() == gid;
    allow update: if myGroup() == gid && isCRplus();               // only reviewers resolve
  }
  match /announcements/{id} {
    allow read:  if myGroup() == gid;
    allow write: if myGroup() == gid && isCRplus();
  }
}

match /departments/{dept}/{document=**} {
  allow read:  if request.auth != null;
  allow write: if isCRplus() && request.auth.token.departmentCode == dept;  // CRs edit own dept
}
```

> **Grades are intentionally absent** — they never touch Firestore (Pillar B). Storage rules mirror these (PYQ images writable by CR+, readable by group members).

---

## 4. Revised Module Plan (grounded deltas only)

I keep your 8 modules but rewrite each as **delta from actual state**, with the new server-side work folded in.

### Module 0 — Foundations (NEW, blocking, ~1 day)
- Add deps: `firebase-firestore-ktx`, `firebase-storage-ktx`, `firebase-messaging-ktx`, `firebase-functions-ktx`, `firebase-crashlytics`. Enable Firestore offline persistence in `Application`.
- Init **Firebase Emulator Suite** (Firestore + Auth + Functions + Storage) — local dev without touching prod, and the only sane way to test rules. Add `firebase.json`, `firestore.rules`, `storage.rules`.
- Scaffold **Cloud Functions** project (`functions/`, TypeScript): `onUserCreate`, `setUserRole`, `advanceSemester` (CT archive), `onMergeRequestResolved`, `notify*`.
- Fix the **Drive folder bug** (`"Nestify Backups"` → `"Nestify"`) — your P0 quick win.

### Module 1 — Identity & Session (auth exists; build identity on top)
- `domain/model/StudentIdentity.kt` — pure parser (`u2304097@...` → batch/dept/roll/groupId). **Unit-tested** (the one piece of pure logic — test it hard, incl. malformed emails).
- `onUserCreate` Function does authoritative profile creation + claim + roster bind + collision check. The client **reads** the profile; it does not create the role.
- `UserSessionManager` (Hilt singleton) exposes `StateFlow<UserSession>` from a `users/{uid}` snapshot listener + cached claims. Cache role/groupId/studentId in DataStore for cold-start UI before the listener fires.
- `AuthViewModel`: refactor to inject an `AuthRepository` interface (currently calls `FirebaseAuth` directly — untestable). Add `getIdToken(true)` refresh on `forceRefresh` flag.

### Module 2 — Firestore Data Layer
- Repo interfaces in `domain/repository/`, impls in `data/remote/`: `UserRepository`, `DepartmentRepository`, `ClassGroupRepository`, `MergeRequestRepository`, `AnnouncementRepository`.
- Expose Firestore as `Flow` via `callbackFlow { snapshotListener }`. **Detach listeners in `onCleared`** (leak/cost guard).
- `FirebaseModule.kt` DI. **Read-cost discipline:** one listener on `classGroups/{gid}` config; subjects/topics fetched with `get()` (one-shot) on level/term change, not perpetual listeners.

### Module 3 — RBAC Engine (client-side UX layer)
- `UserRole` enum, `Permission` sealed class, `RolePermissionManager` — exactly as v1, but framed as **UX gating only**. The authority is §3 rules. `RoleGate` composable hides controls; rules reject if bypassed.

### Module 4 — Exam Planner Overhaul (swap source behind repo)
- Introduce `ExamDataRepository` merging **Firestore (shared: subjects, plans, topics, CT marks)** + **Room (personal: grades, progress, CT archive)**. The existing `ExamPlannerViewModel` keeps its `StateFlow` shape — only the upstream changes.
- **Subjects/Exam Plan/Topics/PYQs:** Firestore-backed; Room overlay only for personal progress keyed by `topicId`/`subjectCode`.
- **CT Marks:** Firestore current term (CR write, student read-own via rules). Archive on semester advance.
- **CGPA/Results:** **stays 100% Room + Drive.** Never syncs (Pillar B). Keep `ExamResultsTab` as-is.
- Delete the seed-on-empty hardcoded subjects — replaced by Firestore seeding (Module 2 Phase 2).

### Module 5 — Reading Room + Merge Requests
- Topics/PYQs read from Firestore (offline cache). Student "Add" = local draft (🔴) → `mergeRequests/{mrId}` (create-only per rules).
- **Accept is a Cloud Function (`onMergeRequestResolved`), not a client batch.** Client only flips `status`; the Function does the privileged copy into `departments/...` atomically + idempotently (re-run safe) + FCM-notifies submitter. This keeps the privileged write server-side.

### Module 6 — Management Dashboard
- Entry point: **Services tab sub-section** (not a 6th bottom tab — preserves UX). Visible only when `role >= CR`.
- Sub-screens: Merge Requests (review), Role Management (calls `setUserRole`), Announcements, Class Settings (level/term selector → single config write → cascade).

### Module 7 — Drive Sync hardening
- P0 folder-name fix (done in Module 0). Then structured backup (`personal/` + `academic/{semester}.zip`), retry/backoff, partial-sync resume. Keep the existing Zip-Slip guard.

### Module 8 — Navigation & Auth UI
- RBAC-aware nav (role-gated Management). Sign-up screen: live email-format validation + parsed identity preview ("Batch 2023 | CSE | Roll 097") + `@student.cuet.ac.bd` restriction (mirrors server enforcement).

---

## 5. Cross-Cutting Concerns v1 Never Addressed

| Concern | Plan |
|---------|------|
| **Cost / read amplification** | 132 clients × listeners. Use **one** config listener; one-shot `get()` for static data; denormalize; bound PYQ queries with pagination (`limit(20)` + `startAfter`). Budget against Firestore free tier (50k reads/day). |
| **Image/PDF storage** | Firebase Storage, WebP compression client-side, storage rules mirroring Firestore, lazy load with Coil. |
| **FCM notifications** (Q5) | Yes — but **server-triggered only** (Functions on MR submit/resolve + announcement create). No client-to-client. Store `fcmToken` on `users/{uid}`. |
| **Offline conflict** | Eliminated by Pillar B (no dual-write). Firestore last-write-wins is fine for CR-authored shared data (single writer class). |
| **Testing** | Unit: `StudentIdentity` parser, `RolePermissionManager`, `AcademicGradingEngine`. **Rules tests** via `@firebase/rules-unit-testing` on emulator (the highest-ROI tests — they prove the security model). Instrumented: nav role-gating. |
| **Observability** | Crashlytics + Analytics (track sign-up parse failures, MR funnel, sync errors). |
| **Dept registry expansion** | Move dept-code map to **Firebase Remote Config** (or a `config/departments` doc) so new departments don't need an app release. |
| **CI** | `./gradlew assembleDebug` + unit tests + `firebase emulators:exec "npm test"` for rules on each PR. |
| **Feature-flag rollout** | Remote Config flags (`firestore_enabled`, `management_enabled`) to dark-launch modules without breaking current users. |

---

## 6. Room Migration Debt (answer to Q4 — address NOW, cheaply)

Current: v18, `exportSchema = false`, no migration files. If `fallbackToDestructiveMigration` is in use, **every schema bump wipes student grades/notes** — unacceptable once real data exists.

**Plan:** set `exportSchema = true` (commit the JSON schemas), write explicit `Migration(18→19)` for the `PYQEntity` addition and onward, drop destructive fallback. One-time ~2 hr cost; prevents silent data loss. Do it before any further entity change ships.

---

## 7. Answers to v1's Open Questions (firm recommendations)

| Q | Recommendation |
|---|---------------|
| **Q1 — email scope** | Restrict to `@student.cuet.ac.bd` now (enforced in `onUserCreate` + rules). Add `@cuet.ac.bd` (teacher) as a **separate role path** later — don't block on it. |
| **Q2 — Super Admin** | Hardcode your UID in rules + seed claim once. **Non-transferable** initially. Add a `transferSuperAdmin` Function behind a confirmation flow only if a real handoff need arises. |
| **Q3 — Firestore vs RTDB** | **Firestore.** Your data is relational/hierarchical with per-document security needs (own-CT-marks, role-scoped writes) that RTDB rules express far more awkwardly. Worth the slightly higher complexity. |
| **Q4 — Room migrations** | **Yes, now.** See §6. Cheap insurance against catastrophic data loss. |
| **Q5 — FCM** | **Yes**, server-triggered via Functions (§5). In-app polling wastes reads and battery. |
| **Q6 — start order** | Drive fix (10 min) → **Module 0 Foundations** → Module 1 Identity. Don't start UI modules before rules + emulator exist, or you'll build on sand. |

---

## 8. Optimized Execution Order

| Wave | Work | Why this order | Effort |
|------|------|----------------|--------|
| **W0** | Drive folder fix · Room migrations (§6) | Zero-dependency quick wins, stop data loss | 0.5 day |
| **W1** | Module 0 Foundations (deps, emulator, Functions scaffold, **rules skeleton**) | Everything downstream depends on the data layer + the security substrate | 1–1.5 day |
| **W2** | Module 1 Identity (`onUserCreate`, parser+tests, `UserSessionManager`, auth refactor) | Unlocks role-aware everything | 1.5 day |
| **W3** | Module 2 Firestore repos + Module 3 RBAC engine | Repos + permission layer in parallel (independent) | 2 day |
| **W4** | Module 4 Exam Planner (source swap) + Module 8 nav | Highest user value; depends on W2–W3 | 2.5 day |
| **W5** | Module 2 Phase 2 seeding (emulator import → prod) | Needs schema stable from W4 | 1 day |
| **W6** | Module 5 Reading Room + MR (incl. `onMergeRequestResolved`) | Most complex; needs identity + rules + seeding | 2.5 day |
| **W7** | Module 6 Management + Module 7 Drive hardening + FCM | Polish + ops | 2 day |

**Critical path:** W0 → W1 → W2 → W4. Rules + emulator (W1) are the true blocker, not auth.

---

## 9. Definition of Done (per security-sensitive feature)

A shared-data feature is **not done** until:
1. Firestore/Storage rules cover it **and** have an emulator test proving a wrong-role write is rejected.
2. The privileged path (role assign, MR accept, CT archive) runs in a **Cloud Function**, never a client batch.
3. Offline behavior is verified (airplane-mode read from cache, queued write on reconnect).
4. Read cost is bounded (no unbounded listener; pagination on lists).
5. Crashlytics breadcrumb + Analytics event added for the funnel.

---

### TL;DR — what changed from v1
1. **Added the missing keystone:** Security Rules + Custom Claims + Cloud Functions = real server-side RBAC (v1's matrix was client-only).
2. **Resolved the caching contradiction:** one source of truth per data class (Room *or* Firestore offline, never both).
3. **Solved identity bootstrapping:** server-side `onUserCreate`, roll-squatting guard, hardcoded super-admin seed.
4. **Grounded in reality:** auth already exists; Firestore/Storage/Functions/rules do not — re-sequenced around the true blocker.
5. **Added the professional layer:** cost/scale, testing (esp. rules tests), migrations, FCM-via-Functions, Remote Config, CI, feature flags, DoD.
