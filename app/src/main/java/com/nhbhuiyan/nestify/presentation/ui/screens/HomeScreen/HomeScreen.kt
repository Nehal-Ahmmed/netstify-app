package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.Announcement
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Avatar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ProgressBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.StatRow
import com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamPlannerViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data.HomeFeedViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data.HomeViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.Category
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.RecentItem
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeState by viewModel.homeState.collectAsState()

    val examViewModel: ExamPlannerViewModel = hiltViewModel()
    val termReports by examViewModel.termReports.collectAsState()
    val subjects by examViewModel.subjects.collectAsState()
    val defaultLevel by examViewModel.defaultLevel.collectAsState()
    val defaultTerm by examViewModel.defaultTerm.collectAsState()

    val feedViewModel: HomeFeedViewModel = hiltViewModel()
    val announcements by feedViewModel.announcements.collectAsState()

    val c = NestifyTheme.colors

    // ── Derived academic standing (live data, no VM changes) ──────────────────
    val standing = remember(termReports, subjects, defaultLevel, defaultTerm) {
        computeStanding(termReports, subjects, defaultLevel, defaultTerm)
    }

    val dateLabel = remember {
        SimpleDateFormat("EEEE • MMM d", Locale.getDefault()).format(java.util.Date())
    }

    com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyScaffold(
        appBar = null,
    ) {
        Spacer(Modifier.height(Space.m))

        if (homeState.isLoading && subjects.isEmpty() && termReports.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().height(220.dp),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = c.brand) }
        } else {
            // ── Dark HERO stat card ───────────────────────────────────────────
            HeroCard(standing = standing, onClick = { navController.navigate(Route.ExamPlanner.route) })

            Spacer(Modifier.height(Space.xl))

            // ── Announcements carousel ────────────────────────────────────────
            SectionHead(
                title = "Announcements",
                kicker = "Latest",
                actionText = "Network",
                onAction = { navController.navigate(Route.Network.route) },
            )
            Spacer(Modifier.height(Space.m))
            if (announcements.isEmpty()) {
                NestifyCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                        IconTile(Icons.Outlined.Campaign)
                        Column(Modifier.weight(1f)) {
                            Text("No announcements yet", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                            Kicker("Your class broadcasts will appear here")
                        }
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    items(announcements) { ann -> AnnouncementCard(ann) }
                }
            }

            Spacer(Modifier.height(Space.xl))

            // ── Quick actions carousel ────────────────────────────────────────
            SectionHead(title = "Quick actions", kicker = "Jump in")
            Spacer(Modifier.height(Space.m))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                items(quickActions) { qa ->
                    QuickActionTile(qa) { navController.navigate(qa.route) }
                }
            }

            Spacer(Modifier.height(Space.xl))

            // ── Workspace grid (live counts) ──────────────────────────────────
            SectionHead(title = "Your workspace", kicker = "Library")
            Spacer(Modifier.height(Space.m))
            homeState.categories.chunked(2).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    rowItems.forEach { cat ->
                        WorkspaceCard(
                            category = cat,
                            modifier = Modifier.weight(1f),
                            onClick = { routeForCategory(cat.id)?.let(navController::navigate) },
                        )
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(Space.m))
            }

            // ── Recent activity ───────────────────────────────────────────────
            if (homeState.recentItems.isNotEmpty()) {
                Spacer(Modifier.height(Space.s))
                SectionHead(title = "Recent activity", kicker = "Updated")
                Spacer(Modifier.height(Space.m))
                NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.s) {
                    Column {
                        homeState.recentItems.forEachIndexed { i, item ->
                            RecentRow(item)
                            if (i != homeState.recentItems.lastIndex) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair2))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Hero ─────────────────────────────────────────────────────────────────────

@Composable
private fun HeroCard(standing: AcademicStanding, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier.fillMaxWidth(),
        background = c.surfaceDk,
        padding = Space.xl,
        onClick = onClick,
    ) {
        Box(Modifier.fillMaxWidth().background(NestifyGradients.darkHero())) {
            Column {
                Kicker("ACADEMIC STANDING", color = Color.White.copy(alpha = 0.6f))
                Spacer(Modifier.height(Space.s))
                Text(
                    if (standing.completedTerms > 0) String.format("%.2f", standing.cgpa) else "—",
                    style = NestifyTheme.type.displaySerif,
                    color = Color.White,
                )
                Text(
                    "Running CGPA · ${standing.completedTerms} of 8 terms",
                    style = NestifyTheme.type.body,
                    color = Color.White.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(Space.l))
                ProgressBar(
                    value = (standing.cgpa / 4f),
                    color = Color.White,
                    track = Color.White.copy(alpha = 0.18f),
                )
                Spacer(Modifier.height(Space.l))
                StatRow(
                    stats = listOf(
                        (if (standing.completedTerms > 0) String.format("%.2f", standing.cgpa) else "—") to "CGPA",
                        "${standing.currentSubjects}" to "Subjects",
                        standing.nextExamLabel to "Next exam",
                    ),
                    valueColor = Color.White,
                    labelColor = Color.White.copy(alpha = 0.6f),
                    dividerColor = Color.White.copy(alpha = 0.15f),
                )
            }
        }
    }
}

