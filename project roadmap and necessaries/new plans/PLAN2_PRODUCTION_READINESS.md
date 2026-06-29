# Nestify — Plan 2: Production-Readiness & University-Scale Feature Plan

> **Status:** forward-looking (next stage, after the BrainSton UI remake Phases A–F).
> **Goal:** evolve Nestify from a working pilot into an industry-grade, multi-role, university-wide
> platform — robust identity, a real Academic Network, enforced role hierarchy, clean local/remote
> data separation, and the operational/security maturity needed to ship to thousands of students.

This plan is organised as **epics** (E1…E9). Each has *Why → What → Design → Acceptance*. Epics are
mostly independent but ordered by dependency: identity & data-architecture first, then features that
sit on top.

---

## 0. Where we are today (baseline — do not re-derive)
- **Identity:** single FirebaseAuth user → `users/{uid}` Firestore doc → `UserSession`
  (uid/email/role/classGroupId/departmentCode/rollNumber/displayName/photoUrl), kept live by
  `UserSessionManager` (snapshot listener). Email + Google sign-in via `AuthRepository`
  (`signInWithEmail`/`signUpWithEmail`/`signInWithGoogle`/`signOut`). CUET email shape:
  `uXXYYZZZ@student.cuet.ac.bd`.
- **Roles:** `UserRole` = PENDING(0) < STUDENT(1) < CR(2) < ADMIN(3) < SUPERADMIN(4). Capability
  checks via `RolePermissionManager.hasPermission(role, Permission)` + `RoleGate`/`PermissionGate`
  composables. `Permission` covers EditSubject/EditExamPlan/View+EditCtMarks/ResolveMergeRequest/
  PostAnnouncement/ManageRoles.
- **Shared academic data (remote, Firestore):** `departments/{dept}/semesters/{sem}/subjects/{code}/
  topics/{id}/pyqs/{id}` (the "universal data box"); `classGroups/{groupId}/{roster, announcements,
  ctMarks, mergeRequests, posts}`.
- **Personal data (local, Room v20):** notes, links, files/folders, media, library, schedules,
  projects/plans, profile, and a mirrored exam-planner cache. Some entities already carry a
  `firestoreId` (groundwork for remote sync).
- **Merge requests:** `classGroups/{groupId}/mergeRequests`; submit/get/resolve; **client-side apply**
  on approval in `ManagementViewModel` (Spark plan — no Cloud Functions yet). Types: subject/topic/pyq.
- **Network feed (Phase D):** `classGroups/{groupId}/posts` — real Firestore feed, compose sheet,
  category filters, like/delete, CR/admin moderation.
- **Platform:** Firebase **Spark** (free) — Firestore + Auth + offline persistence; no Cloud
  Functions / FCM yet. `minSdk 28`, Compose, Hilt, Room.

---

## E1 — Identity & multi-account (student-primary + linked Gmail)
**Why:** Students sign up with a verified university address, but want to use the app from a personal
Google account too (different phone, no university mail app, convenience). We must keep the *academic
identity* anchored to the university email while allowing a second login that resolves to the **same**
account.

**What**
- **Sign-up is university-email-only.** The first registration MUST use the institutional domain
  (`@student.cuet.ac.bd`, configurable allow-list). Enforce in `AuthRepositoryImpl.signUpWithEmail`
  (regex/domain check) **and** in Firestore Security Rules (defence in depth). Non-domain sign-ups are
  rejected with a clear message.
- **Email verification** required before a `PENDING` user is promoted to `STUDENT` (gate the app behind
  `emailVerified` + roster match).
- **Link a secondary Google account** (NOT a separate account): use Firebase **account linking**
  (`currentUser.linkWithCredential(googleCredential)`) so one `uid` has both providers. After linking,
  the student can sign in with *either* the university email or the personal Gmail and land on the same
  profile, role, and data.
  - Store on `users/{uid}`: `primaryEmail` (institutional, immutable), `linkedEmails: [..]`,
    `providers: ["password","google.com"]`.
  - Settings → "Connected accounts" screen: show primary (locked) + linked, add/remove a Google login,
    re-auth before sensitive changes.
