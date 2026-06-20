package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components

import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.LinkFolder

data class LinkUiState(
    val link: Link? = null,
    val error: String? = null,
    val links: List<Link> = emptyList(),
    val folders: List<LinkFolder> = emptyList(),
    val isLoading: Boolean = true
)
