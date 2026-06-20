package com.nhbhuiyan.nestify.domain.model

import kotlinx.datetime.Instant

data class ScheduleItem(
    val id: Long = 0,
    val categoryId: Long,
    val title: String,
    val description: String,
    val fromTime: Int, // Minutes from midnight
    val toTime: Int,
    val date: String? = null,
    val daysOfWeek: List<Int> = emptyList(),
    val repeatStrategy: RepeatStrategy,
    val reminderType: ReminderType,
    val customAudioUri: String? = null,
    val attachmentUri: String? = null,
    val isAutoTask: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isArchived: Boolean = false
) {
    val totalDuration: String
        get() {
            val durationMinutes = if (toTime >= fromTime) {
                toTime - fromTime
            } else {
                (1440 - fromTime) + toTime
            }
            val hours = durationMinutes / 60
            val minutes = durationMinutes % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
}