- **Identity model in code:** keep `UserSession` as the single source of truth (uid unchanged across
  providers). Add `linkedEmails`/`providers` to `UserSession` + `users/{uid}`. `UserSessionManager`
  already rebinds on the user doc — extend it to read the new fields.

**Design notes**
- Linking conflict (the Gmail already belongs to another Firebase user) → surface a merge/contact-support
  path; never silently overwrite.
- Re-authentication: linking/unlinking and role-sensitive actions require a recent sign-in
  (`reauthenticate`) to satisfy Firebase security expectations.

**Acceptance**
- Sign-up rejected for non-institutional emails (client + rules).
- A student links a personal Gmail in Settings; signing out and back in with that Gmail restores the
  same uid/role/profile/data; unlinking works and is blocked for the primary provider.

---

## E2 — Role hierarchy, role-wise control & the permission matrix
**Why:** A university app is fundamentally an authorization product. Today the rank ladder exists but
permissions are coarse and enforced **only client-side**. Production needs a single, auditable,
server-enforced matrix.

**What — the canonical capability matrix** (extend `Permission` + `RolePermissionManager`):

| Capability | STUDENT | CR | ADMIN | SUPERADMIN |
|---|---|---|---|---|
| View own academics, feed, personal data | ✅ | ✅ | ✅ | ✅ |
| Create/edit **own** posts, notes, links, files | ✅ | ✅ | ✅ | ✅ |
| Submit a **merge request** (propose academic data) | ✅ | ✅ | ✅ | ✅ |
| Like/comment on posts | ✅ | ✅ | ✅ | ✅ |
| Moderate (remove) others' posts in own class | — | ✅ | ✅ | ✅ |
| Post class **announcements** | — | ✅ | ✅ | ✅ |
| Edit subjects / exam plan / PYQs (direct) | — | ✅ | ✅ | ✅ |
| View/edit class **CT marks roster** | — | ✅ | ✅ | ✅ |
| **Resolve** merge requests (own class) | — | ✅ | ✅ | ✅ |
| Promote/demote **STUDENT↔CR** (own class) | — | — | ✅ | ✅ |
| Manage **ADMIN** role / cross-class | — | — | — | ✅ |
| Manage departments / global config / feature flags | — | — | — | ✅ |
| View audit log | — | own class | own dept | global |

**Role-wise "jobs to be done":**
- **Student:** consume academics + feed; contribute via merge requests; manage personal workspace; can
  earn CR/ADMIN by promotion.
- **CR (Class Representative):** the class operator — announcements, approve/curate class academic data
  (subjects/exam-plan/PYQs/CT marks), moderate the class feed, resolve merge requests for the class.
- **ADMIN (dept/faculty):** everything a CR can, plus appoint/revoke CRs, oversee multiple class groups
  in a department, resolve escalations, edit department-level academic structure.
- **SUPERADMIN (platform):** role management across the hierarchy, department/semester scaffolding,
  global announcements, feature flags, audit, abuse handling, data exports.

**Design**
- **Enforce twice:** `RoleGate`/`PermissionGate` for UX, **and Firestore Security Rules + Auth custom
  claims** for real enforcement (client checks are advisory only). Mirror `role`/`rank` into a custom
  claim (`request.auth.token.role`) so rules can authorize without extra reads.
- **Promotion is itself a guarded, audited action** (see E3). A role can only grant a role **strictly
  below** its own rank (CR cannot make ADMIN; ADMIN cannot make SUPERADMIN).
- Add a `permissions`/`policy` module test suite (extend the existing `RolePermissionManagerTest`).

**Acceptance**
- Every capability above is gated in UI **and** denied by rules when attempted out-of-role (verified
  with the Firestore Emulator rules tests).
