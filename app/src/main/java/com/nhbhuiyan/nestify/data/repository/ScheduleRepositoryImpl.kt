package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.ScheduleDao
import com.nhbhuiyan.nestify.data.mapper.*
import com.nhbhuiyan.nestify.domain.model.ScheduleAttachment
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import com.nhbhuiyan.nestify.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override suspend fun insertCategory(category: ScheduleCategory): Long {
        return scheduleDao.insertCategory(category.toCategoryEntity())
    }

    override suspend fun updateCategory(category: ScheduleCategory) {
        scheduleDao.updateCategory(category.toCategoryEntity())
    }

    override suspend fun deleteCategory(category: ScheduleCategory) {
        scheduleDao.deleteCategory(category.toCategoryEntity())
    }

    override fun getAllCategories(): Flow<List<ScheduleCategory>> {
        return scheduleDao.getAllCategories().map { entities ->
            entities.map { it.toScheduleCategory() }
        }
    }

    override suspend fun getCategoryById(id: Long): ScheduleCategory? {
        return scheduleDao.getCategoryById(id)?.toScheduleCategory()
    }

    override suspend fun insertScheduleItem(item: ScheduleItem): Long {
        return scheduleDao.insertScheduleItem(item.toScheduleEntity())
    }

    override suspend fun updateScheduleItem(item: ScheduleItem) {
        scheduleDao.updateScheduleItem(item.toScheduleEntity())
    }

    override suspend fun deleteScheduleItem(item: ScheduleItem) {
        scheduleDao.deleteScheduleItem(item.toScheduleEntity())
    }

    override fun getScheduleItemsByCategory(categoryId: Long): Flow<List<ScheduleItem>> {
        return scheduleDao.getScheduleItemsByCategory(categoryId).map { entities ->
            entities.map { it.toScheduleItem() }
        }
    }

    override fun getScheduleItemsByDate(date: String): Flow<List<ScheduleItem>> {
        return scheduleDao.getScheduleItemsByDate(date).map { entities ->
            entities.map { it.toScheduleItem() }
        }
    }

    override fun getScheduleItemsByDayOfWeek(dayOfWeek: Int): Flow<List<ScheduleItem>> {
        return scheduleDao.getScheduleItemsByDayOfWeek(dayOfWeek).map { entities ->
            entities.map { it.toScheduleItem() }
        }
    }

    override suspend fun getScheduleItemById(id: Long): ScheduleItem? {
        return scheduleDao.getScheduleItemById(id)?.toScheduleItem()
    }

    override suspend fun getAllScheduleItems(): List<ScheduleItem> {
        return scheduleDao.getAllScheduleItems().map { it.toScheduleItem() }
    }

    override fun getAllScheduleItemsFlow(): Flow<List<ScheduleItem>> {
        return scheduleDao.getAllScheduleItemsFlow().map { entities ->
            entities.map { it.toScheduleItem() }
        }
    }

    override suspend fun insertAttachment(attachment: ScheduleAttachment): Long {
        return scheduleDao.insertAttachment(attachment.toAttachmentEntity())
    }

    override suspend fun deleteAttachment(attachment: ScheduleAttachment) {
        scheduleDao.deleteAttachment(attachment.toAttachmentEntity())
    }

    override fun getAttachmentsForSchedule(scheduleId: Long): Flow<List<ScheduleAttachment>> {
        return scheduleDao.getAttachmentsForSchedule(scheduleId).map { entities ->
            entities.map { it.toScheduleAttachment() }
        }
    }
}
