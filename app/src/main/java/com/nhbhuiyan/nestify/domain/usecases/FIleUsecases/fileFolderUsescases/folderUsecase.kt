package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases

import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow

class createFolderUsecase(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(fileFolder: FileFolder) : Long{
        return repository.insertFileFolder(fileFolder = fileFolder)
    }
}

class getFoldersByCategoryUsecase(
    private val repository: ContentRepository
){
    operator fun invoke(category: String): Flow<List<FileFolder>>{
        return repository.getFoldersByCategory(category = category)
    }
}

class getFilesByFolderUsecase(
    private val repository: ContentRepository
){
    operator fun invoke(folderId: Long): Flow<List<File>>{
        return repository.getFilesByFolder(folderId = folderId)
    }
}