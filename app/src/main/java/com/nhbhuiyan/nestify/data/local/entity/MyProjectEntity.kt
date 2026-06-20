package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_projects")
data class MyProjectEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val motive: String,
    val description: String,
    val features: List<String>,
    val specialities: List<String>,
    val techStack: List<String>,
    val libraries: List<String>,
    val sources: Map<String, String>,
    val brandLogo: Int,
    val demoImages: List<Int>,
    val videoUrl: String?,
    val whereToFind: String,
    val category: String,
    val status: String
)
