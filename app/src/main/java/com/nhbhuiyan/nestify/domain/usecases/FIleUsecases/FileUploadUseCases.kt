package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class FileUploadUseCases @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(
        sourceUri: String,
        fileName: String,
        fileType: String,
        folderId: Long,
        fileSize: Long,
        mimeType: String,
        moveFile: Boolean
    ) : Long {
        return repository.uploadFile(
            sourceUri = sourceUri,
            fileName = fileName,
            fileType = fileType,
            folderId=folderId,
            fileSize = fileSize,
            mimeType = mimeType,
            moveFile = moveFile
        )
    }
}

class CopyFileToAppStorageUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(sourceUri: String, fileName: String): String {
        return repository.copyFileToAppStorage(sourceUri, fileName)
    }
}

class MoveFileToAppStorageUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(sourceUri: String, fileName: String): String {
        return repository.moveFileToAppStorage(sourceUri, fileName)
    }
}

class GetAppFilesDirectoryUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(): java.io.File {
        return repository.getAppFilesDirectory()
    }
}