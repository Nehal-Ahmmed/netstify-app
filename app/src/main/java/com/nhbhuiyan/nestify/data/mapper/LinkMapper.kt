package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.domain.model.Link

fun LinkEntity.toLink() : Link{
    return Link(
        id = id,
        url = url,
        title = title,
        description = description,
        previewImageUrl = previewImageUrl,
        domain = domain,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPreviewFetched = isPreviewFetched,
        isArchived = isArchived
    )
}

fun Link.toLinkEntity() : LinkEntity{
    return LinkEntity(
        id = id,
        url = url,
        title = title,
        description = description,
        previewImageUrl = previewImageUrl,
        domain = domain,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPreviewFetched = isPreviewFetched,
        isArchived = isArchived
    )
}