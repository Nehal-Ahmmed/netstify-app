package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.DynamicUserFrameNotebook
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.NotesViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(navController: NavController) {
    val viewModel: NotesViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()
    
    // State for modern features
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) } // Default to GRID for sticky notes
    var selectedNotes by remember { mutableStateOf(emptySet<Long>()) }
    val isSelectionMode = selectedNotes.isNotEmpty()

    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    
    val backgroundBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.a2y6_21s0_220127)
    }

    // Filter notes based on search
    val filteredNotes = state.value.notes.filter { note ->
        searchQuery.isEmpty() || note.content.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchActiveChange = { isSearchActive = it },
                    focusRequester = searchFocusRequester,
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                        keyboardController?.hide()
                    }
                )
            } else {
                MainTopBar(
                    title = "Daily Notes",
                    isLoading = state.value.isLoading,
                    isSelectionMode = isSelectionMode,
                    selectedCount = selectedNotes.size,
                    onBackClick = { navController.popBackStack() },
                    onSearchClick = {
                        isSearchActive = true
                    },
                    onViewModeChange = {
                        viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                    },
                    onSelectAll = {
                        selectedNotes = if (selectedNotes.size == filteredNotes.size) {
                            emptySet()
                        } else {
                            filteredNotes.map { it.id }.toSet()
                        }
                    },
                    onDeleteSelected = {
                        selectedNotes.forEach { id ->
                            viewModel.deleteNote(id)
                        }
                        selectedNotes = emptySet()
                    },
                    onClearSelection = { selectedNotes = emptySet() }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Route.createNote.route)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        }
    ) { padding ->
        if (state.value.isLoading) {
            LoadingShimmer()
        } else {
            when (viewMode) {
                ViewMode.LIST -> NotesListView(
                    notes = filteredNotes,
                    navController = navController,
                    selectedNotes = selectedNotes,
                    backgroundBitmap = backgroundBitmap,
                    onNoteSelected = { id ->
                        selectedNotes = if (selectedNotes.contains(id)) {
                            selectedNotes - id
                        } else {
                            selectedNotes + id
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
                ViewMode.GRID -> NotesGridView(
                    notes = filteredNotes,
                    navController = navController,
                    selectedNotes = selectedNotes,
                    backgroundBitmap = backgroundBitmap,
                    onNoteSelected = { id ->
                        selectedNotes = if (selectedNotes.contains(id)) {
                            selectedNotes - id
                        } else {
                            selectedNotes + id
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // Empty state
        if (filteredNotes.isEmpty() && !state.value.isLoading) {
            EmptyNotesState(
                hasSearchQuery = searchQuery.isNotEmpty(),
                onClearSearch = { searchQuery = "" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    isLoading: Boolean,
    isSelectionMode: Boolean,
    selectedCount: Int,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onViewModeChange: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelection: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (isSelectionMode) {
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                        fontSize = 32.sp
                    )
                )
            }
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Close, contentDescription = "Clear selection")
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (isSelectionMode) {
                IconButton(onClick = onSelectAll) {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select all")
                }
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                }
            } else {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = onViewModeChange) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Change view mode"
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    onClose: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    CenterAlignedTopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text("Search notes...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        }
    )
}

@Composable
fun NotesListView(
    notes: List<Note>,
    navController: NavController,
    selectedNotes: Set<Long>,
    backgroundBitmap: Bitmap,
    onNoteSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            StickyNoteItem(
                note = note,
                bitmap = backgroundBitmap,
                isSelected = selectedNotes.contains(note.id),
                onClick = {
                    if (selectedNotes.isNotEmpty()) {
                        onNoteSelected(note.id)
                    } else {
                        navController.navigate(Route.NoteDetail.createRoute(note.id))
                    }
                },
                onLongClick = { onNoteSelected(note.id) },
                modifier = Modifier.height(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NotesGridView(
    notes: List<Note>,
    navController: NavController,
    selectedNotes: Set<Long>,
    backgroundBitmap: Bitmap,
    onNoteSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(notes.chunked(2)) { rowNotes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowNotes.forEach { note ->
                    StickyNoteItem(
                        note = note,
                        bitmap = backgroundBitmap,
                        isSelected = selectedNotes.contains(note.id),
                        onClick = {
                            if (selectedNotes.isNotEmpty()) {
                                onNoteSelected(note.id)
                            } else {
                                navController.navigate(Route.NoteDetail.createRoute(note.id))
                            }
                        },
                        onLongClick = { onNoteSelected(note.id) },
                        modifier = Modifier.weight(1f).aspectRatio(1f)
                    )
                }
                // Add empty space if row has only one item
                if (rowNotes.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickyNoteItem(
    note: Note,
    bitmap: Bitmap,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .background(containerColor, RoundedCornerShape(12.dp))
            .padding(4.dp) // Slight padding so the selection color shows nicely behind
    ) {
        DynamicUserFrameNotebook(
            frameBitmap = bitmap,
            text = note.content,
            onTextChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxSize()
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun EmptyNotesState(hasSearchQuery: Boolean, onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "No notes",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasSearchQuery) "No notes found" else "Your Canvas is Empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery) {
                "Try different search terms or clear search"
            } else {
                "Create your first sticky note by tapping the + button below."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        if (hasSearchQuery) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClearSearch) {
                Text("Clear Search")
            }
        }
    }
}

fun getRelativeTimeString(instant: Instant): String {
    return try {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = localDateTime.month.name.take(3)
        val day = localDateTime.dayOfMonth
        val year = localDateTime.year

        "$month $day, $year"
    } catch (e: Exception) {
        "recently"
    }
}

enum class ViewMode {
    LIST, GRID
}