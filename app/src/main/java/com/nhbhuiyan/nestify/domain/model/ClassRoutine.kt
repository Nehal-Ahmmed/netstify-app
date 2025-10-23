package com.nhbhuiyan.nestify.domain.model

import com.nhbhuiyan.nestify.data.local.entity.Content
import kotlinx.datetime.Instant

data class ClassRoutine(
    val id: Long? = null,
    val content: String,
    val imageUri : String,
    val imageDescription: String
)
