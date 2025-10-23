package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class GetLinkByIdUsecase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(id: Long): Link?{
       return repository.getLinkById(id)
    }
}