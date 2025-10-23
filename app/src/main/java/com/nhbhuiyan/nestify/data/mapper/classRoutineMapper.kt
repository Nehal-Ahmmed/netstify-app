package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.ClassRoutineEntity
import com.nhbhuiyan.nestify.data.local.entity.Day
import com.nhbhuiyan.nestify.domain.model.ClassRoutine

fun ClassRoutineEntity.toClassRoutine() : ClassRoutine{
    return ClassRoutine(
        id = id,
        content = content,
        imageDescription = imageDescription,
        imageUri = imageUri
    )
}

fun ClassRoutine.toClassRoutineEntity() : ClassRoutineEntity{
    return ClassRoutineEntity(
        content = content,
        imageDescription = imageDescription,
        imageUri = imageUri
    )
}