package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components

import com.nhbhuiyan.nestify.domain.model.File

data class FileUiState(
    val file: File ?= null,
    val error: String? = null,
    val files: List<File> = emptyList(),
    val isLoading: Boolean = true
)
