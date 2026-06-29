package com.nhbhuiyan.nestify.presentation.ui.screens.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarkItem
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarksState
import com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarksViewModel
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

/**
 * Bookmarks Screen — all bookmarked notes, links and files, re-skinned to BrainSton.
 */
@Composable
fun BookmarksScreen(
    navController: NavController,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarksState by viewModel.bookmarkState.collectAsState()
    val c = NestifyTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Bookmarks",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(Icons.Outlined.Search, onClick = { /* search */ }, contentDescription = "Search Bookmarks")
            },
        )
        BookmarksContent(
            bookmarksState = bookmarksState,
            onBookmarkClick = { bookmark ->
                when (bookmark.type) {
                    "note" -> navController.navigate("note/${bookmark.id}")
                    "link" -> navController.navigate("link/${bookmark.id}")
                    "file" -> navController.navigate("file/${bookmark.id}")
                }
            },
            onRemoveBookmark = { bookmark ->
                viewModel.removeBookmark(bookmark.id, bookmark.type)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun BookmarksContent(
    bookmarksState: BookmarksState,
    onBookmarkClick: (BookmarkItem) -> Unit,
    onRemoveBookmark: (BookmarkItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = NestifyTheme.colors
    val filters = listOf("All", "Notes", "Links", "Files")
    var selected by remember { mutableIntStateOf(0) }

    when {
        bookmarksState.isLoading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = c.brand)
            }
        }

        bookmarksState.bookmarks.isEmpty() -> {
            Column(modifier.fillMaxSize()) {
                Spacer(Modifier.height(Space.xxl))
                EmptyState(
                    icon = Icons.Outlined.Bookmark,
                    title = "No bookmarks yet",
                    description = "Bookmark notes, links and files to keep them within easy reach.",
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Space.screen,
                    end = Space.screen,
                    top = Space.m,
                    bottom = Space.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Space.m),
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Space.s),
                    ) {
                        filters.forEachIndexed { i, label ->
                            Chip(
                                label = label,
                                tone = ChipTone.Default,
                                active = i == selected,
                                onClick = { selected = i },
                            )
                        }
                    }
                }

                items(items = bookmarksState.bookmarks, key = { it.id }) { bookmark ->
                    BookmarkRow(
                        bookmark = bookmark,
                        onClick = { onBookmarkClick(bookmark) },
                        onRemove = { onRemoveBookmark(bookmark) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkRow(
    bookmark: BookmarkItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.m, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
            IconTile(iconForType(bookmark.type))
            Column(Modifier.weight(1f)) {
                OneLine(
                    bookmark.title.ifBlank { "Untitled" },
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    color = c.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    bookmark.subtitle,
                    style = NestifyTheme.type.body,
                    color = c.ink50,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (bookmark.timestamp.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Kicker(bookmark.timestamp)
                }
            }
            IconButtonChrome(
                Icons.Outlined.Close,
                onClick = onRemove,
                tint = c.coral,
                contentDescription = "Remove bookmark",
            )
        }
    }
}

private fun iconForType(type: String): ImageVector = when (type) {
    "note" -> Icons.Outlined.Description
    "link" -> Icons.Outlined.Link
    "file" -> Icons.Outlined.Folder
    else -> Icons.Outlined.Bookmark
}
