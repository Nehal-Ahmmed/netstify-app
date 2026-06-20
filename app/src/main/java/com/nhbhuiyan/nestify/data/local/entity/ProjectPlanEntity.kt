package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_plans")
data class ProjectPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: Int,
    val title: String,
    val description: String,
    val ideas: Int,
    val completed: Int,
    val workingWith: String
)
