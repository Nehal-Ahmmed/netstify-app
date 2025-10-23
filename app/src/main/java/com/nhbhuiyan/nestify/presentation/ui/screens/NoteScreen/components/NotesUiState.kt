package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components

import com.nhbhuiyan.nestify.domain.model.Note

data class NotesUiState(
    val note: Note? = null,
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
