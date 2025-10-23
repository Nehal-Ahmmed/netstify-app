package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.viewModel.NotesViewModel

@Composable
fun NoteDetailDestination(navController: NavController, viewmodel: NotesViewModel = hiltViewModel()) {
    val state = viewmodel.uiState.collectAsState()
    val backStackEntry = navController.currentBackStackEntry
    val noteId = backStackEntry?.arguments?.getString("noteId")?.toLongOrNull()
    LaunchedEffect (noteId){
        if (noteId != null) {
            viewmodel.getNoteById(noteId)
        }else{
            Log.d("noteId",noteId.toString())
        }
    }

    state.value.note?.let {
        NoteDetailedScreen(it,onBack = {navController.popBackStack()}, onArchiveToggle = {})
    } ?: run {
        LoadingShimmer()
    }
}