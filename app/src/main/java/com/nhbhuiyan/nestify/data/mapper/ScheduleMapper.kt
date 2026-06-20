package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.AttachmentEntity
import com.nhbhuiyan.nestify.data.local.entity.CategoryEntity
import com.nhbhuiyan.nestify.data.local.entity.ScheduleEntity
import com.nhbhuiyan.nestify.domain.model.ScheduleAttachment
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory
import com.nhbhuiyan.nestify.domain.model.ScheduleItem

fun CategoryEntity.toScheduleCategory(): ScheduleCategory {
    return ScheduleCategory(
        id = id,
        name = name,
        colorHex = colorHex,
        iconRes = iconRes
    )
}

fun ScheduleCategory.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        colorHex = colorHex,
        iconRes = iconRes
    )
}

fun ScheduleEntity.toScheduleItem(): ScheduleItem {
    return ScheduleItem(
        id = id,
        categoryId = categoryId,
        title = title,
        description = description,
        fromTime = fromTime,
        toTime = toTime,
        date = date,
        daysOfWeek = daysOfWeek,
        repeatStrategy = repeatStrategy,
        reminderType = reminderType,
        customAudioUri = customAudioUri,
        attachmentUri = attachmentUri,
        isAutoTask = isAutoTask,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun ScheduleItem.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = id,
        categoryId = categoryId,
        title = title,
        description = description,
        fromTime = fromTime,
        toTime = toTime,
        date = date,
        daysOfWeek = daysOfWeek,
        repeatStrategy = repeatStrategy,
        reminderType = reminderType,
        customAudioUri = customAudioUri,
        attachmentUri = attachmentUri,
        isAutoTask = isAutoTask,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun AttachmentEntity.toScheduleAttachment(): ScheduleAttachment {
    return ScheduleAttachment(
        id = id,
        scheduleId = scheduleId,
        type = type,
        uri = uri
    )
}

fun ScheduleAttachment.toAttachmentEntity(): AttachmentEntity {
    return AttachmentEntity(
        id = id,
        scheduleId = scheduleId,
        type = type,
        uri = uri
    )
}
