package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components

import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.FileFolder

data class fileFolderScreenState(
    val files: List<File> = emptyList(),
    val pdfFolders : List<FileFolder> = emptyList(),
    val photoFolders : List<FileFolder> = emptyList(),
    val documentFolders : List<FileFolder> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null,
    )
