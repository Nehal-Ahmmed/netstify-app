package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.domain.model.File

fun FileEntity.toFile() : File{
    return File(
        id = id,
        uri = uri,
        fileName = fileName,
        fileType = fileType,
        mimeType = mimeType,
        fileSize = fileSize,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun File.toFileEntity() : FileEntity{
    return FileEntity(
        id = id,
        uri = uri,
        fileName = fileName,
        fileType = fileType,
        mimeType = mimeType,
        fileSize = fileSize,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}