package com.nhbhuiyan.nestify.domain.model

data class Announcement(
    val id: String,
    val title: String,
    val body: String,
    val createdBy: String,
    val createdAt: Long? = null,
    val priority: String = "low" // "low", "medium", "high"
)
