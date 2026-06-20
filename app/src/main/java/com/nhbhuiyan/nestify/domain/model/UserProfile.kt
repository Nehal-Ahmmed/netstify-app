package com.nhbhuiyan.nestify.domain.model

data class UserProfile(
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = "",
    val skills: List<String> = emptyList(),
    val experiences: List<ExperienceData> = emptyList(),
    val projects: List<ProjectData> = emptyList(),
    val email: String = "",
    val linkedin: String = "",
    val github: String = "",
    val website: String = "",
    val twitter: String = "",
    val youtube: String = "",
    val instagram: String = ""
)

data class ExperienceData(
    val title: String = "",
    val company: String = "",
    val duration: String = "",
    val description: String = "",
    val logoUrl: String = ""
)

data class ProjectData(
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val link: String = ""
)
