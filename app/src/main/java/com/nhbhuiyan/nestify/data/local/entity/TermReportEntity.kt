package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "term_reports")
data class TermReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: Int,
    val term: Int,
    val gpa: Float,
    val pdfLocalPath: String,
    val timestamp: Long
)
