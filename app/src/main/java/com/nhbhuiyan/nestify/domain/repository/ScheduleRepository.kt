package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.ScheduleAttachment
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    // Categories
    suspend fun insertCategory(category: ScheduleCategory): Long
    suspend fun updateCategory(category: ScheduleCategory)
    suspend fun deleteCategory(category: ScheduleCategory)
    fun getAllCategories(): Flow<List<ScheduleCategory>>
    suspend fun getCategoryById(id: Long): ScheduleCategory?

    // Schedule Items
    suspend fun insertScheduleItem(item: ScheduleItem): Long
    suspend fun updateScheduleItem(item: ScheduleItem)
    suspend fun deleteScheduleItem(item: ScheduleItem)
    fun getScheduleItemsByCategory(categoryId: Long): Flow<List<ScheduleItem>>
    fun getScheduleItemsByDate(date: String): Flow<List<ScheduleItem>>
    fun getScheduleItemsByDayOfWeek(dayOfWeek: Int): Flow<List<ScheduleItem>>
    suspend fun getScheduleItemById(id: Long): ScheduleItem?
    suspend fun getAllScheduleItems(): List<ScheduleItem>
    fun getAllScheduleItemsFlow(): Flow<List<ScheduleItem>>

    // Attachments
    suspend fun insertAttachment(attachment: ScheduleAttachment): Long
    suspend fun deleteAttachment(attachment: ScheduleAttachment)
    fun getAttachmentsForSchedule(scheduleId: Long): Flow<List<ScheduleAttachment>>
}
