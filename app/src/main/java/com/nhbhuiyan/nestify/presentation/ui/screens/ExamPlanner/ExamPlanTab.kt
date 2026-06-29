package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NumberTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ProgressBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun ExamPlanTab(
    navController: NavController,
    viewModel: ExamPlannerViewModel,
    defaultLevel: Int,
    defaultTerm: Int,
) {
    val c = NestifyTheme.colors
    val subjectsList by viewModel.subjects.collectAsState()
    val syllabusTopicsMap by viewModel.syllabusTopics.collectAsState()

    val filteredSubjects = remember(subjectsList, defaultLevel, defaultTerm) {
        subjectsList.filter { it.level == defaultLevel && it.term == defaultTerm }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        item {
            SectionHead(title = "Exam schedule", kicker = "Preparation")
            Spacer(Modifier.height(4.dp))
            Text(
                "Tap a subject to manage Section A/B syllabus checkpoints and revisions.",
                style = NestifyTheme.type.body,
                color = c.ink50,
            )
        }

        if (filteredSubjects.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.EventNote,
                    title = "No subjects to plan",
                    description = "Add courses to this term to build your exam preparation plan.",
                )
            }
        } else {
            itemsIndexed(filteredSubjects) { index, subject ->
                val topics = syllabusTopicsMap[subject.id] ?: emptyList()
                val total = topics.size
                val progress = if (total > 0) topics.count { it.isCompleted }.toFloat() / total else 0f
                val days = daysRemainingOrNull(subject.examDate)

                NestifyCard(
                    Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(Route.ExamDetail.createRoute(subject.name)) },
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                            NumberTile(index + 1)
                            Column(Modifier.weight(1f)) {
                                OneLine(subject.code, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                                OneLine(subject.name, style = NestifyTheme.type.meta, color = c.ink50)
                            }
                            val (tone, label) = when {
                                days == null -> ChipTone.Default to "No date"
                                days <= 0 -> ChipTone.Coral to "Today"
                                days <= 2 -> ChipTone.Coral to "${days}d left"
                                days <= 7 -> ChipTone.Warn to "${days}d left"
                                else -> ChipTone.Ok to "${days}d left"
                            }
                            Chip(label, tone = tone)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = c.ink30)
                        }
                        Spacer(Modifier.height(Space.m))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                            ProgressBar(value = progress, modifier = Modifier.weight(1f))
                            Text("${(progress * 100).toInt()}%", style = NestifyTheme.type.meta, color = c.ink70)
                        }
                        if (subject.examDate.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Kicker(subject.examDate)
                        }
                    }
                }
            }
        }
    }
}

private fun daysRemainingOrNull(examDateStr: String): Int? {
    if (examDateStr.isBlank()) return null
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val examDate = sdf.parse(examDateStr) ?: return null
        val diffMs = examDate.time - System.currentTimeMillis()
        (diffMs / (1000L * 60L * 60L * 24L)).toInt()
    } catch (e: Exception) {
        null
    }
}
