package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nhbhuiyan.nestify.domain.model.RepeatStrategy
import com.nhbhuiyan.nestify.domain.model.ReminderType
import kotlinx.datetime.Instant

@Entity(
    tableName = "schedule_items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val title: String,
    val description: String,
    val fromTime: Int, // Minutes from midnight
    val toTime: Int,
    val date: String? = null, // "dd/MM"
    
    @field:TypeConverters(Converters::class)
    val daysOfWeek: List<Int> = emptyList(),
    
    @field:TypeConverters(Converters::class)
    val repeatStrategy: RepeatStrategy,
    
    @field:TypeConverters(Converters::class)
    val reminderType: ReminderType,
    
    val customAudioUri: String? = null,
    val attachmentUri: String? = null,
    val isAutoTask: Boolean = false,
    
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isArchived: Boolean = false
) : BaseContentEntity(createdAt = createdAt, updatedAt = updatedAt, isArchived = isArchived)
