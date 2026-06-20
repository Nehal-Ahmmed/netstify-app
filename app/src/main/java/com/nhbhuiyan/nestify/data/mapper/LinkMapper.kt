package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.data.local.entity.LinkFolderEntity
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.LinkFolder

fun LinkEntity.toLink() : Link {
    return Link(
        id = id,
        url = url,
        title = title,
        description = description,
        previewImageUrl = previewImageUrl,
        domain = domain,
        folderId = folderId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPreviewFetched = isPreviewFetched,
        isArchived = isArchived,
        isBookmarked = isBookmarked
    )
}

fun Link.toLinkEntity() : LinkEntity {
    return LinkEntity(
        id = id,
        url = url,
        title = title,
        description = description,
        previewImageUrl = previewImageUrl,
        domain = domain,
        folderId = folderId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPreviewFetched = isPreviewFetched,
        isArchived = isArchived,
        isBookmarked = isBookmarked
    )
}

fun LinkFolderEntity.toLinkFolder() : LinkFolder {
    return LinkFolder(
        id = id,
        name = name,
        icon = icon,
        color = color,
        createdAt = createdAt
    )
}

fun LinkFolder.toLinkFolderEntity() : LinkFolderEntity {
    return LinkFolderEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        createdAt = createdAt
    )
}