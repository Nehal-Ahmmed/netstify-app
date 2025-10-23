package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components

import com.nhbhuiyan.nestify.domain.model.Link

data class LinkUiState(
    val link: Link? = null,
    val error: String? = null,
    val links: List<Link> = emptyList(),
    val isLoading: Boolean = true
)
