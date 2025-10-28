package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data

data class CreateNoteState(
    val title: String = "",
    val content : String = "",
    val tags: List<String> = emptyList(),
    val isLoading : Boolean = false,
    val isNoteCreated: Boolean = false,
    val error: String ? = null
)
