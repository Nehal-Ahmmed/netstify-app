package com.nhbhuiyan.nestify.domain.model

import kotlinx.datetime.Instant


data class Link(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val previewImageUrl: String? = null,
    val domain: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isPreviewFetched: Boolean = false,
    val isArchived: Boolean = false
)