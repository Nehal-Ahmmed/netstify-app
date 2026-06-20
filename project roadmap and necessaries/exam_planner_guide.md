# 🎓 Exam Planner Feature Guide

## 🗺️ High-Level Roadmap

1.  **Architecture Alignment**: Integrated the Exam Planner into the existing `InAppNav` and `Route` system.
2.  **UI Design**: Designed a "Full Professional" dashboard with a high-impact Hero Section using Nestify's brand gradients.
3.  **Navigation Integration**: Added "Exam Planner" to the Workspace category section on the Home Screen.
4.  **Component Development**:
    *   `ExamPlannerScreen`: Main dashboard with countdowns and progress tracking.
    *   `ExamDetailScreen`: Detailed view for syllabus checklist and logistics.
    *   `ExamFormSheet`: Interactive bottom sheet for scheduling new exams.
5.  **State Management (Mock)**: Implemented mock data structures for immediate visual feedback without backend dependency.

## 🧠 Logical Descriptions

### 🎨 Frontend (Jetpack Compose)
*   **Hero Card**: Uses `NestifyGradients.meshGradient()` to create a stunning, immersive experience. It highlights the most urgent exam with a "T-Minus" countdown.
*   **Exam List**: A clean, white-surface list with shadow elevation. Each item shows subject, type (Final/Midterm/Quiz), date, and a circular progress indicator for syllabus coverage.
*   **Interactive Sheets**: Uses `ModalBottomSheet` for a non-intrusive "Add Exam" flow, featuring custom-styled text fields and chips for syllabus topics.

### ⚙️ Backend (Planned)
*   **Data Flow**: Once implemented, the UI will fetch data from a `Room` database via a `Repository` and `ViewModel`.
*   **Logic**: The "Days Remaining" calculation will be handled in the Domain layer using `kotlinx-datetime`.

## 💻 Full Implementation Code

### 1. Navigation Setup (`Route.kt`)
```kotlin
object ExamPlanner: Route("examPlanner")
```

### 2. Main Dashboard (`ExamPlannerScreen.kt`)
```kotlin
// ... Imports ...
@Composable
fun ExamPlannerScreen(navController: NavController) {
    // Scaffold with Extended FAB and Stunning Mesh Gradient Hero
    // LazyColumn for upcoming focus and completed exams
}
```

### 3. Exam Detail View (`ExamDetailScreen.kt`)
```kotlin
// ... Imports ...
@Composable
fun ExamDetailScreen(navController: NavController, subjectName: String) {
    // Features Circular Progress, Logistics Grid, and Syllabus Checklist
}
```

### 4. Add Exam Form (`ExamFormSheet.kt`)
```kotlin
// ... Imports ...
@Composable
fun ExamFormSheet(sheetState: SheetState, onDismissRequest: () -> Unit, onSaveExam: (MockExamData) -> Unit) {
    // Beautifully styled form with Date/Time pickers and Syllabus Topic builder
}
```

## 🛠️ Extra Steps

*   **Resources**: Ensure `R.drawable.exammarks` is present in the project (it is already included).
*   **Theme**: Ensure `NestifyGradients` is available in `ui.theme` (it is already included).

## 📝 Summary

The **Exam Planner** is now the central hub for academic success in Nestify. Users can navigate from the Home screen's Workspace section directly to a stunning dashboard. The data flows from the UI interactions (adding an exam via the BottomSheet) to a (future) persistent storage layer, while the UI provides immediate visual motivation through progress bars and countdowns.

---
*Created by Gemini CLI - Professional Edition* 🚀
