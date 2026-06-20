package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        content: String,
        tags: List<String> = emptyList(),
        createdAt: Instant
    ){
        val now = Clock.System.now()
        val note = Note(
            id= id,
            title = title,
            content = content,
            updatedAt = now,
            tags = tags,
            createdAt = createdAt
        )
        repository.updateNote(note)
    }
}