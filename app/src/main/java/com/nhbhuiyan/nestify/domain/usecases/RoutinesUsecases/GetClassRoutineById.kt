package com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases

import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class GetClassRoutineById @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(id: Long) : ClassRoutine? {
        return repository.getRoutineById(id =id )
    }
}