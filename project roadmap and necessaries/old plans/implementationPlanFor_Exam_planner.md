# 🎓 Detailed Implementation Plan: Exam Planner (Pro Feature)

This document outlines the end-to-end architecture, UI/UX designs, database models, grade calculation logic, and file packaging engines for the Nestify **Exam Planner** feature.

---

## 🗺️ High-Level Roadmap & Architecture Choices

1. **Workspace Decoupling**: Build a dedicated sidebar Navigation Drawer (collapsing bottom-navigation) when entering the Exam Planner view.
2. **Persistent Storage (Room DB)**: Establish relation-based database entities to track semesters, courses, class tests, syllabus items, and generated reports.
3. **Internal & Predictive Calculation Engine**: Implement robust calculations to compute the best 3 of 4 CT marks and predict required written exam marks to secure grades (A+, A, etc.).
4. **Target CGPA Track Analyzer**: Formulate linear projections to calculate remaining term targets based on current running average and set targets.
5. **Native PDF Document Builder**: Integrate Android's native `PdfDocument` graphics canvas to render formal, professional academic reports.
6. **Package & Cloud Synchronization**: Write JSON-serialization wrappers and use Android Storage Access Framework (SAF) to sync data with standard cloud folder backends.

---

# 🛠️ Development & Implementation Steps

## Module 0: Complete Dummy UI Implementation (Prototyping Phase)

### 1. Goal
Provide a comprehensive, pixel-perfect, mock-state driven interface matching all feature requirements to give developers immediate design feedback.

### 2. Scope
- Designing the sidebar navigation layout replacing the bottom bar.
- Structuring all six sub-screens with mock details (CGPA Dashboard, Course Registration, Class Test Marks Grid, Topic checklists, Results entry, Packaging and Sync view).

### 3. Requirement Analysis
- Must completely replace standard in-app navigation bottom bar.
- Layout must be responsive, clean, and utilize Nestify's gradient styling.

### 4. Architecture Decisions
- Implement `ModalNavigationDrawer` at the top level of the screen container.
- Use a dedicated sub-navigation router or dynamic local enum state variable (`ExamPlannerTab`) to switch contents within a single composable container.

### 5. Data Model Changes
- Mock structures only (`MockAcademicReport`, `MockCourse`, `MockCTMarks`, `MockTopic`).

### 6. Interface/API Changes
- Adds `Route.ExamPlanner` navigation route.

### 7. Security Considerations
- Screen-scoped transient states, no external permissions required.

### 8. Testing Strategy
- Previews and interactive touch verification.

### 9. Risks and Mitigations
- **Risk**: Drawer overlaps with phone system swipe gestures.
- **Mitigation**: Adjust drawer guesture intercept bounds.

---

### Implementation Steps

#### Phase 1: Left Sidebar Drawer & Tab Scaffold
- **Step 1**: Create a layout file `ExamPlannerScaffold.kt` that utilizes a `ModalNavigationDrawer` with custom items: CGPA Dashboard, Subjects details, Class test marks, Exam Plan, Exam results, and Packaging & sync.
- **Step 2**: Hide the bottom navigation of `InAppNav.kt` upon transition, assigning full screen height to this workspace.

#### Phase 2: CGPA Dashboard UI
- **Step 1**: Design high-impact, premium cards representing printed PDF academic sheets. Include University Name, Student Name, Student ID, Batch, Course results, and Overall GPA.
- **Step 2**: Build an 8-term term-GPA inputs table using sliders/text fields.
- **Step 3**: Design a target tracker card highlighting current running average (computed dynamically) vs target CGPA.

#### Phase 3: Subject (Course) Details UI
- **Step 1**: Design the course creation sheet: Course Name, Course Code, Credits dropdown (1.0 to 4.0), and Academic Term selector.
- **Step 2**: Build a list showing course cards categorized and grouped under specific Terms (e.g., L2T2, L2T1).

#### Phase 4: Class Test Marks Grid UI
- **Step 1**: Design a table-like layout where each row stands for a subject, containing 4 editable CT inputs and 1 Attendance input.
- **Step 2**: Highlight the highest 3 of 4 entered CT marks dynamically.
- **Step 3**: Display a bottom card containing required final written marks to secure target grades (A+, A, etc.) out of 210 (3-credit) or 280 (4-credit).

#### Phase 5: Syllabus Checklist (Exam Plan Detail UI)
- **Step 1**: Provide Section A and Section B tab layout screens for a selected subject.
- **Step 2**: Render topic list cards having: checkbox for Completed, checkbox for Revised, and 5-star priority level.
- **Step 3**: Display remaining days countdown and circular progress reports.

#### Phase 6: Exam Results Entry UI
- **Step 1**: Build a form displaying all current subjects with grade selection dropdowns (A+, A, A-, etc.).
- **Step 2**: Include a "Generate Term GPA Report" and "Save to Dashboard / Share PDF" button.

#### Phase 7: Packaging & Sync Settings UI
- **Step 1**: Design actions to "Export Semester Package" and "Import Semester Archive".
- **Step 2**: Render sync status markers and connection feedback indicators.

---

## Module 1: Data persistence (Room Database Integration)

### 1. Goal
Establish localized data schemas to securely persist courses, grades, topic checklists, and report histories.

