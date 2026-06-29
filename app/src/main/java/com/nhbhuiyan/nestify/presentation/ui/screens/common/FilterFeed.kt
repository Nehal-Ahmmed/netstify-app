package com.nhbhuiyan.nestify.presentation.ui.screens.common

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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

/**
 * Shared list item + rendering for the content-filter screens (Archive, Favorites, Search).
 * Each item is a note/link/file surfaced through the existing content use cases; tapping a row
 * opens the matching detail screen.
 */
data class FeedListItem(
    val id: String,
    val type: String, // "note" | "link" | "file"
    val title: String,
    val subtitle: String,
    val timestamp: String,
)

/** Maps a feed item type to its BrainSton icon. */
fun iconForFeedType(type: String): ImageVector = when (type) {
    "note" -> Icons.Outlined.Description
    "link" -> Icons.Outlined.Link
    "file" -> Icons.Outlined.Folder
    else -> Icons.Outlined.InsertDriveFile
}

/**
 * Loading / empty / list rendering with an "All / Notes / Links / Files" client-side type filter.
 * Used by Archive and Favorites.
 */
@Composable
fun FilterFeedContent(
    items: List<FeedListItem>,
    isLoading: Boolean,
    emptyIcon: ImageVector,
    emptyTitle: String,
    emptyDescription: String,
    onItemClick: (FeedListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    val filters = listOf("All", "Notes", "Links", "Files")
    var selected by remember { mutableIntStateOf(0) }
    val typeFor = mapOf(1 to "note", 2 to "link", 3 to "file")
    val visible = remember(items, selected) {
        if (selected == 0) items else items.filter { it.type == typeFor[selected] }
    }

    when {
        isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = c.brand)
        }

        items.isEmpty() -> Column(modifier.fillMaxSize()) {
            Spacer(Modifier.height(Space.xxl))
            EmptyState(icon = emptyIcon, title = emptyTitle, description = emptyDescription)
        }

        else -> LazyColumn(
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
                        Chip(label = label, tone = ChipTone.Default, active = i == selected, onClick = { selected = i })
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        "Nothing here in this filter.",
                        style = NestifyTheme.type.body,
                        color = c.ink50,
                        modifier = Modifier.padding(top = Space.l),
                    )
                }
            }
            items(items = visible, key = { it.type + it.id }) { item ->
                FeedRow(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

/** A single content row (note/link/file) in the BrainSton card style. */
@Composable
fun FeedRow(item: FeedListItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = modifier.fillMaxWidth(), padding = Space.m, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
            IconTile(iconForFeedType(item.type))
            Column(Modifier.weight(1f)) {
                OneLine(
                    item.title.ifBlank { "Untitled" },
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    color = c.ink,
                )
                if (item.subtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(item.subtitle, style = NestifyTheme.type.body, color = c.ink50, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                if (item.timestamp.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Kicker(item.timestamp)
                }
            }
        }
    }
}

/** Relative "x ago" timestamp shared across the filter screens. */
fun relativeFeedTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis
    if (diff < 0) return "now"
    val minutes = diff / 60_000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        days < 30 -> "${days / 7}w ago"
        else -> "${days / 30}mo ago"
    }
}
