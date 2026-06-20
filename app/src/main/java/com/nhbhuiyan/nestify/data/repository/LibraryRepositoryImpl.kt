package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.LibraryItemDao
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val libraryItemDao: LibraryItemDao
) : LibraryRepository {
    override suspend fun insertLibraryItem(item: LibraryItemEntity) = libraryItemDao.insertLibraryItem(item)
    override suspend fun updateLibraryItem(item: LibraryItemEntity) = libraryItemDao.updateLibraryItem(item)
    override suspend fun deleteLibraryItem(item: LibraryItemEntity) = libraryItemDao.deleteLibraryItem(item)
    override fun getAllLibraryItems(): Flow<List<LibraryItemEntity>> = libraryItemDao.getAllLibraryItems()
    override fun getLibraryItemsByStatus(status: LibraryItemStatus): Flow<List<LibraryItemEntity>> = libraryItemDao.getLibraryItemsByStatus(status)
    override suspend fun getLibraryItemById(id: Long): LibraryItemEntity? = libraryItemDao.getLibraryItemById(id)
}
