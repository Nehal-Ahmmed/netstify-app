package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun getAnnouncements(groupId: String): Flow<List<Announcement>>
    suspend fun postAnnouncement(groupId: String, announcement: Announcement): Result<Unit>
}
