package com.nhbhuiyan.nestify.presentation.ui.screens.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.components.BookmarkItem
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.components.EmptyBookmarksState
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarkItem
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarksState
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarksViewModel

/**
 * Bookmarks Screen - Shows all bookmarked notes, links, and files
 * Follows the design from the provided image
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    navController: NavController,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarksState by viewModel.bookmarkState.collectAsState()

    Scaffold(
        topBar = {
            BookmarksTopBar(
                onSearchClick = { /* Handle search */ }
            )
        },
        containerColor = Color(0xFFF8FAFD)
    ) { paddingValues ->
        BookmarksContent(
            bookmarksState = bookmarksState,
            onBookmarkClick = { bookmark ->
                // Navigate to the item based on type
                when (bookmark.type) {
                    "note" -> navController.navigate("note/${bookmark.id}")
                    "link" -> navController.navigate("link/${bookmark.id}")
                    "file" -> navController.navigate("file/${bookmark.id}")
                }
            },
            onRemoveBookmark = { bookmark ->
                viewModel.removeBookmark(bookmark.id, bookmark.type)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Top App Bar for Bookmarks Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksTopBar(
    onSearchClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Hello",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Bookmarks",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Bookmarks"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * Main Content Area for Bookmarks Screen
 */
@Composable
fun BookmarksContent(
    bookmarksState: BookmarksState,
    onBookmarkClick: (BookmarkItem) -> Unit,
    onRemoveBookmark: (BookmarkItem) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        bookmarksState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        bookmarksState.bookmarks.isEmpty() -> {
            EmptyBookmarksState(
                modifier = modifier.fillMaxSize()
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFD)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = bookmarksState.bookmarks,
                    key = { it.id }
                ) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        onClick = { onBookmarkClick(bookmark) },
                        onRemove = { onRemoveBookmark(bookmark) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}