package com.nhbhuiyan.nestify.domain.model

data class ScheduleAttachment(
    val id: Long = 0,
    val scheduleId: Long,
    val type: AttachmentType,
    val uri: String
)
