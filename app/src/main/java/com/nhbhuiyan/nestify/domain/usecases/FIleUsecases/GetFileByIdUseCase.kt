package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases

import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class GetFileByIdUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(id: Long): File? {
        return repository.getFileById(id)
    }
}