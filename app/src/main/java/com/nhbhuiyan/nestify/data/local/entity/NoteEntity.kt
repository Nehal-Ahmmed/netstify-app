package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title : String,
    val content : String,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isArchived: Boolean = false,

    @field:TypeConverters(Converters::class)
    val tags: List<String> = emptyList()
) : BaseContentEntity(createdAt = createdAt, updatedAt= updatedAt, isArchived = isArchived )