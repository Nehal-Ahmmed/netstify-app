package com.nhbhuiyan.nestify.domain.usecases.NoteUseCases

import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val repository: ContentRepository
){
suspend operator fun invoke(
    title : String,
    content : String,
    tags: List<String> = emptyList()
) : Long {
    val now = Clock.System.now()
    val note= Note(
        title = title,
        content = content,
        createdAt = now,
        updatedAt = now,
        tags = tags,
    )
    return repository.createNote(note)
}
}