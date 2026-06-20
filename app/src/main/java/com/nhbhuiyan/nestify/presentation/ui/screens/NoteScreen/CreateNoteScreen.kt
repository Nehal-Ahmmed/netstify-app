package com.nhbhuiyan.nestify.presentation.ui.screens.createnote

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.presentation.ui.components.DynamicUserFrameNotebook
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.CreateNoteState
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.CreateNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: CreateNoteViewModel = hiltViewModel()
) {
    val currentBackstack = navController.currentBackStackEntry
    val noteId = currentBackstack?.arguments?.getString("noteId")?.toLongOrNull()
    val uiState by viewModel.uiState.collectAsState()
    var createOrUpdate by remember { mutableStateOf(status.Create) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNote(noteId)
            createOrUpdate = status.Update
        } else {
            // Auto-generate title for sticky notes since there's no visible title field
            viewModel.updateTitle("Sticky Note ${System.currentTimeMillis() % 10000}")
        }
    }
    
    // Handle navigation after note creation
    LaunchedEffect(uiState.isNoteCreated) {
        if (uiState.isNoteCreated) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CreateNoteTopBar(
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    // Update title based on content if it's empty
                    if (uiState.title.isEmpty() || uiState.title.startsWith("Sticky Note")) {
                        val firstWords = uiState.content.split(" ").take(3).joinToString(" ")
                        val newTitle = if (firstWords.isNotBlank()) firstWords else "New Sticky Note"
                        viewModel.updateTitle(newTitle)
                    }
                    
                    when(createOrUpdate){
                        status.Create -> {
                            viewModel.createNote()
                            Log.d("CreateNoteScreen", "Note created")
                        }
                        status.Update -> {
                            viewModel.updateNote()
                            Log.d("CreateNoteScreen", "Note updated")
                        }
                    }
                },
                isLoading = uiState.isLoading
            )
        },
        containerColor = Color(0xFFF8FAFD)
    ) { paddingValues ->
        CreateNoteContent(
            uiState = uiState,
            onContentChange = { viewModel.updateContent(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteTopBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isLoading: Boolean
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Write a Note",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                    fontSize = 32.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onSaveClick,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Note",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFF8FAFD),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun CreateNoteContent(
    uiState: CreateNoteState,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.a2y6_21s0_220127)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Sticky Note Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f) // Slightly taller than square for the note pad
                .padding(16.dp)
        ) {
            DynamicUserFrameNotebook(
                frameBitmap = backgroundBitmap,
                text = uiState.content,
                onTextChange = onContentChange,
                readOnly = false,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

enum class status {
    Create,
    Update
}