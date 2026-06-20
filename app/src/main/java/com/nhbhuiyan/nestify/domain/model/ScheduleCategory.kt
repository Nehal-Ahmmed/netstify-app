package com.nhbhuiyan.nestify.domain.model

data class ScheduleCategory(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconRes: Int? = null
)

