package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data

import kotlinx.datetime.Instant

data class CreateNoteState(
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: Instant? = null,
    val isLoading: Boolean = false,
    val isNoteCreated: Boolean = false,
    val error: String? = null,
    val id: Long? = null
)
