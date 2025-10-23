package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "class_routines")
data class ClassRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    @ColumnInfo(name = "image_uri")
    val imageUri: String,
    @ColumnInfo(name = "image_description")
    val imageDescription: String
)


//unused
enum class Day {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
}

data class Content(
    val startClass: Instant,
    val endClass: Instant,
    val subject: String,
    val teacher: String,
   // val special : String? = null
)
