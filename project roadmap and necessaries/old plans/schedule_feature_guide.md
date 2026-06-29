# Schedule Feature Implementation Guide 📅✨

Welcome to the comprehensive implementation guide for the **Schedule Feature** in Nestify! This document serves as the ultimate source of truth for the entire Schedule architecture, detailing the logic from the Room database all the way up to the polished Material 3 Compose UI.

---

## 1. 🗺️ High-Level Roadmap

1.  **Deconstruction**: Removed legacy `ClassRoutine` code to make way for a scalable scheduling engine.
2.  **Domain & Data Layer Construction**: Built `ScheduleEntity`, `CategoryEntity`, and `AttachmentEntity`. Defined `ScheduleItem` and created `ScheduleDao` with complex time-based queries.
3.  **Validation Engine**: Implemented robust overlap detection ("One time can never be scheduled two times") directly in the `ScheduleViewModel`.
4.  **Reminder Engine**: Set up `ScheduleAlarmReceiver`, `AlarmService`, and a styled `NotificationHelper` for reliable scheduling.
5.  **Senior Refactoring (Production Polish)**:
    -   Implemented a **Timeline-based UI** for clear daily visualization.
    -   Integrated **Native TimePickers** for precision input.
    -   Created **Adaptive Repeat Pattern UI** for Weekly, Monthly, and Yearly strategies.
    -   Applied **Semantic Color Systems** for category-based visual cues.

---

## 2. 🧠 Logical Descriptions

### 🧱 Backend (Data & Domain)
**Simple Breakdown**: The app saves your schedules like sticky notes in a digital drawer. Before saving a new note, it quickly checks the drawer to make sure you haven't double-booked yourself. It uses "Repeat Strategies" to know if a task should appear every Monday, every 15th, or once a year.

**Technical Breakdown**:
-   **Entities**: `ScheduleEntity` contains time bounds (`fromTime`, `toTime` stored as integer minutes from midnight), relational `categoryId`, and `attachmentUri`.
-   **Repeat Logic**: `RepeatStrategy` enum (ONCE, WEEKLY, MONTHLY, ANNUALLY) dictates how the UI filters items. 
-   **Overlap Logic**: Inside `ScheduleViewModel`, it checks for intersection on `date` or `daysOfWeek`, and validates time bounds: `startA < endB && endA > startB`.

### 🎨 Frontend (UI)
**Simple Breakdown**: A premium, timeline-focused interface. It looks like a high-end productivity app with clear indicators for time, duration, and task type.

**Technical Breakdown**:
-   **Timeline View**: A custom `LazyColumn` where each item is paired with a time indicator and a vertical visual line. Uses `IntrinsicSize.Min` for the timeline spine.
-   **Adaptive Selectors**: 
    -   **Weekly**: 1-7 day toggle.
    -   **Monthly**: 1-31 grid.
    -   **Yearly**: Month navigation + date grid.
-   **Creation Sheet**: Uses `rememberTimePickerState` for a native Android time selection experience.

---

## 3. 💻 Full Implementation Code

### `ScheduleScreen.kt` (Production Timeline UI)
```kotlin
@Composable
fun ScheduleTimelineItem(item: ScheduleItem, categoryColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Time indicator column
        Column(modifier = Modifier.width(60.dp)) { ... }
        // The vertical timeline spine
        Column(modifier = Modifier.fillMaxHeight().padding(horizontal = 12.dp)) {
            Box(modifier = Modifier.size(12.dp).background(categoryColor))
            VerticalDivider(modifier = Modifier.weight(1f).width(2.dp))
        }
        // Task Card
        Card(modifier = Modifier.weight(1f)) { ... }
    }
}
```

### `ScheduleCreationSheet.kt` (Advanced Pattern UI)
```kotlin
when(categoryId) {
    1L -> // Weekly day bubbles
    2L -> // Monthly 31-day grid
    3L -> // Yearly month-swapper + grid
}
```

---

## 4. 🛠️ Extra Steps

1.  **Semantic Colors**: Defined in `Color.kt` (e.g., `ScheduleWeekly`, `ScheduleMonthly`).
2.  **Experimental APIs**: Requires `@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)` for `FlowRow` and `TimePicker`.
3.  **Type Consistency**: All IDs migrated to `Long` for Room compatibility.

---

## 5. 📝 Summary

**Workflow Overview**: 
1. The user selects a category (Weekly/Monthly/Yearly).
2. They use the specialized selector (Day/Date/Month) to view their timeline.
3. To add a task, the `ScheduleCreationSheet` opens with a native `TimePicker`.
4. The system validates overlaps and repeat rules.
5. The task appears in the **Timeline View** with category-specific colors and duration labels.

*Nestify Schedule: Built for the pros.* 🚀
