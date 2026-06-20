package com.nhbhuiyan.nestify.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri : String,
    val fileName : String,
    val mimeType : String,
    val fileSize : Long,
    val fileType: String,
    val folderId: Long ,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isArchived: Boolean = false,
    val isBookmarked: Boolean = false
): BaseContentEntity(createdAt,updatedAt,isArchived)

@Entity(tableName = "file_folders")
data class FileFolderEntity(
    @PrimaryKey(autoGenerate = true) val id:  Long =0,
    val name : String,
    val color: Int,
    val isCustom : Boolean = true,
    val category: String,
    val icon: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
