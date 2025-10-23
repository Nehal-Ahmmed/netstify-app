package com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.viewModel.ClassroutineViewModel

@Composable
fun RoutineDetailDestination(
    navController: NavController,
    viewModel: ClassroutineViewModel = hiltViewModel()
) {
    // Get the backStackEntry
    val backStackEntry = navController.currentBackStackEntry
    val routineId = backStackEntry?.arguments?.getString("routineId")?.toLongOrNull()
    var routine by remember { mutableStateOf<ClassRoutine?>(null) }
    // Use LaunchedEffect to load the routine
    val state = viewModel.state.collectAsState()
    Log.d("routine id", state.value.routine?.id.toString())
    LaunchedEffect(routineId) {
        viewModel.getRoutineById(routineId)
        Log.d("routine id", routineId.toString())
    }

    state.value.routine?.let {
        RoutineDetailScreen(
            routine = it,
            onBack = { navController.popBackStack() }
        )
    } ?: run {
       LoadingShimmer()
    }
}