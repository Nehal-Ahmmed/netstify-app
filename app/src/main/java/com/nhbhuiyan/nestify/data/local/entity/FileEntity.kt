package com.nhbhuiyan.nestify.data.local.entity

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
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isArchived: Boolean = false
): BaseContentEntity(createdAt,updatedAt,isArchived)
