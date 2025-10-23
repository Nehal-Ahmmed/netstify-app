package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.viewModel.LinksViewmodel

@Composable
fun LinkDetailDestination(navController: NavController, viewModel: LinksViewmodel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState()
    val backStackEntry = navController.currentBackStackEntry
    val linkId = backStackEntry?.arguments?.getString("linkId")?.toLongOrNull()


    LaunchedEffect(linkId) {
        viewModel.getLinkById(id = linkId)
    }

    state.value.link?.let {
        LinkDetailScreen(
            link=it,
            navController = navController,
            onBack = {navController.popBackStack()}
        )
    } ?: run { LoadingShimmer() }
}