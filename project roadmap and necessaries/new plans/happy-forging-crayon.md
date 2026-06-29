# Nestify — Master Remake Plan (Phase 0 → 6)

> A ground-up reorganization of Nestify into a clean, publishable academic super-app for the
> whole university (CUET). Refactor-in-place: keep the solid bones (Room, Firestore schema, Hilt
> DI, email-identity/auth, repositories), rebuild the UI, navigation, information architecture,
> and security on top. Free-tier (Firebase Spark) for now; APK distribution first, Play later.

---

## Context — why this remake

The current app grew organically into ~40 screens with strong ideas buried under duplication,
dead stubs, and demo-only wiring. Exploration found: solid architecture (clean Kotlin + Hilt +
Room + Firestore, a well-designed Firestore schema, reliable CUET email-identity parsing) but
**not production-safe** (Firestore rules are wide open, no server enforcement, several
half-built/duplicate screens). The goal is a single, organized product the author can confidently
publish to every CUET student: same great ideas, ruthlessly consolidated, properly secured for the
free tier, and built in verifiable phases.

**Confirmed direction:** keep all 4 pillars (Academic toolkit, Class governance, Academic
network, Personal productivity); stay on Spark (free) for now; refactor in place; ship an APK to
pilot cohorts first.

---

## Product positioning & the two halves

Nestify = **one app, two clearly separated halves**:

- **Campus (shared, cloud, university-wide)** — Academic toolkit + Class governance + Network feed.
  Lives in Firestore, role-governed, the same data for everyone in a class/dept.
- **My Space (personal, offline-first)** — notes, links, files, schedules, personal projects,
  reading library, private CGPA. Lives in Room on-device; optional encrypted Google Drive backup.
  Never leaves the device except as the user's own backup.

**One source of truth per data class** is the guiding rule: shared academic data = Firestore only;
personal data = Room only. Never both for the same record.

---

## New information architecture (open redesign — ignores current nav)

**5-tab bottom bar** (center tab is the prominent uplifted button):

1. **Home** — personalized dashboard that aggregates across pillars: greeting + CGPA snapshot,
   next-exam countdown, today's schedule, latest class announcements, quick actions. The true
   landing hub.
2. **Academics** — the academic toolkit in one place: subjects & syllabus, Exam Planner, Class-Test
   marks, Results/CGPA, and the **PYQ Reading Room** (shared question bank). Sectioned/tabbed.
3. **Network** *(center, uplifted)* — the academic social feed (the connective tissue between
   batches and departments).
4. **My Space** — personal productivity hub: a clean grid into Notes, Links, Files, Schedules,
   Projects, Library.
5. **Profile** — identity & class info, roster access, settings, sign-out, and a **role-gated
   "Class Management"** entry (governance tools live here, not as a tab).

**Class governance** is woven in contextually: announcements surface on Home and in a Class view;
management actions (post announcement, review merge requests, manage roles, set class level/term)
sit behind the role-gated Profile → Class Management entry. Search becomes a real global search
(not a dead screen). Bookmarks/Archive/Favorites become **filters**, not separate screens.

---

## What to KEEP, CONSOLIDATE, and CUT (the "bulgur")

**Keep & polish (core):** Auth, Home dashboard, Exam Planner + tabs (Subjects, CT Marks, Results,
Reading Room, Exam detail), Notes, Links, Files/Folders, Schedules, Profile, Settings, Management
hub (merge requests, role management, announcements), Network feed.

**Consolidate (rework/merge):**
- Project planning has **two parallel implementations** (`presentation/ui/screens/ProjectPlanner/*`
  and `com/nhbhuiyan/nestify/projectplans/*`) → keep one. Merge "MyProjects" (showcase) +
  "ProjectPlanner" (planning) under a single **Projects** area in My Space.
- Bookmarks / Archive / Favorites screens → replace with bookmark/archive **filters** on the
  existing content lists.
- Exam Planner's stub helpers (`AcademicGradingEngine`, `AcademicPdfGenerator`,
  `AcademicArchiveSyncEngine`, `GoogleDriveSyncManager`) → either finish and wire in (CGPA/results
  use them) or drop; decide per-phase, no orphaned classes left.

**Cut (dead/filler):**
- Dead placeholder screens: `ArchiveScreen`, `FacouritesScreen`, `SearchScreen` (rebuilt as
  filters/real search).
- Scratch files: `presentation/ui/screens/test.kt`, `test2.kt`, `test3.kt`.
- Obsolete `HomeScreen/components_older/` folder (live screen uses `home.data.HomeState`).
- `ServiceScreen` (marketing filler; the 4 fake services are already removed) → delete; Management
  moves to Profile.
