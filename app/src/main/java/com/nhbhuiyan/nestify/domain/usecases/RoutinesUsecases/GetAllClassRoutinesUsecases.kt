package com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases

import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllClassRoutinesUsecases @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(): Flow<List<ClassRoutine>>{
        return repository.getAllRoutines()
    }
}