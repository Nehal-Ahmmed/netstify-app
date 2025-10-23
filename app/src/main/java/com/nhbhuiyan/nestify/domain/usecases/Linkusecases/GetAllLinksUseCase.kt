package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLinksUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke() : Flow<List<Link>>{
        return repository.getAllLinks()
    }
}