- Custom claims propagate on role change (token refresh path exists via `UserSessionManager.forceRefreshClaims`).

---

## E3 — Merge-request & approval workflow (governed data changes, all roles)
**Why:** Crowd-sourced academic data (subjects, syllabus topics, PYQs, exam dates, CT marks
corrections) must be **proposable by anyone but approved by authority**, with a clear trail. The
skeleton exists (`MergeRequest`, submit/get/resolve, client-side apply); production needs governance,
generality, and server enforcement.

**What**
- **Generalise the request object:** today types are subject/topic/pyq. Extend to any governed change:
  `add/edit/delete` × `{subject, topic, pyq, examDate, ctMark-correction, roster-fix, role-change}`.
  Keep `data: Map`, `target`, `type`, plus `action` (add|edit|delete) and `scope`
  (class|department|global).
- **Routing by hierarchy:** a request is reviewed by the **lowest sufficient authority** for its scope
  (class change → CR/ADMIN of that class; dept change → ADMIN; role change → one rank above target).
  Add an explicit `assigneeRole`/`reviewQueue` so each reviewer sees only what they may resolve.
- **Lifecycle:** `pending → accepted | rejected | cancelled`, with `reviewedBy`, `reviewNote`,
  timestamps, and an **immutable audit entry** written on every transition.
- **Approval applies the change** (already client-side in `ManagementViewModel`). For production move
  apply-on-approve to a **Cloud Function trigger** (Blaze, see E8) so the write is atomic, server-
  authorized, and not dependent on the reviewer's device — but keep the client-side path as the
  Spark-plan fallback behind a feature flag.
- **Role-change as a merge request:** promotions/demotions flow through the same pipeline (proposed by
  an authority, optionally counter-signed for ADMIN+), giving a uniform, auditable governance model.

**Design**
- Conflict handling: stamp `baseVersion` on the target; reject/auto-rebase if the target changed since
  the request was filed.
- Rate-limit submissions per user; spam/abuse guard on the feed and request queue.

**Acceptance**
- A student proposes a subject edit → it appears only in the correct reviewer's queue → CR approves →
  data updates atomically → both see the result → an audit record exists. Rejections carry a reason.

---

## E4 — Local ↔ Remote data architecture (offline-first, per-user routing)
**Why:** The app mixes **personal** data (Room) and **shared academic** data (Firestore). Production
needs one coherent rule for *where the source of truth lives*, how a specific user's data is fetched
from local vs remote, multi-device consistency, and the new requirement that a **linked second account**
sees the same data.

**What — the source-of-truth split:**
- **Personal workspace** (notes, links, files, schedules, projects, library, profile): **local-first**,
  Room is the source of truth, with an **opt-in encrypted cloud mirror** at `users/{uid}/workspace/...`
  (or an encrypted backup blob) for multi-device + second-account access. This is what makes "second
  Gmail can do any task" actually work — the workspace follows the **uid**, not the device.
- **Shared academic data** (department tree, roster, posts, announcements, CT marks, merge requests):
  **remote-first**, Firestore is the source of truth, Firestore offline persistence provides the local
  cache. Never duplicate these into Room as a second source of truth (the exam-planner mirror should be
  a read cache only).

**What — the routing layer:**
- Introduce a **`SyncManager` + repository routing** so each repository declares its source. A read for
  "this specific user" resolves: personal → Room (then background-refresh from mirror if signed in &
  online); shared → Firestore cache → server. Add a small `DataSource` policy enum per entity
  (`LOCAL`, `REMOTE`, `LOCAL_FIRST_SYNCED`).
- **Sync engine:** per-entity `remoteId` (the `firestoreId` groundwork already started in Room v20),
  `updatedAt`, and a `dirty`/`pendingSync` flag; background `WorkManager` job pushes dirty rows and
  pulls remote changes; **last-write-wins by `updatedAt`** with a per-type override for mergeable data.
- **Connectivity-aware:** offline → serve local + queue writes; online → flush queue, reconcile. Surface
  a subtle "syncing/offline" state in the UI.
