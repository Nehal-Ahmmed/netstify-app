package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.ProfileEntity
import com.nhbhuiyan.nestify.domain.model.ExperienceData
import com.nhbhuiyan.nestify.domain.model.ProjectData
import com.nhbhuiyan.nestify.domain.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

fun ProfileEntity.toUserProfile(): UserProfile {
    return UserProfile(
        name = name,
        jobTitle = jobTitle,
        location = location,
        bio = bio,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        skills = if (skills.isEmpty()) emptyList() else skills.split(",").map { it.trim() },
        experiences = experiencesJson.toExperienceList(),
        projects = projectsJson.toProjectList(),
        email = email,
        linkedin = linkedin,
        github = github,
        website = website,
        twitter = twitter,
        youtube = youtube,
        instagram = instagram
    )
}

fun UserProfile.toProfileEntity(): ProfileEntity {
    return ProfileEntity(
        id = 1L,
        name = name,
        jobTitle = jobTitle,
        location = location,
        bio = bio,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        skills = skills.joinToString(","),
        experiencesJson = experiences.toJson(),
        projectsJson = projects.toJson(),
        email = email,
        linkedin = linkedin,
        github = github,
        website = website,
        twitter = twitter,
        youtube = youtube,
        instagram = instagram
    )
}

@JvmName("experienceDataToJson")
fun List<ExperienceData>.toJson(): String {
    val arr = JSONArray()
    forEach { exp ->
        arr.put(JSONObject().apply {
            put("title", exp.title)
            put("company", exp.company)
            put("duration", exp.duration)
            put("description", exp.description)
            put("logoUrl", exp.logoUrl)
        })
    }
    return arr.toString()
}

fun String.toExperienceList(): List<ExperienceData> {
    if (isEmpty() || this == "[]") return emptyList()
    return try {
        val arr = JSONArray(this)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            ExperienceData(
                title = obj.optString("title", ""),
                company = obj.optString("company", ""),
                duration = obj.optString("duration", ""),
                description = obj.optString("description", ""),
                logoUrl = obj.optString("logoUrl", "")
            )
        }
    } catch (_: Exception) { emptyList() }
}

@JvmName("projectDataToJson")
fun List<ProjectData>.toJson(): String {
    val arr = JSONArray()
    forEach { proj ->
        arr.put(JSONObject().apply {
            put("name", proj.name)
            put("description", proj.description)
            put("imageUrl", proj.imageUrl)
            put("link", proj.link)
        })
    }
    return arr.toString()
}

fun String.toProjectList(): List<ProjectData> {
    if (isEmpty() || this == "[]") return emptyList()
    return try {
        val arr = JSONArray(this)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            ProjectData(
                name = obj.optString("name", ""),
                description = obj.optString("description", ""),
                imageUrl = obj.optString("imageUrl", ""),
                link = obj.optString("link", "")
            )
        }
    } catch (_: Exception) { emptyList() }
}
