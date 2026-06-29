# Professional Schedule Engine Implementation Guide

## 1. 🗺️ High-Level Roadmap
1. **Phase 1: Data & Domain Setup:** Implemented `ScheduleEntity`, `CategoryEntity`, DAOs, and Repositories to handle robust database operations via Room.
2. **Phase 2: Reminder Engine:** Created `AlarmScheduler`, `AlarmService`, `ScheduleAlarmReceiver`, and `NotificationHelper` to precisely track schedules and notify the user when a schedule triggers. Hooked up necessary hardware vibrations and permissions in AndroidManifest.
3. **Phase 3: Presentation Layer:** Developed the custom Canvas-based `NotebookSpreadComponent`, state-managing `ScheduleViewModel` (Hilt), the full `ScheduleScreen` using a `HorizontalPager`, and a Material 3 `ScheduleCreationSheet` for entering schedule data.

## 2. 🧠 Logical Descriptions

**Simple:**
Think of the new system like a real notebook. You flip pages (tabs) between categories like "Study" or "Job Prep". You can jot down time-blocks on the lined pages. Once jotted down, an invisible butler waits patiently and alerts you with a ring and vibration when it's time to start.

**Technical:**
- **Backend (Data/Domain):** Utilizing Clean Architecture principles, Room serves as the single source of truth. Repositories supply `Flow` data to the ViewModel. `AlarmManager` sets exact RTC alarms using `PendingIntent`. When triggered, a `BroadcastReceiver` awakens a Foreground `Service` displaying a high-priority notification with hardware vibrations.
- **Frontend (UI):** Entirely Jetpack Compose. `HorizontalPager` iterates through Category tabs. A custom `Canvas` composes continuous horizontal lines and vertical margins. State is observed via Kotlin `StateFlow`. `ModalBottomSheet` acts as the creation dialog for creating new elements and parsing them into domain models.

## 3. 💻 Full Implementation Code

**ScheduleViewModel.kt**
```kotlin
package com.nhbhuiyan.nestify.presentation.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import com.nhbhuiyan.nestify.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ScheduleCategory>>(emptyList())
    val categories: StateFlow<List<ScheduleCategory>> = _categories.asStateFlow()

    private val _scheduleItems = MutableStateFlow<List<ScheduleItem>>(emptyList())
    val scheduleItems: StateFlow<List<ScheduleItem>> = _scheduleItems.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collectLatest { catList ->
                _categories.value = catList
                if (catList.isNotEmpty() && _selectedCategoryId.value == null) {
                    selectCategory(catList.first().id)
                }
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
        loadScheduleItems(categoryId)
    }

    private fun loadScheduleItems(categoryId: Long) {
        viewModelScope.launch {
            repository.getScheduleItemsByCategory(categoryId).collectLatest { items ->
                _scheduleItems.value = items
            }
        }
    }

    fun addScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            repository.insertScheduleItem(item)
        }
    }

    fun addCategory(category: ScheduleCategory) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }
}
```

**NotebookSpreadComponent.kt**
```kotlin
package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NotebookSpreadComponent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val paperColor = Color(0xFFFDFBF7)
    val marginColor = Color(0xFFFF7E7E)
    val lineColor = Color(0xFFE0E0E0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(paperColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val lineSpacing = 40.dp.toPx()
            val marginPosition = 60.dp.toPx()

            var currentY = lineSpacing
            while (currentY < height) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, currentY),
                    end = Offset(width, currentY),
                    strokeWidth = 1f
                )
                currentY += lineSpacing
            }

            drawLine(
                color = marginColor,
                start = Offset(marginPosition, 0f),
                end = Offset(marginPosition, height),
                strokeWidth = 2f
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
```

**ScheduleAlarmReceiver.kt**
```kotlin
package com.nhbhuiyan.nestify.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("EXTRA_SCHEDULE_ID", -1L)
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Schedule Alarm"

        if (scheduleId != -1L) {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("EXTRA_SCHEDULE_ID", scheduleId)
                putExtra("EXTRA_MESSAGE", message)
            }
            context.startForegroundService(serviceIntent)
        }
    }
}
```

## 4. 🛠️ Extra Steps
- **Permissions:** You must declare `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `FOREGROUND_SERVICE`, `RECORD_AUDIO`, and `WAKE_LOCK` in the `AndroidManifest.xml` (already added).
- **Runtime Notifications:** When running on Android 13+ (API 33+), ensure you request `POST_NOTIFICATIONS` runtime permission dynamically in your Main Activity. Without it, the Foreground Service will run, but the visible notification will not show up.

## 5. 📝 Summary
1. User clicks the Floating Action Button in `ScheduleScreen.kt`.
2. `ScheduleCreationSheet.kt` pops up.
3. User fills out details and clicks Save.
4. `ScheduleViewModel.kt` invokes `ScheduleRepository.insertScheduleItem(item)`.
5. The `AndroidAlarmScheduler` calculates the RTC wakeup time and sets an exact Alarm via `AlarmManager`.
6. At the target time, `ScheduleAlarmReceiver` triggers.
7. `AlarmService` activates, buzzing the vibrator motor and posting a high-priority notification.