- **Per-user isolation & cleanup:** on sign-out, clear/seal the local workspace for that uid; on
  sign-in (incl. via the linked Gmail), hydrate from the mirror. Encrypt local DB
  (SQLCipher) and any cloud backup (client-side key) for privacy.

**Design**
- Keep Firestore reads **bounded** (pagination, `.limit`, single listeners) — already the pattern in
  the posts/announcements repos. Budget reads/writes against the free tier until Blaze (E8).
- Document the data map (collections, ownership, who-reads-what) as a living `DATA_MODEL.md`.

**Acceptance**
- Create a note offline → it persists locally → appears on a second device / linked-Gmail login after
  sync. Academic data renders from cache offline and refreshes when online. No personal data leaks
  across users; signing out clears the local workspace.

---

## E5 — Academic Network (grow the feed into the social pillar)
**Why:** The Network tab is the product's differentiator. Phase D made it real (posts/like/delete);
production needs the full social loop, safely and cheaply.

**What**
- **Comments & threaded replies** (`posts/{id}/comments`), with counts that stay correct (transaction
  or Cloud-Function counter at scale). **@mentions** and basic notifications (E8/FCM).
- **Richer posts:** image/file attachments (Firebase Storage with rules), link previews, post edit
  history, pinned class posts.
- **Discovery & scope:** beyond the single class — department-wide and university-wide channels, topic
  tags, follow/bookmark a tag, and a **global Search** over posts (extend E-search). Category taxonomy
  already defined (Academic Aid / Project Plan / Completed Project / Hackathon / Project/Research Invite
  / University News).
- **Moderation & safety:** report a post/comment, CR/ADMIN moderation queue, profanity/abuse filtering,
  rate limits, block/mute. Audit moderation actions.
- **Reputation:** lightweight endorsements/karma to surface trustworthy contributors (drives data quality
  in E3).

**Design**
- One listener per visible feed; cursor pagination; denormalised author snapshot on each post to avoid
  N extra reads. Counters via FieldValue increments (Spark) → Cloud-Function aggregation (Blaze) at scale.

**Acceptance**
- Comment/like/mention/report/moderate all work and persist; attachments upload under Storage rules;
  department/university channels load; reported content reaches a moderation queue.

---

## E6 — Profile section (standard, production UI on the current design language)
**Why:** Profile is identity + the public face in the Network. It must match the BrainSton design,
separate **public** vs **private** fields, and reflect role/academic standing.

**What**
- **Standardised profile** in the BrainSton look: serif name, mono meta (dept · batch · roll · role
  chip), avatar/cover, About, Skills (`Chip`), Experience, Projects, Connect/social links — all already
  re-skinned; finish the data wiring and validation.
- **Public vs private:** a clear visibility toggle per section (what classmates/department see vs
  private). Network author cards pull from the public subset.
