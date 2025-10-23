package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val repository: ContentRepository
){
    operator fun invoke() : Flow<List<Note>>{
        return repository.getAllNotes()
    }
}