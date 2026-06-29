package com.nhbhuiyan.nestify.presentation.ui.screens.FacouritesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.navigateToDetail
import com.nhbhuiyan.nestify.presentation.ui.screens.FacouritesScreen.data.FavoritesViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.common.FilterFeedContent
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme

@Composable
fun FacouritesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors
    val state by viewModel.state.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(title = "Favorites", onBack = { navController.popBackStack() })
        FilterFeedContent(
            items = state.items,
            isLoading = state.isLoading,
            emptyIcon = Icons.Outlined.Star,
            emptyTitle = "No favorites yet",
            emptyDescription = "Star a note, link or file to pin it here for quick access.",
            onItemClick = { item -> navigateToDetail(navController, item.type, item.id) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
