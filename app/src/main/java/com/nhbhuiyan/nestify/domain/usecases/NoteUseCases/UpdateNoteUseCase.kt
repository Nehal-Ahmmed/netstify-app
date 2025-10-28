package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(note: Note){
        repository.updateNote(note)
    }
}