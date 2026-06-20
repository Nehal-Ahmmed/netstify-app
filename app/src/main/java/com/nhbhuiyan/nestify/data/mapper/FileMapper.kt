package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.data.local.entity.FileFolderEntity
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.FileFolder

fun FileEntity.toFile(): File {
    return File(
        id = id,
        uri = uri,
        fileName = fileName,
        fileType = fileType,
        mimeType = mimeType,
        fileSize = fileSize,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        isBookmarked = isBookmarked,
        folderId = folderId
    )
}

fun File.toFileEntity(): FileEntity {
    return FileEntity(
        id = id,
        uri = uri,
        fileName = fileName,
        fileType = fileType,
        mimeType = mimeType,
        folderId = folderId,
        fileSize = fileSize,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        isBookmarked = isBookmarked
    )
}

fun FileFolderEntity.toFileFolder(): FileFolder {
    return FileFolder(
        id = id,
        name = name,
        color = color,
        isCustom = isCustom,
        category = category,
        icon = icon,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun FileFolder.toFileFolderEntity(): FileFolderEntity {
    return FileFolderEntity(
        id = id,
        name = name,
        color = color,
        isCustom = isCustom,
        category = category,
        icon = icon,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}