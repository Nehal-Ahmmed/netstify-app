package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    suspend fun insertLibraryItem(item: LibraryItemEntity): Long
    suspend fun updateLibraryItem(item: LibraryItemEntity)
    suspend fun deleteLibraryItem(item: LibraryItemEntity)
    fun getAllLibraryItems(): Flow<List<LibraryItemEntity>>
    fun getLibraryItemsByStatus(status: LibraryItemStatus): Flow<List<LibraryItemEntity>>
    suspend fun getLibraryItemById(id: Long): LibraryItemEntity?
}
