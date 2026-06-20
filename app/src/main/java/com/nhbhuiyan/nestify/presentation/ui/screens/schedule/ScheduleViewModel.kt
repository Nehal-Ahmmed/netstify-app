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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.nhbhuiyan.nestify.domain.alarm.AlarmScheduler
import com.nhbhuiyan.nestify.domain.model.ReminderType

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ScheduleCategory>>(emptyList())
    val categories: StateFlow<List<ScheduleCategory>> = _categories.asStateFlow()

    private val _scheduleItemsByCategory = MutableStateFlow<Map<Long, List<ScheduleItem>>>(emptyMap())
    val scheduleItemsByCategory: StateFlow<Map<Long, List<ScheduleItem>>> = _scheduleItemsByCategory.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        loadCategories()
        loadAllScheduleItems()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collectLatest { catList ->
                _categories.value = catList
                if (catList.isNotEmpty() && _selectedCategoryId.value == null) {
                    _selectedCategoryId.value = catList.first().id
                }
            }
        }
    }

    private fun loadAllScheduleItems() {
        viewModelScope.launch {
            repository.getAllScheduleItemsFlow().collectLatest { items ->
                _scheduleItemsByCategory.value = items.groupBy { it.categoryId }
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    private val _overlapWarning = MutableSharedFlow<String>()
    val overlapWarning: SharedFlow<String> = _overlapWarning.asSharedFlow()

    fun addScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            val categoryItems = repository.getAllScheduleItems().filter { it.categoryId == item.categoryId }
            val hasOverlap = categoryItems.any { existing ->
                val sameDate = (item.date != null && existing.date == item.date)
                val sameDayOfWeek = item.daysOfWeek.intersect(existing.daysOfWeek.toSet()).isNotEmpty()
                
                if (sameDate || sameDayOfWeek) {
                    val startA = item.fromTime
                    val endA = item.toTime
                    val startB = existing.fromTime
                    val endB = existing.toTime
                    
                    startA < endB && endA > startB
                } else {
                    false
                }
            }
            
            if (hasOverlap) {
                _overlapWarning.emit("Warning: This schedule overlaps with an existing time block in this category!")
            } else {
                val id = repository.insertScheduleItem(item)
                val insertedItem = item.copy(id = id)
                
                if (insertedItem.reminderType != ReminderType.NONE) {
                    alarmScheduler.schedule(insertedItem)
                }
            }
        }
    }

    fun updateScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            val categoryItems = repository.getAllScheduleItems().filter { 
                it.categoryId == item.categoryId && it.id != item.id 
            }
            val hasOverlap = categoryItems.any { existing ->
                val sameDate = (item.date != null && existing.date == item.date)
                val sameDayOfWeek = item.daysOfWeek.intersect(existing.daysOfWeek.toSet()).isNotEmpty()
                
                if (sameDate || sameDayOfWeek) {
                    val startA = item.fromTime
                    val endA = item.toTime
                    val startB = existing.fromTime
                    val endB = existing.toTime
                    
                    startA < endB && endA > startB
                } else {
                    false
                }
            }

            if (hasOverlap) {
                _overlapWarning.emit("Warning: Update failed. New time overlaps with another schedule!")
            } else {
                repository.updateScheduleItem(item)
                
                // Re-schedule alarm/notification
                alarmScheduler.cancel(item)
                if (item.reminderType != ReminderType.NONE) {
                    alarmScheduler.schedule(item)
                }
            }
        }
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            repository.deleteScheduleItem(item)
            alarmScheduler.cancel(item)
        }
    }

    fun addCategory(category: ScheduleCategory) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }
}
