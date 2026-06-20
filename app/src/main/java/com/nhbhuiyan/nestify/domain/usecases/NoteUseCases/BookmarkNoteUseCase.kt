package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class BookmarkNoteUseCase @Inject constructor(
    private val repository: ContentRepository
){
    operator suspend fun invoke(noteId: Long, isBookmarked: Boolean){
        repository.bookmarkNote(noteId,isBookmarked)
    }
}