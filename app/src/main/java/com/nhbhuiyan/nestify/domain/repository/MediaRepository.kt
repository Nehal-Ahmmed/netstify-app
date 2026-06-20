package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.data.local.entity.GalleryCategory
import com.nhbhuiyan.nestify.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun insertMedia(media: MediaEntity): Long
    suspend fun updateMedia(media: MediaEntity)
    suspend fun deleteMedia(media: MediaEntity)
    fun getAllMedia(): Flow<List<MediaEntity>>
    fun getMediaByCategory(category: GalleryCategory): Flow<List<MediaEntity>>
    suspend fun getMediaById(id: Long): MediaEntity?
}
