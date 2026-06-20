package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val credits: Float,
    val level: Int,
    val term: Int,
    val examDate: String = "",
    val finalGrade: String = "Pending",
    val attendanceMarks: Float = 0f
)