- Already-deleted Gallery; unused `ProfileScreen/PdfGenerator.kt`.

---

## Architecture & backend (free-tier reality)

Because we stay on **Spark**, there are **no Cloud Functions**, so server-side enforcement is
limited. The security backbone becomes **strict Firestore Security Rules** plus the existing
**client-side identity bootstrapping** (already in `AuthRepositoryImpl`: parse CUET email →
create `users/{uid}` + roster binding). Design accordingly:

- **Firestore schema:** keep as-is (it's well-modeled): `users/{uid}`,
  `classGroups/{gid}/(roster|announcements|mergeRequests|ctMarks)`,
  `departments/{dept}/semesters/{sem}/subjects/{code}/topics/{id}/pyqs/{id}`.
- **Security rules (the big rewrite):** replace the open `allow read,write: if true` with RBAC:
  - `users/{uid}`: read = signed-in; write = owner, **but role/classGroupId/studentId immutable by
    client** (prevents self-promotion).
  - `classGroups/{gid}` config + `roster/*`: read = signed-in; roster docs **create-once** (no
    overwrite → roll-number squatting guard); level/term writable only by CRs of that class.
  - `announcements/*`: read = class members; create/update = CR+ of that class.
  - `mergeRequests/*`: create = any class member (own submission); update→accepted/rejected = CR+.
  - `ctMarks/{sem}/{studentId}`: read = owner or CR+; write = CR+ of that dept/class.
  - `departments/**`: read = signed-in; write = CR+/admin (validated by role field on the user doc).
  - Validate shapes (no empty announcements, CT marks within range, required fields present).
- **Merge-request apply on Spark:** without `onMergeRequestResolved`, the **CR's client** performs
  the merge write into `departments/**` when accepting (rules permit CR+ writes). Acceptable for
  free tier; note Blaze upgrade later makes it atomic/server-side.
- **Notifications on Spark:** no server FCM push. Use an **in-app notification center** backed by
  Firestore listeners (announcements, MR status, role changes) — cheap and effective. FCM push is a
  Blaze-era upgrade.
- **Room:** turn on `exportSchema`, write **explicit migrations**, remove
  `fallbackToDestructiveMigration` (prevents user data loss on update). Fix the
  `ExamDataRepositoryImpl.getSubjectsFlow` race (don't emit before the async Room insert completes).
- **Session/security hardening:** encrypted DataStore for cached session; enforce `pendingReview`
  (collision) state in rules + UI; `forceRefreshClaims` not needed (role read from Firestore doc,
  not custom claims, on free tier).
- **Cost guardrails:** one snapshot listener per class config doc; paginate feed/announcements;
  cache in Room; stay within the 50k reads/day free budget for a pilot cohort.

> **Blaze is the documented future upgrade** (Phase 6+ / post-pilot): Cloud Functions for
> `onUserCreate`, `setUserRole`, atomic `onMergeRequestResolved`, and FCM push. The free-tier
> design must not block that later move (keep the `functions/` code, wire it when on Blaze).

---

## Design system (do this once, reuse everywhere)

Establish a small, consistent foundation before rebuilding screens (reuse existing
`ui/theme/Color.kt`, `NestifyGradients`, and the UI principles in `UI_PROMPT_INSTRUCTIONS.txt`):
- Tokens: colors, spacing (8/16/24), radii (16/20/24), elevation, typography scale.
- Reusable components: `NestifyScaffold`, section header, content card, role badge/chip,
  empty/loading/error states, avatar, primary button/FAB. Every feature must handle
  empty/loading/error.

---

## Phased roadmap (Phase 0 → 6)

Each phase ships **UI + backend together** and ends in a verifiable state (compiles, runs, smoke
test passes). Use Firebase Remote Config / a simple flags object to dark-launch risky phases.

### Phase 0 — Foundation & de-bulgar (stabilize the base)
- Delete dead code & duplicates (Archive/Favorites/Search stubs, `test*.kt`, `components_older/`,
  `ServiceScreen`, redundant project-planner impl, unused PdfGenerator).
- Room: `exportSchema=true`, explicit migrations, drop destructive fallback.
- Fix `ExamDataRepositoryImpl` race condition.
- Build the **design system** + the **new 5-tab nav skeleton** (empty Home/Academics/Network/My
  Space/Profile shells wired into `InAppNav`/`Route`).
- **Verify:** app builds, launches to the new shell, no orphaned references.

### Phase 1 — Identity, access & security (the multi-user gate)
- Rewrite `firestore.rules` to the RBAC matrix above; rewrite/confirm `storage.rules`.
- Harden client auth/identity: enforce `pendingReview` collisions in UI; encrypted session cache;
  immutable role fields; sign-in/up/Google flows reviewed against the new rules.
- Role gating via existing `RoleGate` + `RolePermissionManager` (UI), rules as the real authority.
- **Verify:** with the Firestore emulator/rules tests, a student cannot read others' CT marks,
  cannot self-promote, cannot post announcements; a CR can. Login works for a seeded student vs CR.

### Phase 2 — Academics pillar
- Rebuild the **Academics** tab over the existing hybrid repos: subjects & syllabus, Exam Planner,
  CT marks, Results/CGPA (wire `AcademicGradingEngine` or compute inline), PYQ **Reading Room**.
- Polish to design-system quality; fix the partial/stub tabs.
- **Verify:** seeded subjects/topics/PYQs render; CT marks read per-rules; CGPA computes; exam
  countdowns show on Home.

### Phase 3 — Class governance + notifications
- Roster view; announcements (read for all, post for CR+); merge-request **submit** (student) and
  **review/apply** (CR, client-side merge per Phase-1 rules); role management; class level/term.
- **In-app notification center** (Firestore-listener backed).
- **Verify:** student submits a PYQ → appears in CR review queue → CR accepts → it lands in
  `departments/**` and shows in Reading Room; announcement from CR appears in students' Home + notif.

### Phase 4 — Academic Network (feed) — make it real
- Promote the existing `AcademicFeed` UI shell to a Firestore-backed feature: a `posts` collection
  (author, type, title, body, tags, timestamps, like/comment counts), compose-post sheet, category
  filters, lightweight like/comment, basic moderation (author delete; CR/admin remove).
- **Verify:** a post created on one account appears on another; filters work; counts persist.

### Phase 5 — My Space (personal productivity)
- Consolidate Notes, Links, Files/Folders, Schedules, Projects, Library under the **My Space** tab
  with a clean grid hub. Bookmarks/Archive/Favorites as filters. Real global **Search**. Optional
  encrypted **Drive backup** for personal + academic archives.
- **Verify:** CRUD works offline; search spans notes/links/files; backup/restore round-trips.

### Phase 6 — Polish, QA & publish (APK pilot → wider)
- Empty/loading/error/accessibility/perf passes; remove demo seeding from production builds; APK
  signing config; privacy policy; content rating prep.
- **Pilot:** ship signed APK to one class, gather feedback, watch Firestore usage vs free budget.
- Then widen the APK rollout; queue Play Store + Blaze upgrade (Cloud Functions + FCM) as the next
  milestone.
- **Verify:** clean install on a fresh device; a real student completes sign-up → academics →
  feed → personal flow end-to-end.

---

## Critical files (representative, not exhaustive)

- Nav/IA: `presentation/navigation/InAppNav.kt`, `navigation/Components/Route.kt`,
  `navigation/Components/bottomNavigation.kt`.
- Security: `firestore.rules`, `storage.rules` (Phase 1 rewrite); `functions/src/index.ts` (kept for
  the Blaze-era upgrade).
- Identity/session: `data/repository/AuthRepositoryImpl.kt`, `domain/manager/UserSessionManager.kt`,
  `domain/model/StudentIdentity.kt`, `data/datastore/SettingDatastore.kt`.
- Academic data: `data/repository/ExamDataRepositoryImpl.kt` (fix race),
  `DepartmentRepositoryImpl.kt`, `data/local/AppDataBase.kt` (migrations) + `Dao/ExamPlannerDao.kt`.
- Feed: `presentation/ui/screens/AcademicFeed/AcademicFeedScreen.kt` (exists; back it with Firestore).
- Theme/design: `ui/theme/Color.kt`, `NestifyGradients`, `UI_PROMPT_INSTRUCTIONS.txt` (principles).

## Verification (end-to-end)
- After every phase: `./gradlew :app:compileDebugKotlin` then `:app:assembleDebug`.
- Security: Firestore rules unit tests / emulator (student-vs-CR matrix from Phase 1).
- Manual smoke per phase using seeded accounts (CR `u2104001@…`, student `u2104005@…`, password
  `123456`): identity → academics → governance → feed → my-space.
- Pilot metric: stay within Spark's ~50k reads/day for the test cohort.

## Out of scope (now) / future
- Blaze upgrade: Cloud Functions (atomic merges, `setUserRole`, `onUserCreate`), FCM push.
- Faculty/teacher (`@cuet.ac.bd`) roles; multi-university tenancy; AI study suggestions; analytics;
  gamification — all explicitly deferred to post-pilot.
