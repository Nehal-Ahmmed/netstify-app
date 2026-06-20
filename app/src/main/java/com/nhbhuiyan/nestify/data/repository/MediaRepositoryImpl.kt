package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.MediaDao
import com.nhbhuiyan.nestify.data.local.entity.GalleryCategory
import com.nhbhuiyan.nestify.data.local.entity.MediaEntity
import com.nhbhuiyan.nestify.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao
) : MediaRepository {
    override suspend fun insertMedia(media: MediaEntity) = mediaDao.insertMedia(media)
    override suspend fun updateMedia(media: MediaEntity) = mediaDao.updateMedia(media)
    override suspend fun deleteMedia(media: MediaEntity) = mediaDao.deleteMedia(media)
    override fun getAllMedia(): Flow<List<MediaEntity>> = mediaDao.getAllMedia()
    override fun getMediaByCategory(category: GalleryCategory): Flow<List<MediaEntity>> = mediaDao.getMediaByCategory(category)
    override suspend fun getMediaById(id: Long): MediaEntity? = mediaDao.getMediaById(id)
}
