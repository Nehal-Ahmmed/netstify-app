package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases

import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class CreateFileUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(
        uri: String,
        fileName: String,
        fileType: String,
        mimeType: String,
        fileSize: Long
    ):Long{
        val now = Clock.System.now()
        val file = File(
            uri = uri,
            fileName = fileName,
            fileType = fileType,
            mimeType = mimeType,
            fileSize = fileSize,
            createdAt = now,
            updatedAt = now
        )
        return repository.createFile(file)
    }
}