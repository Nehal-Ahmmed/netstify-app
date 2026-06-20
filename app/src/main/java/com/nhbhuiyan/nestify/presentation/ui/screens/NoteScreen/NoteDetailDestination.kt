package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.NotesViewModel

@Composable
fun NoteDetailDestination(navController: NavController, viewmodel: NotesViewModel = hiltViewModel()) {
    val state by viewmodel.uiState.collectAsState()
    val backStackEntry = navController.currentBackStackEntry
    val noteId = backStackEntry?.arguments?.getString("noteId")?.toLongOrNull()

    val initialLoad = remember { mutableStateOf(true) }

    LaunchedEffect(noteId){
        if (noteId != null && initialLoad.value) {
            Log.d("NOTE_DETAIL", "🔄 Loading note with ID: $noteId")
            viewmodel.getNoteById(noteId)
            initialLoad.value = false
        } else {
            Log.d("NOTE_DETAIL", "❌ Note ID is null: $noteId")
        }
    }

    Log.d("NOTE_DETAIL", "📱 Composable state - isLoading: ${state.isLoading}, note: ${state.note}")

    // Show loading only if we're actively loading AND don't have a note yet
    if (state.isLoading && state.note == null) {
        Log.d("NOTE_DETAIL", "📱 Showing loading shimmer")
        LoadingShimmer()
    } else if (state.note != null) {
        Log.d("NOTE_DETAIL", "📱 Showing note detail screen for: ${state.note!!.title}")
        NoteDetailedScreen(
            note = state.note!!,
            onBack = { navController.popBackStack() },
            onEditClick = { noteId->
                Log.d("NOTE_DETAIL", "✏️ Editing note: ${noteId}")
                navController.navigate(Route.createNote.createRoute(noteId = noteId))
            },
            onBookmarkToggle = { isBookmarked ->
                Log.d("NOTE_DETAIL", "⭐ Bookmark toggle: $isBookmarked for note ${state.note!!.id}")
                viewmodel.bookmarkNote(
                    id = state.note!!.id,
                    isBookmarked = isBookmarked
                )
            }
        )
    } else {
        Log.d("NOTE_DETAIL", "📱 Showing 'Note not found'")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Note not found - ID: $noteId")
        }
    }
}