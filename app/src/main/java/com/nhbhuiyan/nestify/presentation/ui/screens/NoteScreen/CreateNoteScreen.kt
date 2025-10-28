package com.nhbhuiyan.nestify.presentation.ui.screens.createnote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components.NoteTagsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components.NoteTextField
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.CreateNoteState
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.CreateNoteViewModel

/**
 * Create Note Screen - Beautiful UI for creating new notes
 * Follows the Note data class structure with title, content, and tags
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: CreateNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                onSaveClick = { viewModel.createNote() },
                isLoading = uiState.isLoading
            )
        },
        containerColor = Color(0xFFF8FAFD)
    ) { paddingValues ->
        CreateNoteContent(
            uiState = uiState,
            onTitleChange = { viewModel.updateTitle(it) },
            onContentChange = { viewModel.updateContent(it) },
            onTagsChange = { viewModel.updateTags(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Top App Bar for Create Note Screen
 */
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
                text = "New Note",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
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
                        contentDescription = "Save Note"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * Main Content Area for Create Note Screen
 */
@Composable
fun CreateNoteContent(
    uiState: CreateNoteState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Title Section
        NoteTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            placeholder = "Note Title",
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Tags Section
        NoteTagsSection(
            tags = uiState.tags,
            onTagsChange = onTagsChange,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Content Section
        NoteTextField(
            value = uiState.content,
            onValueChange = onContentChange,
            placeholder = "Start writing your thoughts...",
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            singleLine = false,
            maxLines = Int.MAX_VALUE
        )

        // Character Count
        Text(
            text = "${uiState.content.length} characters",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}