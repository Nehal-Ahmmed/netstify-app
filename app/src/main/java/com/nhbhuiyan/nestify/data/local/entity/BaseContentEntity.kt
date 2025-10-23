package com.nhbhuiyan.nestify.data.local.entity

import androidx.room.ColumnInfo
import kotlinx.datetime.Instant

abstract class BaseContentEntity(
    open val createdAt: Instant,
    open val updatedAt: Instant,
    open val isArchived: Boolean = false
)