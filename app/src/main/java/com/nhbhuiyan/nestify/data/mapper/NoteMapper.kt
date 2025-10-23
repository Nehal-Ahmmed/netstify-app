package com.nhbhuiyan.nestify.data.mapper

import com.nhbhuiyan.nestify.data.local.entity.NoteEntity
import com.nhbhuiyan.nestify.domain.model.Note

fun NoteEntity.toNote() : Note {
    return Note(
        id=id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        tags = tags
    )
}

fun Note.toNoteEntity() : NoteEntity{
    return NoteEntity(
        id=id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        tags = tags
    )
}