# Nestify → BrainSton UI Remake — Master Implementation Plan

> **Goal:** Tear down the entire current Nestify UI layer (theme, navigation, bottom bar, every
> screen's visuals) and rebuild it from scratch in the **BrainSton LMS design language** — warm
> off-white canvas, deep-teal brand, serif headlines + monospace labels, card-based modular layouts,
> and a **floating glassmorphism navigation** with an **uplifted center "Network" button**.
>
> **Scope rule:** This is a **UI/presentation rebuild only**. Keep all working bones intact —
> Room DB, Firestore, Hilt DI, repositories, use-cases, ViewModels, auth/identity. We re-skin and
> re-architect *screens, navigation, theme, and components*; we do **not** touch data/domain layers
> except to add the Network feed backend (already scoped in `happy-forging-crayon.md`).
>
> **Reference design:** `new plans/BrainSton LMS new design/` (`system.jsx`, `chrome.jsx`,
> `screens-*.jsx`). **Companion roadmap:** `new plans/happy-forging-crayon.md` (product IA, backend,
> security — this plan is the *UI execution arm* of that roadmap).

---

## 0. Decisions locked in (from the user's brief)

1. **Network becomes the center, uplifted bottom-nav tab** — it physically replaces the current
   "Library" center slot. Network = the academic social feed (connective tissue between batches/depts).
2. **The current bottom-nav UI is deleted** and replaced with a **new glassmorphism floating nav**.
3. **The base app color theme changes** — adopt the BrainSton palette (teal/warm-sand/coral), not
   the current slate/peach Material-You scheme.
4. **The whole UI is remade** following BrainSton's screen system — *replicate the design language,
   not the content*. Nestify keeps its own features; they are re-expressed in BrainSton's visual idiom.
5. The current Nestify UI structure is **abandoned**, not patched.

### Locked technical choices
- **Glass blur:** use the **Haze** library (real frost on API 31+, auto scrim fallback on 28–30),
  added via `libs.versions.toml`.
- **Fonts:** **bundle TTFs** in `res/font/` (offline, no extra dependency). *Prerequisite:* the three
  font files (`Instrument Serif` Regular+Italic, `Inter Tight` 400–800, `JetBrains Mono` 400/500)
  must be present — download from Google Fonts (OFL) and drop into `res/font/` before Phase A step 1.

### Reconciling the two nav specs
`happy-forging-crayon.md` already defines the target 5-tab IA. BrainSton's reference nav is a
4-tab floating pill with **no** uplifted center. The user explicitly wants **glassmorphism + an
uplifted center Network button**. We therefore **merge**: take BrainSton's *floating, rounded,
soft-shadow pill aesthetic* and add a *teal uplifted center FAB* for Network.

**Final bottom bar (5 slots):**

| Slot | Tab | Icon (BrainSton set) | Replaces (old) |
|------|-----|----------------------|----------------|
| 1 | **Home** | `home` / `homeF` | Home |
| 2 | **Academics** | `grad` / `gradF` | Planner |
| 3 | **Network** *(center, uplifted)* | `news`/`globe` | **Library** ← per user request |
| 4 | **My Space** | `study` / `studyF` | Services |
| 5 | **Profile** | `user` | Profile |

---

## 1. Design language translation (BrainSton JSX → Compose tokens)

The whole system is driven by the `T` token object in `system.jsx`. Step one is to port it 1:1 into
a Kotlin design-token layer. **Everything downstream consumes these tokens — no hard-coded colors,
sizes, or fonts in screens.**

### 1.1 Color tokens (`ui/theme/Color.kt` — full rewrite)

```
// Backgrounds
BrandBg        = #F4F2EE   // warm near-white app canvas
Surface        = #FFFFFF   // cards
Surface2       = #FAF8F4   // subtle alt surface
SurfaceDk      = #0F1A18   // deep teal-black (dark hero cards)
// Ink (text hierarchy)
Ink            = #0F1A18
Ink70          = #3A4744
Ink50          = #6E7A77
Ink30          = #A6ADAB
Ink10          = #E6E4DF
// Brand — deep teal
Brand          = #0E6E5A
BrandDeep      = #0A4F41
BrandSoft      = #E7F1EE
BrandTint      = #F1F7F5
// Accent — warm coral (sparingly: like/error/sale)
Coral          = #D97A57
CoralSoft      = #FAEBE3
// Functional
Warn=#B8842B  WarnSoft=#FAF1DE  Ok=#1B7A53  OkSoft=#DDEEE6
// Hairlines
Hair  = rgba(15,26,24,0.08)
Hair2 = rgba(15,26,24,0.05)
```

Map these into a Material3 `lightColorScheme` (and a derived dark scheme using `SurfaceDk`/inverted
ink) **and** expose them as a custom `NestifyColors` object via `CompositionLocal` for the
non-Material tokens (ink50, hair, brandSoft, coral, etc.) that Material3 has no slot for.

> **Kill dynamic color.** `Theme.kt` currently defaults `dynamicColor = true`, so Material-You
> wallpaper theming is live. Remove it — it fights the fixed brand identity. Theme = fixed
> BrandBg/Brand palette.
>
> **⚠ Sequencing — add, don't delete (corrected).** Existing screens and the old `bottomNavigation.kt`
> still reference the old tokens (`NestifySlate`, `NestifyGreen`, `NestifyGradients`, the
> `md_theme_light_*` vals). Phase A must **add the new BrainSton tokens alongside** the old ones and
> **change the values feeding the Material `colorScheme`** — it must **not delete** the old `val`s, or
> the build breaks before those screens are re-skinned. Old tokens are removed **per-screen** as each
> screen migrates; the final slate/peach/gradient purge happens in Phase F (§5 cleanup).
>
> **Theme switch is deferred.** `AppSettingManager`/`SettingDatastore` expose only a **boolean
> `isDarkMode`**, and `MainActivity` calls `NestifyTheme(darkTheme = …)`. A real Sys/Light/Dark
> (`ThemeConfig`) switch needs DataStore + manager + settings-UI work → **moved to Phase E (Settings)**.
> Phase A keeps the existing boolean plumbing and only drops the `dynamicColor` parameter (update the
> `MainActivity` call site accordingly).

### 1.2 Typography (`ui/theme/Type.kt` — full rewrite)

BrainSton uses **three** type families:
- **Serif** (`Instrument Serif`) → all headlines (28–42sp, negative letter-spacing).
- **Sans** (`Inter Tight`) → body, labels, buttons.
- **Mono** (`JetBrains Mono`) → kickers, metadata, section labels (10sp, UPPERCASE, +letter-spacing).

**Action:** bundle these three fonts as TTF in `app/src/main/res/font/` (offline-reliable; no extra
dependency), define `FontFamily`s, and build a full `Typography` plus a `NestifyType` token object
for the serif/mono roles Material3 doesn't model:

> **⚠ Font reality:** **Instrument Serif ships only Regular + Italic — there are no bold weights.**
> Do **not** assign `FontWeight.Bold`/`SemiBold` to serif styles (it would synth-bold and look wrong);
> serif hierarchy comes from *size + tight letter-spacing*, all at `FontWeight.Normal`. Weight
> variation lives in **Inter Tight** (400/500/600/700/800) and **JetBrains Mono** (400/500) only.
> Keep `MaterialTheme.typography` populated too — old un-migrated screens read `typography.*` and must
> still render until rewritten.

| Role | Family | Size / weight | Use |
|------|--------|---------------|-----|
| `displaySerif` | Serif | 38–42sp | splash / hero H1 |
| `h1Serif` | Serif | 30–36sp | screen titles |
| `h2Serif` | Serif | 22–28sp | section / card titles |
| `h3Serif` | Serif | 18–20sp | sub-headers, stat values |
| `kicker` | Mono | 10sp, UPPERCASE, ls=1 | section eyebrow labels |
| `meta` | Mono | 9–10sp | dates, counts, codes |
| `body` | Sans | 14–15sp, lh 1.5 | paragraphs |
| `label` | Sans | 12–13sp, w500/600 | chips, captions |
| `button` | Sans | 14–16sp, w600 | buttons |

### 1.3 Shape / spacing / elevation tokens (new `ui/theme/Tokens.kt`)

> **⚠ Corrected:** there is **no** `ui/theme/Dimens.kt`. The existing
> `presentation/ui/util/Dimens.kt` is onboarding/article-specific (`ArticleCardSize`,
> `articleImageHeight`) — leave it alone. Create a **new** `ui/theme/Tokens.kt` for the BrainSton
> spacing/radii/elevation scale below.

- Radii: `xs=6 s=10 m=14 l=18 xl=22 pill=999`.
- Spacing scale: `6 / 8 / 10 / 12 / 14 / 16 / 20 / 24 / 28 / 32`. Default screen padding = **20dp**.
- Shadows: `shadowCard` (subtle, y8 blur24 −16 spread), `shadowSheet` (top, y−8 blur32),
  `shadowFab` (teal-tinted, for the uplifted Network button & FABs).
- Replace `GradientUtils.kt` gradients with BrainSton-flavored ones: the dark-hero gradient
  (SurfaceDk + faint teal blobs) and the brand-soft wash.

---

## 2. Reusable component library (`presentation/ui/components/brainston/`)

Build the primitive layer **once**, before any screen. These are direct Compose ports of the
`system.jsx` + `chrome.jsx` primitives. Every screen is assembled from these.

### 2.1 Primitives (port of `system.jsx`)

| Composable | Source | Notes |
|------------|--------|-------|
| `NestifyCard` | `Card` | surface, radius `l`, 1dp hair2 border, optional onClick |
| `Chip` | `Chip` | tones: default/brand/soft/coral/warn/ok/ghost; active state; pill |
| `NButton` | `Btn` | variants primary/secondary/ghost/soft/danger/dark; sizes sm/md/lg; leading/trailing icon; `full` |
| `SectionHead` | `SectionHead` | mono kicker + serif/sans title + optional "See all" action w/ chevron |
| `NestifyInput` | `Input` | 48dp, leading/trailing, brand focus border |
| `SearchBarPill` | `SearchBar` (chrome) | 46dp pill, search icon, optional trailing send |
| `Avatar` | `Avatar` | initials, deterministic palette color, optional ring |
| `ProgressBar` | `Progress` | 4–6dp, color/track, optional label |
| `Stars` | `Stars` | rating row (reused for endorsements/quality if needed) |
| `TabPill` | `TabPill` | segmented pill switcher (surface2 track, active=surface+shadow) |
| `Placeholder` | `Placeholder` | striped image placeholder w/ mono label (until real images) |
| `BrandMark` | `BrandMark` | the Nestify logo lockup (re-draw with Nestify glyph, teal) |
| `VerifiedBadge` | `Verified` | small teal check (for CR/verified authors) |
| `StatRow` | (recurring) | N values w/ vertical dividers + mono labels |
| `EmptyState` | `AuthBlock`/empty patterns | icon-in-rounded-square + serif title + body + CTA |
| `IconButtonChrome` | `iconBtn` | 38dp transparent square icon button |

### 2.2 Chrome / scaffolds (port of `chrome.jsx`)

- **`NestifyAppBar`** — variants `default` (brandmark + subtitle + trailing icons), `title` (back +
  centered title), `logo`, `transparent` (for hero-image screens). Status-bar-aware top padding.
- **`NestifyScaffold`** — standard screen wrapper: `BrandBg` background, app bar slot, scrollable
  content with 20dp padding, and a `NavSpacer` so content clears the floating nav.
- **`GlassBottomNav`** — see §3 (the headline new component).
- **`Drawer`** *(optional, phase-late)* — BrainSton side drawer for secondary nav/settings shortcuts.
- **`BottomSheet`** — handle bar + rounded-top sheet + backdrop blur (compose-port; used for
  compose-post, filters, course/module preview equivalents).
- **`AuthBlock`** — "sign in to continue" gated body (reuse for role-gated/locked sections).

> **Definition of done for Phase 1:** a `ComponentGallery` preview screen renders every primitive
> above against `BrandBg`, matching the BrainSton look, before any feature screen is built.

---

## 3. The new glassmorphism navigation (headline deliverable)

**Delete** `presentation/navigation/Components/bottomNavigation.kt` entirely (the white rounded-top
surface + slate uplifted button). Replace with a **floating glass pill + uplifted teal center FAB**.

### 3.1 `GlassBottomNav` spec

- **Container:** floating, detached from screen edges — `padding(start=16, end=16, bottom=16)`,
  height ~64dp, `clip(RoundedCornerShape(28dp))`.
- **Glass effect:** semi-translucent surface (`Surface` @ ~70% alpha) over a `hazeChild`/blur of the
  content behind it. Use the **Haze** library (`dev.chrisbanes.haze`) for real backdrop blur
  (Compose has no native backdrop filter). Add a 1dp `Hair2` top border + faint inner highlight for
  the "frosted" edge, and `shadowCard`/`shadowFab` drop shadow beneath.
- **4 flat items** (Home, Academics, My Space, Profile): icon + (animated) label. Active = filled
  icon variant (`homeF`/`gradF`/`studyF`) + `BrandSoft` pill behind + `BrandDeep` tint + label
  fades in; inactive = stroke icon + `Ink50`, label hidden (BrainSton behavior).
- **Center uplifted Network button:** a 64dp circle floating ~22dp above the bar, teal
  (`Brand`→`BrandDeep` gradient), white `news`/`globe` icon, `shadowFab` teal glow, 2dp white ring.
  Selected state = brighter gradient / subtle scale pulse. This is the social-feed entry point.
- **Animation:** `animateColorAsState` for tints, `animateFloatAsState` scale on the FAB,
  crossfade between stroke/filled icons.

> **Library note:** add Haze to `app/build.gradle.kts`. If we want zero new deps, fall back to a
> translucent-surface "pseudo-glass" (alpha + gradient + border, no true blur) — still a big upgrade,
> but real blur (Haze) is recommended for the requested glassmorphism look.

### 3.2 Navigation wiring (`InAppNav.kt`, `Route.kt`, `NavGraph.kt`)

- **`Route.kt`:** rename/replace tab routes → `Home`, `Academics` (was `ExamPlanner`), `Network`
  (new), `MySpace` (new hub), `Profile`. Keep all secondary routes; add `Route.Network`.
  Delete dead routes (`Archive`, `Favorites`, `Search`, `Services`/`Library` if folded in).
- **`InAppNav.kt`:** rebuild the `items` list to the 5 new tabs, map `selectedItem` to the new
  routes, render `GlassBottomNav` instead of `bottomNavigation`. Center index (2) → Network.
  Because the nav now **floats over** content, switch the Scaffold so content draws full-bleed
  behind the nav (no bottom content padding; rely on `NavSpacer` inside each screen). Keep the
  `navigateToTab` single-top + save/restore logic.
- **`NavGraph.kt`:** unchanged auth gate; just points to the rebuilt `InAppNav`.

---

## 4. Screen-by-screen remake (Nestify feature → BrainSton pattern)

Each Nestify screen is re-expressed using a BrainSton screen archetype. **ViewModels & state stay;
only the `@Composable` UI is rewritten** against the new component library.

| Nestify screen | BrainSton archetype to follow | Key elements |
|----------------|-------------------------------|--------------|
| **Home (dashboard)** | Home Tab + Study Tab greeting | Greeting row (avatar + serif name), dark **hero stat card** (CGPA% / next exam countdown — the "Progress Card" pattern), `SectionHead`+horizontal carousels (announcements, quick actions), stats bar (3-col dividers). |
| **Academics hub** | Courses Tab (catalog) + Study Tab | `TabPill` (Subjects / Exam Planner / CT Marks / Results / Reading Room). Category cards (100dp image + content + arrow link), dark "featured" bundle card. |
| → Exam Planner / Exam detail | Course Details (hero-image header) | Hero header w/ overlapping stat card, pricing-card→"exam summary" card, module list = exam topics w/ numbered circles. |
| → CT Marks / Results / CGPA | Study progress + StatRow | Dark progress hero (CGPA), list rows w/ status chips (color-coded by grade), progress bars. |
| → Reading Room (PYQ bank) | Module/Video list | List rows w/ thumbnail + play/check overlay, filter chips (Videos/Resources → Questions/Solutions). |
| **Network (feed)** ⭐ | Articles Tab + Article Detail + compose sheet | Search pill + category filter chips; **feed cards** (author avatar+name+verified, type chip, serif title, body, hashtags, like/comment/share row). Compose-post = bottom sheet. Already prototyped in `AcademicFeedScreen.kt` — re-skin + wire to Firestore (Phase 4 of companion roadmap). |
| **My Space hub** | Profile "Quick Actions" grid + Study list | 2-col grid of feature cards (Notes / Links / Files / Schedules / Projects / Library), each = icon box + title + desc. Bookmarks/Archive/Favorites become **filter chips**, not screens. |
| → Notes / Links / Files / Folders | Study course list / Wishlist list rows | Card rows (image/icon left + content + trailing action), `SectionHead`, empty states. Detail screens = title header + body + meta. |
| → Schedules | Checkout step-indicator + list | Timeline rows, category chips (weekly/monthly/yearly recolored to brand tones). |
| → Projects (merge the two impls) | Course Details + module list | Hero header, module/task list w/ progress, status chips. **Consolidate** `ProjectPlanner/*` + `projectplans/*` into one. |
| **Profile** | Profile screen | User card (avatar ring + name + email + institution chip), quick-actions grid, settings card list, role-gated "Class Management" row, logout (coral). |
| → Settings | Settings screen | `SettingsSection` (mono label + card) + `SettingRow` (icon box + title/desc + trailing toggle/chip/chevron), `Toggle`, 3-way theme switch (Sys/Light/Dark). |
| → Management Hub (CR/admin) | Settings list + Article cards | Role-gated. Merge-requests review list, announcements composer, role management rows. |
| **Auth (login/signup/OTP)** | Auth screens | Serif welcome headline, icon inputs, divider-with-"or", Google button, testimonial card, OTP digit boxes. |
| **Splash** | Splash | Centered brandmark in white rounded square + serif wordmark + spinner. |

### 4.1 Cross-cutting screen rules (apply to every rebuilt screen)
- Background = `BrandBg`; cards = `Surface` with `Hair2` border + `shadowCard`.
- Every screen handles **empty / loading / error** via shared components (loading = shimmer on
  placeholders; empty = `EmptyState`; error = retry card).
- Headlines serif, eyebrows mono-uppercase, body sans. 20dp screen padding, `NavSpacer` at bottom.
- Horizontal scroll rows for browse-y content; `TabPill` for in-screen section switching.

---

## 5. Cleanup (delete with the old UI)

> **Timing:** deletions happen **as each screen migrates**, not up front. Dead screens go when their
> tab is rebuilt; the old `bottomNavigation.kt` goes in Phase B; the slate/peach color tokens and old
> `meshGradient` go in **Phase F** (only once nothing references them). Deleting earlier breaks the build.

Per `happy-forging-crayon.md` §"Cut" — do this as part of the teardown:
- **Delete:** `bottomNavigation.kt` (old), `ArchiveScreen`, `FacouritesScreen`, `SearchScreen`
  (rebuilt as filters / real global search), `test.kt`/`test2.kt`/`test3.kt`,
  `HomeScreen/components_older/`, `ServiceScreen` (folded into My Space / Profile), unused
  `ProfileScreen/PdfGenerator.kt`, the duplicate project-planner implementation, old `GradientUtils`
  gradients, the slate/peach color tokens.
- **Keep & re-skin:** every ViewModel, repository, use-case, DAO, entity, mapper, manager.

---

## 6. Phased execution (each phase compiles & runs)

> Every phase ends with `./gradlew :app:compileDebugKotlin` + `:app:assembleDebug` green and a
> manual smoke test. Build the foundation before re-skinning screens so there's one source of truth.

### Phase A — Design foundation (no feature screens yet)
1. **Fonts:** add `Instrument Serif` (Regular+Italic only), `Inter Tight` (400–800), `JetBrains Mono`
   (400/500) TTFs to `res/font/`; define `FontFamily`s.
2. **Tokens:** rewrite `Color.kt` (**add** BrainSton tokens + repoint the Material `colorScheme` values;
   keep old `val`s, don't delete), rewrite `Type.kt` (serif/sans/mono `Typography` + `NestifyType`),
   create new `ui/theme/Tokens.kt` (spacing/radii/elevation), add BrainSton gradients to
   `GradientUtils.kt` (keep the old `meshGradient` until the old nav is gone).
3. **Theme:** in `Theme.kt` drop the `dynamicColor` branch; update the `MainActivity` call site (still
   pass the existing `isDarkMode` boolean). **Do not** add the 3-way switch yet (Phase E).
4. Build the component library §2 + a `ComponentGallery` `@Preview`/debug route.
5. **Verify:** `:app:compileDebugKotlin` + `:app:assembleDebug` green; old screens still compile and
   render (loosely re-tinted by the new `colorScheme`); gallery shows the full BrainSton kit on
   `BrandBg`; app launches.

### Phase B — Navigation shell
1. Add Haze dep; build `GlassBottomNav` + uplifted Network FAB.
2. Rewrite `Route.kt` (5 new tabs + `Network`), `InAppNav.kt`, delete old bottom nav.
3. Stub the 5 tab screens (empty `NestifyScaffold` shells) so nav is navigable.
4. **Verify:** all 5 tabs switch; glass nav floats over content; Network FAB opens the Network shell.

### Phase C — Home + Academics  ✅ DONE
Rebuild Home dashboard and the Academics hub + its sub-tabs/detail screens against their archetypes.
**Verify:** real CGPA/exam/announcement data renders in the new look.

**Phase C checklist (completed):**
- [x] **Home** (`HomeScreen.kt`) — greeting row (Avatar + serif greeting + mono date), dark HERO
  stat card (live running CGPA from `termReports`, ProgressBar, StatRow CGPA/Subjects/Next-exam),
  announcements carousel (new read-only `HomeFeedViewModel` over `AnnouncementRepository`),
  quick-actions IconTile carousel, live workspace counts grid, recent-activity list.
  Loading/empty states handled. Old `components_older/` + `components_new/` deleted.
- [x] **Academics hub** (`ExamPlannerScreen.kt`) — `NestifyAppBar` + `ScrollableTabPill` (Subjects /
  Planner / CT Marks / Results / CGPA / Sync) swapping the existing tab composables; default-workspace
  dialog preserved. No nav-route changes.
- [x] **Sub-tabs rebuilt in place:** `SubjectsDetailsTab` (course cards + add form, RoleGate),
  `ExamPlanTab` (numbered IconTiles + status chips + progress + ExamDetail nav),
  `ClassTestMarksTab` (marks grid + color-coded grade-predictor chips, roster picker),
  `ExamResultsTab` (dark CGPA hero + StatRow + grade-toned dropdown rows + PDF export),
  `CGPADashboardTab` (dark CGPA hero + target analysis + 8-term grid + transcript dialog),
  `PackagingSyncTab` (re-skinned card rows, all export/import/Drive-sync logic preserved).
- [x] **Detail screens:** `ExamDetailScreen` (dark hero summary + section TabPill + numbered topic
  cards + status chips), `ReadingRoomScreen` (PYQ bank: filter chips Questions/Solutions, table
  overview + re-skinned PYQ cards, add/edit sheet + timer preserved).
- [x] Shared helpers: `ScrollableTabPill`, `NumberTile`, dark-variant `StatRow`, `LevelTermFilter`,
  `gradeTone()`.
- [x] `:app:compileDebugKotlin` + `:app:assembleDebug` green.
- **Deferred:** CGPADashboard PDF-viewer dialog keeps its intentional "printed paper" literal-color
  look; full grade-predictor restyle and a numeric keyboard on `NestifyInput` left for polish.

### Phase D — Network (the new pillar)  ✅ DONE
Re-skin `AcademicFeedScreen` to the Articles/feed archetype; add compose-post bottom sheet; wire to
Firestore `posts` (per companion roadmap Phase 4). **Verify:** post on one account shows on another;
filters work.

**Phase D checklist (completed):**
- [x] **Backend (additive only):** `domain/model/Post.kt`, `domain/repository/PostRepository.kt`,
  `data/repository/PostRepositoryImpl.kt` (one `callbackFlow` snapshot listener, `orderBy createdAt`
  DESC with a `.limit(50)` page cap; `createPost` via `doc.set` + `serverTimestamp`; `toggleLike`
  in a transaction keeping `likeCount` synced with `likedBy` arrayUnion/arrayRemove; `deletePost`).
  Scoped under `classGroups/{groupId}/posts`, mirroring announcements.
- [x] **DI:** `providePostRepository(firestore)` added to `FirebaseModule.kt`; Hilt graph builds.
- [x] **VM:** `NetworkFeedViewModel` (`@HiltViewModel`) — `posts` StateFlow via
  `sessionFlow.flatMapLatest { repo.getPosts(classGroupId) }.stateIn(...)`; `createPost`/`toggleLike`/
  `deletePost` read the session for author identity (uid, displayName, dept, roll, role); `canModerate`
  via `UserRole.rank`. One listener; category filter is client-side.
- [x] **Screen** (`AcademicFeedScreen.kt`) — rebuilt on `NestifyAppBar` ("Network" / "Your academic
  community" + search icon) + canvas `LazyColumn` clearing the glass nav. Composer entry row (Avatar +
  pill + brand "+") opens a BrainSton `ModalBottomSheet` (category Chip selector + title/details/tags
  inputs → `createPost`). Category filter Chip row (7 `ChipTone`-coded categories + All). Feed
  `NestifyCard`s: Avatar + serif name + mono meta/time + type Chip, serif title, Inter-Tight body,
  #hashtags, action row (Endorse/like with live count + `likedBy` state, Comment count, Share) and an
  author/CR-admin remove action. `EmptyState` for empty/filtered-empty; error surfaced in the sheet.
- [x] Legacy hardcoded `sampleFeed` / private `PostType` enum / raw Material3 Card+TopAppBar removed.
- [x] Optional demo seeding: `seedNetworkPosts` in `FirebaseTestDataInitializer`, gated by
  `BuildConfig.DEBUG` (out of release builds).
- [x] `:app:compileDebugKotlin` + `:app:assembleDebug` green; `Route.Network`/`InAppNav`/`GlassBottomNav`
  untouched.
- **Deferred:** comment threads (only `commentCount` is shown; no comment-create yet) and Share are
  no-ops pending Phase 5/6; no explicit loading spinner (cache-backed listener resolves to empty/data
  fast, so empty state covers first paint).

### Phase E — My Space + Profile/Settings/Management  ✅ DONE
Rebuild the My Space grid hub and all personal-productivity screens; Profile/Settings/Management,
3-way theme switch, bookmark/archive as filters. **Verify:** CRUD works; theme switch persists.

**Phase E checklist (completed):**
- [x] **3-way theme switch (cross-cutting):** `theme_mode` ("system"|"light"|"dark") threaded through
  `SettingDatastore` → `SettingsRepo`/`SettingsRepoImpl` → `AppSettingManager`; `MainActivity` observes
  `themeMode` and drives `NestifyTheme(darkTheme=…)` with an `isSystemInDarkTheme()` fallback.
  `SettingsViewModel` + `SettingState` expose `themeMode`/`onThemeModeChanged`.
- [x] **Settings** rebuilt (`NestifyScaffold`/`NestifyAppBar`, `SectionHead`, token rows) with the theme
  `TabPill` (System/Light/Dark) and tokened toggle/action rows; dialogs kept functional.
- [x] **Profile** re-skinned (`NestifyAppBar`, `NestifyCard` sections, `NestifyInput`, `Chip` skills,
  `Avatar`); Coil image pickers + edit/dialog logic preserved.
- [x] **Personal CRUD re-skinned:** Notes (list/detail/create), Links (×5), Files (×3), Schedule
  (screen + creation sheet), Projects + ProjectPlanner (×4), Library — all on the `brainston` library,
  ViewModel/Route/nav APIs untouched.
- [x] **Management cluster:** hub + Announcements / RoleManagement / MergeRequests re-skinned
  (priority/role/status → `ChipTone`; approve/reject → `NButton`).
- [x] **Filter screens wired to real data:** `ArchiveScreen` (`isArchived`), `FacouritesScreen`
  (reuses `isBookmarked` — no `isFavorite` column, see deferral) and `SearchScreen` (debounced
  `flatMapLatest` over `ContentRepository.search{Notes,Links,Files}`) now have `@HiltViewModel`s that
  `combine` the existing `GetAll{Notes,Links,Files}UseCase`s; shared `screens/common/FilterFeed.kt`
  (`FeedListItem`/`FilterFeedContent`/`FeedRow`). `Route.Search` registered in `InAppNav`; the Network
  app-bar search icon opens it. **No Room migration / DAO / entity changes.**
- [x] Dead legacy components deleted (`NoteTextField`, `NoteTagsSection`, Bookmarks `BookmarkItem`/
  `EmptyBookmarksState` composables, FileScreen `fileItem`).
- [x] `:app:compileDebugKotlin` + `:app:assembleDebug` green.
- **Deferred:** distinct Favorites-vs-Bookmarks would need an `isFavorite` column + Room migration (kept
  out — Favorites reuses `isBookmarked`); `LibraryScreen` reading-progress is still a hard-coded `0.65f`
  placeholder; `ProjectDetailScreen` still reads `mockProjects.first()` (no `projectId` wiring pre-remake).

### Phase F — Auth/Splash + polish + QA  ◐ PARTIAL
Re-skin auth/splash; full empty/loading/error/accessibility/perf pass; remove demo seeding from
release; signed APK smoke test on a clean device.

**Phase F checklist:**
- [x] **Auth** re-skinned to BrainSton (`BrandMark` + serif headline, `TabPill` login/sign-up toggle,
  `NestifyInput` fields, `NButton` CTAs); all Firebase/Google auth logic preserved.
- [x] **Demo seeding removed from release:** `NestifyApplication` only calls
  `testDataInitializer.initializeData()` under `BuildConfig.DEBUG`; demo Network posts also debug-gated.
- [x] Empty/loading/error states present across the re-skinned screens (`EmptyState` /
  `CircularProgressIndicator(color=c.brand)` / tokened error text).
- **Deferred:** Splash is an **empty** nav entry (no composable — launch/auth-check lives in
  `NavGraph` start-dest logic), so there is nothing to re-skin; **release signing config not set up**
  (`buildTypes.release` has no `signingConfig`) — needed before the signed-APK pilot smoke test;
  full accessibility/perf pass and on-device clean-install smoke test still to run.

---

## 7. Critical files (touch list)

- **Theme:** `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`, `Dimens.kt`, `GradientUtils.kt`,
  `ThemeConfig.kt`; `res/font/*` (new).
- **Components (new):** `presentation/ui/components/brainston/*` (all of §2).
- **Nav:** `presentation/navigation/InAppNav.kt`, `NavGraph.kt`,
  `navigation/Components/Route.kt`; **delete** `navigation/Components/bottomNavigation.kt`.
- **Screens:** every package under `presentation/ui/screens/*` (UI bodies rewritten);
  `AcademicFeed/AcademicFeedScreen.kt` (re-skin + back with Firestore).
- **Build:** `app/build.gradle.kts` (Haze + fonts), `MainActivity.kt` (theme wiring unchanged logic).
- **Deletions:** dead screens/components/tests per §5.

---

## 8. Risks & mitigations

| Risk | Mitigation |
|------|------------|
| Real backdrop blur for glass nav | **`minSdk = 28`, but RenderEffect blur needs API 31+.** Use **Haze** (auto-falls back to a translucent scrim on API 28–30, true frost on 31+); or pseudo-glass (alpha+gradient+border) as a zero-dep fallback everywhere. Add Haze via `libs.versions.toml`, consistent with the catalog. |
| Font licensing/bundling weight | All three fonts are OFL (Google Fonts) — safe to bundle; subset if APK size matters. |
| Large surface area → regressions | Phase-gated; keep ViewModels/data untouched; component gallery as a visual contract. |
| Two project-planner impls | Consolidate to one during Phase E (decided in companion roadmap). |
| Dynamic-color users lose Material-You | Intentional — fixed brand identity is the goal; offer Light/Dark/System only. |

---

## 9. Definition of done

- Old bottom nav, slate/peach theme, and dead screens are **gone**.
- App uses the BrainSton palette, three-family typography, and card-based layouts everywhere.
- Floating glassmorphism nav with **uplifted teal Network center button** is live.
- All five tabs (Home, Academics, **Network**, My Space, Profile) work end-to-end on real data.
- Every screen has empty/loading/error states and matches the component gallery's visual language.
- `:app:assembleDebug` is green; clean-device smoke test passes.
