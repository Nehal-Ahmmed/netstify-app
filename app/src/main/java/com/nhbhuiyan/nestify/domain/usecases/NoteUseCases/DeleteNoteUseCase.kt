package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(noteId: Long){
        repository.deleteNote(noteId)
    }
}