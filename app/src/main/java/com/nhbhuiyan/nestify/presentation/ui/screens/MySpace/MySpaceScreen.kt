package com.nhbhuiyan.nestify.presentation.ui.screens.MySpace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyScaffold
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import androidx.compose.material3.Text
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

private data class SpaceEntry(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val route: String,
)

/**
 * My Space — the personal-productivity hub (BrainSton "quick actions" grid archetype).
 * Phase B: a working 2-column launcher into existing CRUD screens. Phase E rebuilds the
 * detail screens themselves and folds bookmarks/archive in as filters.
 */
@Composable
fun MySpaceScreen(navController: NavController) {
    val entries = listOf(
        SpaceEntry("Notes", "Capture & organise", Icons.Outlined.Description, Route.Notes.route),
        SpaceEntry("Links", "Saved resources", Icons.Outlined.Link, Route.LinkCategories.route),
        SpaceEntry("Files", "Documents & PDFs", Icons.Outlined.Folder, Route.FolderScreen.route),
        SpaceEntry("Schedules", "Plan your week", Icons.Outlined.CalendarMonth, Route.Schedule.route),
        SpaceEntry("Projects", "Build & track", Icons.Outlined.Workspaces, Route.MyProjects.route),
        SpaceEntry("Library", "Your collection", Icons.Outlined.MenuBook, Route.Library.route),
    )

    NestifyScaffold(
        appBar = null,
    ) {
        Spacer(Modifier.height(Space.l))
        SectionHead(title = "Your toolkit", kicker = "Personal")
        Spacer(Modifier.height(Space.l))

        entries.chunked(2).forEach { rowItems ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.m),
            ) {
                rowItems.forEach { entry ->
                    SpaceTile(
                        entry = entry,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(entry.route) },
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(Space.m))
        }
    }
}

@Composable
private fun SpaceTile(
    entry: SpaceEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = modifier, padding = Space.l, onClick = onClick) {
        Column {
            IconTile(icon = entry.icon)
            Spacer(Modifier.height(Space.m))
            Text(entry.title, style = NestifyTheme.type.h3Serif, color = c.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Kicker(entry.desc)
        }
    }
}
