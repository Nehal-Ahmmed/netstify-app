package com.nhbhuiyan.nestify.domain.model

import kotlinx.datetime.Instant


data class File(
    val id: Long = 0,
    val uri: String,
    val fileName: String,
    val fileType: String,
    val mimeType: String,
    val fileSize: Long,
    val folderId: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isArchived: Boolean = false,
    val isBookmarked: Boolean = false

)

data class FileFolder(
    val id: Long = 0,
    val name: String,
    val color: Int,
    val isCustom: Boolean = true,
    val category: String,
    val icon: String,
    val createdAt: Instant,
    val updatedAt: Instant
)