package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components.NoteItem
import com.nhbhuiyan.nestify.presentation.viewModel.NotesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController) {
    val viewModel: NotesViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createNote("New Note", "This is a new note", tags = listOf("Nehal","Bhuiyan","Hussain","mehedi","Nehal","Bhuiyan","Hussain","mehedi"))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        if (state.value.isLoading) {
            LoadingShimmer()
        } else {
            NotesList(
                notes = state.value.notes,
                navController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun NotesList(notes: List<Note>, navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(notes.size) { index ->
            NoteItem(
                note = notes[index],
                onClick = {
                    Log.d("noteId", notes[index].id.toString())
                    navController.navigate(Route.NoteDetail.createRoute(notes[index].id))
                })
            if (index < notes.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

