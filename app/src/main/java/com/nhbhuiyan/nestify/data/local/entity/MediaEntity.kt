package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_table")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val filePath: String,
    val type: MediaType, // Enum or string for PHOTO, VIDEO, AUDIO
    val galleryCategory: GalleryCategory, // PERSONAL, FORMAL, NORMAL
    val dateAdded: Long = System.currentTimeMillis()
)

enum class MediaType {
    PHOTO, VIDEO, AUDIO
}

enum class GalleryCategory {
    PERSONAL, FORMAL, NORMAL
}
