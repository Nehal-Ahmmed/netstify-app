package com.nhbhuiyan.nestify.presentation.ui.screens.Management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material3.Text
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
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

private data class ManagementEntry(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val route: String,
)

@Composable
fun ManagementHubScreen(navController: NavController) {
    val entries = listOf(
        ManagementEntry(
            "Merge Requests",
            "Approve or reject student submissions for PYQs & topics",
            Icons.Default.MergeType,
            Route.MergeRequests.route,
        ),
        ManagementEntry(
            "Role Management",
            "Promote classmates to Class Representatives (CR)",
            Icons.Default.Group,
            Route.RoleManagement.route,
        ),
        ManagementEntry(
            "Post Announcements",
            "Broadcast high-priority notifications to the class",
            Icons.Default.Announcement,
            Route.Announcements.route,
        ),
    )

    NestifyScaffold(
        appBar = { NestifyAppBar(title = "Management Hub", onBack = { navController.popBackStack() }) },
    ) {
        Spacer(Modifier.height(Space.l))
        SectionHead(title = "Representative tools", kicker = "Class management")
        Spacer(Modifier.height(Space.l))

        entries.chunked(2).forEach { rowItems ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.m),
            ) {
                rowItems.forEach { entry ->
                    ManagementTile(
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
private fun ManagementTile(
    entry: ManagementEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = modifier, padding = Space.l, onClick = onClick) {
        Column {
            IconTile(icon = entry.icon)
            Spacer(Modifier.height(Space.m))
            Text(entry.title, style = NestifyTheme.type.h3Serif, color = c.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Kicker(entry.desc)
        }
    }
}