// ── Announcement card ──────────────────────────────────────────────────────────

@Composable
private fun AnnouncementCard(ann: Announcement) {
    val c = NestifyTheme.colors
    val tone = when (ann.priority.lowercase()) {
        "high" -> ChipTone.Coral
        "medium" -> ChipTone.Warn
        else -> ChipTone.Ok
    }
    NestifyCard(modifier = Modifier.width(264.dp)) {
        Column {
            Chip(ann.priority.replaceFirstChar { it.uppercase() }, tone = tone)
            Spacer(Modifier.height(Space.s))
            Text(
                ann.title,
                style = NestifyTheme.type.h3Serif,
                color = c.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                ann.body,
                style = NestifyTheme.type.body,
                color = c.ink50,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── Quick action tile ──────────────────────────────────────────────────────────

private data class QuickAction(val title: String, val icon: ImageVector, val route: String)

private val quickActions = listOf(
    QuickAction("Academics", Icons.Outlined.School, Route.ExamPlanner.route),
    QuickAction("Notes", Icons.Outlined.Description, Route.Notes.route),
    QuickAction("Schedules", Icons.Outlined.CalendarMonth, Route.Schedule.route),
    QuickAction("Network", Icons.Outlined.Hub, Route.Network.route),
    QuickAction("Library", Icons.Outlined.MenuBook, Route.Library.route),
)

@Composable
private fun QuickActionTile(qa: QuickAction, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.widthIn(min = 116.dp), onClick = onClick) {
        Column {
            IconTile(qa.icon)
            Spacer(Modifier.height(Space.m))
            OneLine(qa.title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
        }
    }
}

// ── Workspace card ──────────────────────────────────────────────────────────────

@Composable
private fun WorkspaceCard(category: Category, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
            IconTile(iconForCategory(category.id))
            Column(Modifier.weight(1f)) {
                OneLine(category.title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Kicker("${category.count} items")
            }
        }
    }
}

@Composable
private fun RecentRow(item: RecentItem) {
    val c = NestifyTheme.colors
    Row(
        Modifier.fillMaxWidth().padding(Space.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        IconTile(iconForType(item.type), size = 36.dp)
        Column(Modifier.weight(1f)) {
            OneLine(item.title.ifBlank { "Untitled" }, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
            OneLine(item.subtitle, style = NestifyTheme.type.meta, color = c.ink50)
        }
        Kicker(item.timestamp)
    }
}

// ── Mappers / derivation ────────────────────────────────────────────────────────

private fun iconForCategory(id: String): ImageVector = when (id) {
    "notes" -> Icons.Outlined.Description
    "links" -> Icons.Outlined.Link
    "files" -> Icons.Outlined.Folder
    "archive" -> Icons.Outlined.Inventory2
    "favorites" -> Icons.Outlined.Star
    else -> Icons.Outlined.Description
}

private fun routeForCategory(id: String): String? = when (id) {
    "notes" -> Route.Notes.route
    "links" -> Route.LinkCategories.route
    "files" -> Route.FolderScreen.route
    "archive" -> Route.Archive.route
    "favorites" -> Route.Favorites.route
    else -> null
}

private fun iconForType(type: String): ImageVector = when (type) {
    "note" -> Icons.Outlined.Description
    "link" -> Icons.Outlined.Link
    "file" -> Icons.Outlined.Folder
    else -> Icons.Outlined.Description
}

data class AcademicStanding(
    val cgpa: Float,
    val completedTerms: Int,
    val currentSubjects: Int,
    val nextExamLabel: String,
)

private fun computeStanding(
    termReports: List<TermReportEntity>,
    subjects: List<SubjectEntity>,
    defaultLevel: Int,
    defaultTerm: Int,
): AcademicStanding {
    val byTerm = termReports
        .groupBy { it.level to it.term }
        .mapValues { entry -> entry.value.maxByOrNull { it.timestamp }?.gpa ?: 0f }
    val completed = byTerm.size
    val cgpa = if (completed > 0) byTerm.values.sum() / completed else 0f

    val currentSubjects = subjects.count { it.level == defaultLevel && it.term == defaultTerm }

    val nextDays = subjects
        .mapNotNull { daysUntil(it.examDate) }
        .filter { it >= 0 }
        .minOrNull()
    val nextExamLabel = nextDays?.let { "${it}d" } ?: "—"

    return AcademicStanding(cgpa, completed, currentSubjects, nextExamLabel)
}

private fun daysUntil(examDateStr: String): Int? {
    if (examDateStr.isBlank()) return null
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val examDate = sdf.parse(examDateStr) ?: return null
        val diff = examDate.time - System.currentTimeMillis()
        (diff / (1000L * 60L * 60L * 24L)).toInt()
    } catch (e: Exception) {
        null
    }
}
