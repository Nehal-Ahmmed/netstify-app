package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class DeleteLinkUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator suspend fun invoke(id: Long){
        repository.deleteLink(id)
    }
}