- **Academic standing card:** running CGPA, current term, completed terms (reuse Home's computation),
  shown to the owner; configurable visibility.
- **Connected accounts** (from E1), **role & verification badges**, and an **edit ↔ view** mode that
  writes through to `users/{uid}` + the local `ProfileEntity` cache (E4 sync).
- **Account management:** change display name/photo, manage linked Gmail, sign out, delete account
  (GDPR-style data deletion request), export my data.

**Acceptance**
- Profile renders consistently in the BrainSton design; public/private visibility honored in the
  Network; edits persist locally and remotely; role/verification badges correct.

---

## E7 — Security, privacy & compliance (the production gate)
**Why:** Multi-tenant student data = real obligations. This is non-negotiable before a wide rollout.
- **Firestore & Storage Security Rules** as the real authorization boundary (mirror E2 matrix; rules
  tests in the Emulator). No client trust.
- **Auth custom claims** for role/rank; verified institutional email; per-collection ownership checks.
- **Encryption:** SQLCipher for the local DB; client-side encryption for any cloud workspace backup;
  TLS everywhere (default).
- **Privacy:** privacy policy + terms (referenced in Settings), consent on sign-up, data-export and
  account-deletion flows, PII minimisation, retention policy.
- **Abuse/safety:** rate limits, report/block, content moderation, audit log (immutable) for all
  privileged actions (role changes, merge approvals, moderation, deletions).

**Acceptance**
- Rules deny every cross-tenant/over-role access in emulator tests; local DB and cloud backups are
  encrypted; export/delete flows work; audit log captures privileged actions.

---

## E8 — Platform, scale & operations (Spark → Blaze)
**Why:** Real-time, notifications, atomic governance, and aggregation need server compute; scale needs
cost control and observability.
- **Upgrade to Blaze** to enable **Cloud Functions** (apply-on-approve for merge requests, counter
  aggregation, role-claim setting, moderation hooks) and **FCM push notifications** (announcements,
  mentions, approvals, role changes) — currently stubbed out.
- **Cost guardrails:** pagination + single listeners (existing pattern), denormalisation, scheduled
  aggregation, budget alerts, and a kill-switch feature flag system.
- **CI/CD:** GitHub Actions — build, unit tests, rules tests, lint/detekt, assemble signed release;
  **release signing config** (still missing in `buildTypes.release`), Play App Signing, staged rollout.
- **Observability:** Crashlytics (already wired), Analytics events, performance monitoring, structured
  logging; backup/restore + disaster recovery runbook.
- **Environments:** dev/staging/prod Firebase projects; seed data only in debug (already gated).

**Acceptance**
- Functions handle approvals/notifications/claims; budget alerts live; CI produces a signed APK/AAB;
  crash/analytics dashboards populated; staging separate from prod.

---

## E9 — Quality, accessibility & breadth (industry-level polish)
**Why:** "Full useful app for a university environment" means reliability and reach.
- **Testing pyramid:** unit (VMs, permission matrix, sync/conflict logic), repository/Room/Firestore
  emulator integration tests, Compose UI tests, rules tests; coverage gates in CI.
- **Accessibility:** TalkBack labels, touch targets, dynamic type, contrast (audit the BrainSton tokens),
  RTL readiness, large-font layouts.
- **Performance:** cold-start, scroll jank, image loading (Coil), Room query indices, baseline profiles.
- **Internationalisation:** externalise strings (currently hard-coded), Bengali + English to start.
- **University breadth (roadmap):** multi-university tenancy (configurable domains/departments),
  timetable/routine + room/exam scheduling, results/transcript import, clubs & events, lost-and-found,
  marketplace, faculty/notice integration, calendar sync, web/desktop companion (the codebase already
  ships on multiple Claude-Code surfaces conceptually — target a Compose-Multiplatform or web companion
  later).

**Acceptance**
- CI green with meaningful coverage; a11y audit passes; strings localised; performance budgets met.

---

## Suggested sequencing
1. **E7 + E2 rules foundation** (security/authorization) — make the current features safe first.
2. **E1 identity / multi-account** + **E4 data architecture** (the structural backbone).
3. **E3 governed merge requests** (depends on E2/E4) + **E6 profile** (depends on E1).
4. **E5 Network growth** + **E8 Blaze/ops** (notifications, functions, CI/signing).
5. **E9 quality/accessibility/breadth** — continuous, gated in CI throughout.

## Definition of done (production)
- Security rules + custom claims enforce the full role matrix; emulator rules tests pass.
- Institutional-email sign-up + linked-Gmail second login resolve to one identity and one dataset.
- Personal data is local-first and follows the user across devices/accounts; academic data is remote
  with offline cache; sync conflicts resolved deterministically.
- Merge requests are governed, audited, and applied atomically; role hierarchy enforced end-to-end.
- Network supports comments/attachments/moderation/notifications within cost guardrails.
- Signed release via CI, Crashlytics/Analytics live, privacy/export/delete flows shipped, a11y + i18n
  baseline met.
