package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.RoleGate
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyScaffold
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NumberTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ProgressBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.TabPill
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun ExamDetailScreen(
    navController: NavController,
    subjectName: String,
    viewModel: ExamPlannerViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors
    val session by viewModel.sessionFlow.collectAsState(initial = null)
    val currentRole = session?.role ?: UserRole.STUDENT

    val subjects by viewModel.subjects.collectAsState()
    val subject = remember(subjects, subjectName) { subjects.firstOrNull { it.name == subjectName } }

    if (subject == null) {
        NestifyScaffold(
            appBar = { NestifyAppBar(title = subjectName, onBack = { navController.popBackStack() }) },
            scrollable = false,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = c.brand)
            }
        }
        return
    }

    var selectedSection by remember { mutableStateOf(0) } // 0 = A, 1 = B
    val sectionKey = if (selectedSection == 0) "A" else "B"
    var newTopicTitle by remember { mutableStateOf("") }
    var newTopicPriority by remember { mutableIntStateOf(3) }

    val allTopicsMap by viewModel.syllabusTopics.collectAsState()
    val syllabusTopics = remember(allTopicsMap, subject.id) { allTopicsMap[subject.id] ?: emptyList() }
    val sectionTopics = syllabusTopics.filter { it.section == sectionKey }
    val completedCount = syllabusTopics.count { it.isCompleted }
    val revisedCount = syllabusTopics.count { it.isRevised }
    val totalCount = syllabusTopics.size
    val prepProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val revisionProgress = if (totalCount > 0) revisedCount.toFloat() / totalCount else 0f

    NestifyScaffold(
        appBar = { NestifyAppBar(title = subject.name, subtitle = subject.code, onBack = { navController.popBackStack() }) },
    ) {
        Spacer(Modifier.height(Space.l))

        // Hero header with progress
        NestifyCard(Modifier.fillMaxWidth(), background = c.surfaceDk, padding = Space.xl) {
            Box(Modifier.fillMaxWidth().background(NestifyGradients.darkHero())) {
                Column {
                    Kicker("SYLLABUS PREPARATION", color = Color.White.copy(alpha = 0.6f))
                    Spacer(Modifier.height(Space.s))
                    Text("${(prepProgress * 100).toInt()}%", style = NestifyTheme.type.displaySerif, color = Color.White)
                    Text("$completedCount of $totalCount topics complete", style = NestifyTheme.type.body, color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(Space.l))
                    ProgressBar(value = prepProgress, color = Color.White, track = Color.White.copy(alpha = 0.18f))
                    Spacer(Modifier.height(Space.m))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.s)) {
                        Text("Revision", style = NestifyTheme.type.meta, color = Color.White.copy(alpha = 0.6f))
                        ProgressBar(value = revisionProgress, modifier = Modifier.weight(1f), color = c.brand, track = Color.White.copy(alpha = 0.18f))
                        Text("$revisedCount/$totalCount", style = NestifyTheme.type.meta, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        Spacer(Modifier.height(Space.l))

        // Add topic form (CR only)
        RoleGate(currentRole = currentRole, requiredRole = UserRole.CR) {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Add syllabus topic", style = NestifyTheme.type.h3Serif, color = c.ink)
                    NestifyInput(newTopicTitle, { newTopicTitle = it }, placeholder = "Topic name or chapter…")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        StarRow(newTopicPriority) { newTopicPriority = it }
                        NButton("Add", {
                            if (newTopicTitle.isNotBlank()) {
                                viewModel.insertSyllabusTopic(
                                    SyllabusTopicEntity(subjectId = subject.id, section = sectionKey, title = newTopicTitle, priority = newTopicPriority)
                                )
                                newTopicTitle = ""; newTopicPriority = 3
                            }
                        })
                    }
                }
            }
            Spacer(Modifier.height(Space.l))
        }

        // Section switcher
        TabPill(tabs = listOf("Section A", "Section B"), active = selectedSection, onChange = { selectedSection = it })
        Spacer(Modifier.height(Space.l))

        SectionHead(title = "Topics", kicker = "Section $sectionKey")
        Spacer(Modifier.height(Space.m))

        if (sectionTopics.isEmpty()) {
            NestifyCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = c.ink30)
                    Text("No topics listed for Section $sectionKey yet.", style = NestifyTheme.type.body, color = c.ink50)
                }
            }
        } else {
            sectionTopics.forEachIndexed { index, topic ->
                NestifyCard(
                    Modifier.fillMaxWidth().padding(bottom = Space.m),
                    onClick = { navController.navigate(Route.ReadingRoom.createRoute(topic.id)) },
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                            NumberTile(index + 1)
                            Column(Modifier.weight(1f)) {
                                OneLine(topic.title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                                StarRow(topic.priority) { viewModel.updateSyllabusTopic(topic.copy(priority = it)) }
                            }
                            RoleGate(currentRole = currentRole, requiredRole = UserRole.CR) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = c.coral,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { viewModel.deleteSyllabusTopic(topic) },
                                )
                            }
                        }
                        Spacer(Modifier.height(Space.m))
                        Row(horizontalArrangement = Arrangement.spacedBy(Space.s)) {
                            Chip(
                                "Study done",
                                tone = if (topic.isCompleted) ChipTone.Ok else ChipTone.Default,
                                active = topic.isCompleted,
                                onClick = { viewModel.updateSyllabusTopic(topic.copy(isCompleted = !topic.isCompleted)) },
                            )
                            Chip(
                                "Revised",
                                tone = if (topic.isRevised) ChipTone.Brand else ChipTone.Default,
                                active = topic.isRevised,
                                onClick = { viewModel.updateSyllabusTopic(topic.copy(isRevised = !topic.isRevised)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StarRow(rating: Int, onRate: (Int) -> Unit) {
    val c = NestifyTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        (1..5).forEach { star ->
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = if (star <= rating) c.warn else c.ink10,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onRate(star) },
            )
        }
    }
}
