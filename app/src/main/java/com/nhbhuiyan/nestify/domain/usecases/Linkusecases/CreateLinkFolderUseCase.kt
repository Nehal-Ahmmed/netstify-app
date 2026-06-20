package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.model.LinkFolder
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class CreateLinkFolderUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(name: String, color: Int): Long {
        val folder = LinkFolder(
            name = name,
            color = color,
            createdAt = kotlinx.datetime.Clock.System.now()
        )
        return repository.createLinkFolder(folder)
    }
}
