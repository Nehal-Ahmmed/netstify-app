package com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.data.ArchiveViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.common.FilterFeedContent
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ArchiveViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors
    val state by viewModel.state.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(title = "Archive", onBack = { navController.popBackStack() })
        FilterFeedContent(
            items = state.items,
            isLoading = state.isLoading,
            emptyIcon = Icons.Outlined.Inventory2,
            emptyTitle = "Nothing archived yet",
            emptyDescription = "Items you archive from Notes, Links and Files will collect here for safe keeping.",
            onItemClick = { item -> navigateToDetail(navController, item.type, item.id) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

internal fun navigateToDetail(navController: NavController, type: String, id: String) {
    val longId = id.toLongOrNull()
    when (type) {
        "note" -> navController.navigate(Route.NoteDetail.createRoute(longId))
        "link" -> navController.navigate(Route.LinkDetail.createRoute(longId))
        "file" -> navController.navigate(Route.FileDetail.createRoute(longId))
    }
}
