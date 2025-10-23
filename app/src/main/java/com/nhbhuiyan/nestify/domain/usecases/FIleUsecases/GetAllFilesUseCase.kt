package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class GetAllFilesUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke() = repository.getAllFiles()
}