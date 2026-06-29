package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pyqs",
    foreignKeys = [
        ForeignKey(
            entity = SyllabusTopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class PYQEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val questionText: String? = null,
    val questionImagePath: String? = null,
    val answerText: String? = null,
    val answerImagePath: String? = null,
    val nbFormulas: String? = null,
    val nbTheories: String? = null,
    val nbConstants: String? = null,
    val nbExtras: String? = null,
    val repeatCount: Int = 1,
    val yearsSeen: String = "",
    val marks: String? = null,
    val firestoreId: String? = null
)
