package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "links")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long =0 ,
    val url : String,
    val domain: String,
    val title : String? = null,
    val description : String? = null,
    val previewImageUrl : String? = null,
    val isPreviewFetched : Boolean = false,

    override val createdAt : Instant,
    override val updatedAt : Instant,
    override val isArchived : Boolean = false
) : BaseContentEntity(createdAt = createdAt, updatedAt= updatedAt , isArchived = isArchived)
