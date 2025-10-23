package com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases

import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

class CreateRoutineUsecases @Inject constructor(
    private val repository : ContentRepository
) {
    suspend operator fun invoke(
        content: String,
        imageUri : String,
        imageDescription: String
    ): Long{
        val date: Instant = Clock.System.now()
        val classRoutine = ClassRoutine(
            content = content,
            imageDescription = imageDescription,
            imageUri = imageUri
        )
        return repository.createRoutine(classRoutine)
    }
}