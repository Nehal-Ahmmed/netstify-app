package com.nhbhuiyan.nestify.domain.model

import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val tags : List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isArchived: Boolean = false
)
