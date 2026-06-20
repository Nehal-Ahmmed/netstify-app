package com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases

import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLibraryItemsUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke(): Flow<List<LibraryItemEntity>> {
        return repository.getAllLibraryItems()
    }
}

class GetLibraryItemsByStatusUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke(status: LibraryItemStatus): Flow<List<LibraryItemEntity>> {
        return repository.getLibraryItemsByStatus(status)
    }
}

class AddLibraryItemUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(item: LibraryItemEntity) {
        repository.insertLibraryItem(item)
    }
}

class DeleteLibraryItemUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(item: LibraryItemEntity) {
        repository.deleteLibraryItem(item)
    }
}
