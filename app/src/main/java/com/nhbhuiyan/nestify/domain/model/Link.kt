package com.nhbhuiyan.nestify.domain.model

import kotlinx.datetime.Instant

data class LinkFolder(
    val id: Long = 0,
    val name: String,
    val icon: String = "folder",
    val createdAt: Instant,
    val color: Int // Hex color for the folder accent
)

data class Link(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val previewImageUrl: String? = null,
    val domain: String,
    val folderId: Long? = null, // Connection to LinkFolder
    val createdAt: Instant,
    val updatedAt: Instant,
    val isPreviewFetched: Boolean = false,
    val isArchived: Boolean = false,
    val isBookmarked: Boolean = false
)