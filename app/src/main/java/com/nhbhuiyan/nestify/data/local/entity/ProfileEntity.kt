package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class ProfileEntity(
    @PrimaryKey
    val id: Long = 1L,
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = "",
    val skills: String = "",
    val experiencesJson: String = "[]",
    val projectsJson: String = "[]",
    val email: String = "",
    val linkedin: String = "",
    val github: String = "",
    val website: String = "",
    val twitter: String = "",
    val youtube: String = "",
    val instagram: String = ""
)
