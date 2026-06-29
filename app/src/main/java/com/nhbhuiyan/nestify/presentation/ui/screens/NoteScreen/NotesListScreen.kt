package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.DynamicUserFrameNotebook
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SearchBarPill
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data.NotesViewModel
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(navController: NavController) {
    val viewModel: NotesViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()
    val c = NestifyTheme.colors

    // State for modern features
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) } // Default to GRID for sticky notes
    var selectedNotes by remember { mutableStateOf(emptySet<Long>()) }
    val isSelectionMode = selectedNotes.isNotEmpty()

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val backgroundBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.a2y6_21s0_220127)
    }

    // Filter notes based on search
    val filteredNotes = state.value.notes.filter { note ->
        searchQuery.isEmpty() || note.content.contains(searchQuery, ignoreCase = true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas),
    ) {
        NestifyAppBar(
            title = if (isSelectionMode) "${selectedNotes.size} selected" else "Daily Notes",
            onBack = if (isSelectionMode) {
                { selectedNotes = emptySet() }
            } else {
                { navController.popBackStack() }
            },
            trailing = {
                if (isSelectionMode) {
                    IconButtonChrome(
                        Icons.Default.SelectAll,
                        onClick = {
                            selectedNotes = if (selectedNotes.size == filteredNotes.size) {
                                emptySet()
                            } else {
                                filteredNotes.map { it.id }.toSet()
                            }
                        },
                        contentDescription = "Select all",
                    )
                    IconButtonChrome(
                        Icons.Default.Delete,
                        onClick = {
                            selectedNotes.forEach { id -> viewModel.deleteNote(id) }
                            selectedNotes = emptySet()
                        },
                        tint = c.coral,
                        contentDescription = "Delete selected",
                    )
                } else {
                    if (state.value.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = c.brand,
                        )
                        Spacer(Modifier.width(Space.s))
                    }
                    IconButtonChrome(
                        if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                searchQuery = ""
                                keyboardController?.hide()
                            } else {
                                isSearchActive = true
                            }
                        },
                        contentDescription = "Search",
                    )
                    IconButtonChrome(
                        Icons.Default.Dashboard,
                        onClick = {
                            viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                        },
                        contentDescription = "Change view mode",
                    )
                    IconTile(
                        Icons.Default.Add,
                        modifier = Modifier
                            .clip(Radii.s)
                            .clickable { navController.navigate(Route.createNote.route) },
                    )
                }
            },
        )

        if (isSearchActive) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(c.surface)
                    .padding(horizontal = Space.screen, vertical = Space.m),
            ) {
                SearchBarPill(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search notes…",
                )
            }
        }

        when {
            state.value.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = c.brand)
                }
            }

            filteredNotes.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (searchQuery.isNotEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Search,
                            title = "No notes found",
                            description = "Try different search terms or clear the search.",
                            primaryLabel = "Clear search",
                            onPrimary = { searchQuery = "" },
                        )
                    } else {
                        EmptyState(
                            icon = Icons.Default.Edit,
                            title = "Your canvas is empty",
                            description = "Create your first sticky note by tapping the + button above.",
                            primaryLabel = "New note",
                            onPrimary = { navController.navigate(Route.createNote.route) },
                        )
                    }
                }
            }

            else -> {
                val listPadding = PaddingValues(
                    start = Space.screen,
                    end = Space.screen,
                    top = Space.l,
                    bottom = GlassNavSpace,
                )
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
                        contentPadding = listPadding,
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
                        contentPadding = listPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun NotesListView(
    notes: List<Note>,
    navController: NavController,
    selectedNotes: Set<Long>,
    backgroundBitmap: Bitmap,
    onNoteSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Space.screen),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
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
            Spacer(modifier = Modifier.height(Space.l))
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
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Space.screen),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(notes.chunked(2)) { rowNotes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.l)
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
            Spacer(modifier = Modifier.height(Space.l))
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
    val c = NestifyTheme.colors
    val containerColor = if (isSelected) c.brandSoft else androidx.compose.ui.graphics.Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.m)
            .background(containerColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
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
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(Space.s),
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = c.brand,
                    modifier = Modifier.size(24.dp)
                )
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