### 2. Scope
- SQLite/Room schema entities.
- Relation queries and transactional data operations.

### 3. Architecture Decisions
- Use Room relationships (`@Relation`) to query a Subject with its associated CT marks and Syllabus topics.

### 4. Implementation Steps

#### Phase 1: Room Database Schema & Entities
- **Step 1**: Create `SubjectEntity.kt` holding fields: id, code, name, credits, level, term, examDate, and finalGrade.
- **Step 2**: Create `ClassTestMarkEntity.kt` holding: id, subjectId (foreign key), testIndex (1-4), and marks (out of 20).
- **Step 3**: Create `SyllabusTopicEntity.kt` containing: id, subjectId (foreign key), section (A/B), title, isCompleted, isRevised, and priority (1-5).
- **Step 4**: Create `TermReportEntity.kt` storing: id, level, term, gpa, pdfLocalUri, and timestamp.

#### Phase 2: DAO & Repository Interfaces
- **Step 1**: Code `ExamPlannerDao.kt` containing transactional inserts, updates, and reactive Flow listings.
- **Step 2**: Implement `ExamPlannerRepositoryImpl.kt` exposing clean Domain model maps.

---

## Module 2: Grading Engine & Predictive Mathematics

### 1. Goal
Process grade calculations and forecasting with absolute precision.

### 2. Scope
- Calculation of best 3 of 4 CT marks.
- Cumulative & Term GPA calculations.
- Predictive Written Marks calculator.

### 3. Implementation Steps

#### Phase 1: CT and Term GPA Math
- **Step 1**: Implement `bestThreeOfFour(ctList: List<Float>): Float` summing the highest three grades.
- **Step 2**: Compute term grade point averages using standard scale points:
  - $A+ \rightarrow 4.00$, $A \rightarrow 3.75$, $A- \rightarrow 3.50$, $B+ \rightarrow 3.25$, $B \rightarrow 3.00$, $B- \rightarrow 2.75$, $C+ \rightarrow 2.50$, $C \rightarrow 2.25$, $D \rightarrow 2.00$, $F \rightarrow 0.00$.
- **Step 3**: Write cumulative GPA engine accounting for credit weighting:
  $$\text{CGPA} = \frac{\sum (\text{Term GPA} \times \text{Term Credits})}{\sum \text{Term Credits}}$$

#### Phase 2: Predictive Grade Target Engine
- **Step 1**: Compute internal score:
  - For 3-credit: $\text{Internal} = \text{Best 3 CTs} (60) + \text{Attendance} (30) = 90$ max.
  - For 4-credit: $\text{Internal} = \text{Best 3 CTs} (80) + \text{Attendance} (40) = 120$ max.
- **Step 2**: Calculate required written exam marks to secure target grade:
  - $\text{Required Marks} = \text{Total Marks} \times \text{Grade Percentage} - \text{Internal Score}$.
  - Bound the target between $0$ and maximum written marks (210 for 3-credit, 280 for 4-credit).
  - If required written marks exceed maximum written marks, tag grade as "Impossible".

---

## Module 3: PDF Document Generation Engine

### 1. Goal
Generate clean, highly formatted PDF documents locally and enable share workflows.

### 2. Scope
- Native Android `PdfDocument` canvas rendering.
- Content directories and FileProvider setups.

### 3. Implementation Steps

#### Phase 1: PDF Layout Painter
- **Step 1**: Write a helper class `AcademicPdfGenerator.kt`. Use `android.graphics.pdf.PdfDocument` to paint text blocks, headers, result tables, and signature stamps.
- **Step 2**: Save generated documents to the application's internal files cache folder.

#### Phase 2: Android Share Sheets
- **Step 1**: Configure `FileProvider` in `AndroidManifest.xml` for sharing files under directories safely.
- **Step 2**: Launch target intent `Intent.ACTION_SEND` containing file URI, enabling users to export PDFs to WhatsApp, Drive, or Email.

---

## Module 4: Semester Archiver and Sync Engine

### 1. Goal
Export semester records as portable backups and synchronize data securely.

### 2. Scope
- JSON serialization files.
- Folder selection through Android Storage Access Framework (SAF).

### 3. Implementation Steps

#### Phase 1: JSON Package Bundler
- **Step 1**: Implement backup utility to compile courses, grades, tests, and checklists into a nested JSON structure.
- **Step 2**: Add decompression and verification routines to prevent corrupted file imports.

#### Phase 2: Folder Backup & Restore Sync
- **Step 1**: Prompt users to select their cloud sync directory via SAF intent (`Intent.ACTION_OPEN_DOCUMENT_TREE`).
- **Step 2**: Automatically sync package files to the cloud folder when updates occur.

---

# 🔮 Future Improvements

1. **Cloud DB Backup Integration**: Direct integration with Firebase or Supabase database backends for multi-device sync without file-handling.
2. **AI Study Recommendations**: Analyze checklist progress and target grades to recommend study sessions and allocate focus time.
3. **Google Calendar Sync**: Push exam dates, countdown alerts, and syllabus milestones directly into Google Calendar.
4. **Dynamic Syllabus Scraper**: Allow importing syllabus topics using OCR (photo snapshot) or PDF ingestion.
