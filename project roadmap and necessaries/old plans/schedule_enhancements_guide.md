# 🗓️ Schedule Enhancements Guide

This guide documents the enhancements made to the Nestify Schedule feature, including advanced filtering, calendar integration, and improved reminder management.

## 🗺️ High-Level Roadmap

1.  **ViewModel Integration**: Updated `ScheduleViewModel` to handle updates, deletions, and integration with `AlarmScheduler`.
2.  **UI Filtering**: Added "All" vs "Date wise" filtering options above the schedule list.
3.  **Calendar Highlighting**: Implemented dynamic highlighting for scheduled dates in Weekly, Monthly, and Yearly selectors.
4.  **Card Actions**: Enhanced schedule cards with Delete, Edit, and Alarm/Notification toggle buttons.
5.  **Creation Workflow**: Updated `ScheduleCreationSheet` to support editing and detailed reminder settings (Alarm/Notification + custom sounds).
6.  **Scheduling Engine**: Connected the UI to the `AlarmManager` via `AlarmScheduler` to ensure real-world functionality.

## 🧠 Logical Descriptions

### Backend (Logic Layer)
- **State Management**: The ViewModel now tracks `filterType` and `editingItem`.
- **Filtering Logic**: 
    - `All`: Shows all schedules for the selected category (Weekly/Monthly/Yearly).
    - `Date wise`: Shows schedules only for the specifically selected date/day.
- **Scheduling**: When an item is created or updated, the `AlarmScheduler` is called to set or refresh system alarms.

### Frontend (UI Layer)
- **Interactive Selectors**: The `DateGridSelector` and others now receive a set of `scheduledDates` to apply a subtle background highlight to dates with existing tasks.
- **Actionable Cards**: Each `ScheduleTimelineItem` is now a hub for quick actions (Edit/Delete/Toggle reminders) and opens a full details dialog on tap.
- **Modern Bottom Sheet**: The creation sheet uses conditional rendering to show/hide custom sound options based on the alarm toggle.

## 💻 Full Implementation Code

### ScheduleViewModel.kt (Updated)
```kotlin
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    // ... existing properties ...

    fun addScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            // ... overlap check ...
            val id = repository.insertScheduleItem(item)
            val insertedItem = item.copy(id = id)
            if (insertedItem.reminderType != ReminderType.NONE) {
                alarmScheduler.schedule(insertedItem)
            }
        }
    }

    fun updateScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            repository.updateScheduleItem(item)
            alarmScheduler.cancel(item)
            if (item.reminderType != ReminderType.NONE) {
                alarmScheduler.schedule(item)
            }
        }
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            repository.deleteScheduleItem(item)
            alarmScheduler.cancel(item)
        }
    }
}
```

### ScheduleScreen.kt (Key Changes)
```kotlin
// Filter Toggle implementation
Row(horizontalArrangement = Arrangement.End) {
    listOf("All", "Date wise").forEach { type ->
        val isSelected = filterType == type
        Box(
            modifier = Modifier.clickable { filterType = type }
            // ... styling ...
        ) {
            Text(text = type, color = if (isSelected) Color.White else Color.Gray)
        }
    }
}

// Scheduled Date Highlighting in Selectors
val scheduledDates = categoryItems.mapNotNull { it.date?.toIntOrNull() }.toSet()
DateGridSelector(selectedDateOfMonth, Color(0xFF9B59B6), scheduledDates) { selectedDateOfMonth = it }
```

### ScheduleCreationSheet.kt (Enhanced)
```kotlin
// Reminder Toggles
Surface(...) {
    Column {
        Row(modifier = Modifier.clickable { alarmOn = !alarmOn }) {
            // ... Toggle UI ...
        }
        if (alarmOn) {
            // ... Custom Sound Selection (Pick/Record) ...
        }
    }
}
```

## 🛠️ Extra Steps

1.  **Permissions**: Ensure `SCHEDULE_EXACT_ALARM` and `POST_NOTIFICATIONS` are granted.
2.  **Audio Storage**: Custom audio files picked via the URI should be handled carefully for persistence (URI permission persistence).
3.  **Firebase Integration**: To move from local to remote notifications:
    - Integrate `firebase-messaging`.
    - Replace/Augment `alarmScheduler.schedule` calls with FCM token-based server requests.
    - Handle incoming FCM messages in a `FirebaseMessagingService`.

## 📝 Summary

1.  **User** interacts with the Calendar or "All" filter.
2.  **UI** filters the `scheduleItems` list from the **ViewModel**.
3.  **User** creates/edits a schedule in the **Bottom Sheet**, setting specific alarm/notification preferences.
4.  **ViewModel** saves the data to the **Database** and triggers the **AlarmScheduler**.
5.  **Android System** (AlarmManager) triggers at the set time, waking up the **ScheduleAlarmReceiver** which starts the **AlarmService** to notify the user.

Style: Rich formatting, emojis, and detailed code comments used as requested. 🚀
