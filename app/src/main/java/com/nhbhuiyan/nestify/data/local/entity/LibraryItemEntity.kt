package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_item_table")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String? = null,
    val linkOrFilePath: String,
    val status: LibraryItemStatus, // TO_READ, READING, COMPLETED
    val itemType: LibraryItemType, // BOOK, ARTICLE, VIDEO_TUTORIAL
    val dateAdded: Long = System.currentTimeMillis()
)

enum class LibraryItemStatus {
    TO_READ, READING, COMPLETED
}

enum class LibraryItemType {
    BOOK, ARTICLE, VIDEO_TUTORIAL, OTHER
}
