package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.*
import com.nhbhuiyan.nestify.data.local.entity.AttachmentEntity
import com.nhbhuiyan.nestify.data.local.entity.CategoryEntity
import com.nhbhuiyan.nestify.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    // Categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM schedule_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM schedule_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    // Schedule Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleEntity): Long

    @Update
    suspend fun updateScheduleItem(item: ScheduleEntity)

    @Delete
    suspend fun deleteScheduleItem(item: ScheduleEntity)

    @Query("SELECT * FROM schedule_items WHERE categoryId = :categoryId ORDER BY fromTime ASC")
    fun getScheduleItemsByCategory(categoryId: Long): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedule_items WHERE date = :date OR date IS NULL ORDER BY fromTime ASC")
    fun getScheduleItemsByDate(date: String): Flow<List<ScheduleEntity>>

    // Complex query for daysOfWeek. Room doesn't support array-contains directly on strings easily with @TypeConverter.
    // We use LIKE for simplicity if we store it as "1,2,3".
    @Query("SELECT * FROM schedule_items WHERE daysOfWeek LIKE '%' || :dayOfWeek || '%' ORDER BY fromTime ASC")
    fun getScheduleItemsByDayOfWeek(dayOfWeek: Int): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedule_items WHERE id = :id")
    suspend fun getScheduleItemById(id: Long): ScheduleEntity?

    @Query("SELECT * FROM schedule_items")
    suspend fun getAllScheduleItems(): List<ScheduleEntity>

    @Query("SELECT * FROM schedule_items")
    fun getAllScheduleItemsFlow(): Flow<List<ScheduleEntity>>

    // Attachments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Query("SELECT * FROM schedule_attachments WHERE scheduleId = :scheduleId")
    fun getAttachmentsForSchedule(scheduleId: Long): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM schedule_attachments")
    fun getAllAttachments(): Flow<List<AttachmentEntity>>
}
