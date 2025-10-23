package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.viewModel.FileViewModel

@Composable
fun FileDetailDestination(navController: NavController, viewModel: FileViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsState()
    val backStackeEntry = navController.currentBackStackEntry
    val fileId = backStackeEntry?.arguments?.getString("fileId")?.toLongOrNull()

    LaunchedEffect(fileId) {
        viewModel.getFileById(fileId)
    }

    state.value.file?.let {
        FileDetailScreen(
            file = it,
            onBack = { navController.navigateUp() },
            navController = navController
        )
    } ?: run { LoadingShimmer() }
}