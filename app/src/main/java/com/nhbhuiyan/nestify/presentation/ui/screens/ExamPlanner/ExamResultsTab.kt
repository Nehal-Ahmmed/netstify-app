package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.StatRow
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

data class CourseGrade(
    val subjectCode: String,
    val subjectName: String,
    val credits: Float,
    var selectedGrade: String = "Pending",
)

@Composable
fun ExamResultsTab(
    viewModel: ExamPlannerViewModel,
    defaultLevel: Int,
    defaultTerm: Int,
) {
    val c = NestifyTheme.colors
    val context = LocalContext.current
    val grades = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F", "Pending")

    val subjectsList by viewModel.subjects.collectAsState()

    var filterLevel by remember(defaultLevel) { mutableIntStateOf(defaultLevel) }
    var filterTerm by remember(defaultTerm) { mutableIntStateOf(defaultTerm) }
    val isEditable = filterLevel == defaultLevel && filterTerm == defaultTerm

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    val gradedCourses = filteredSubjects.filter { it.finalGrade != "Pending" }
    val totalCredits = gradedCourses.sumOf { it.credits.toDouble() }.toFloat()
    val weightedGpSum = gradedCourses.sumOf { (it.credits * AcademicGradingEngine.gradeToGp(it.finalGrade)).toDouble() }.toFloat()
    val calculatedGpa = if (totalCredits > 0) weightedGpSum / totalCredits else 0f

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        item {
            LevelTermFilter(
                level = filterLevel, term = filterTerm,
                onLevel = { filterLevel = it }, onTerm = { filterTerm = it },
            )
        }

        // Dark progress hero
        item {
            NestifyCard(Modifier.fillMaxWidth(), background = c.surfaceDk, padding = Space.xl) {
                Box(Modifier.fillMaxWidth().background(NestifyGradients.darkHero())) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Kicker("TERM RESULT · L$filterLevel T$filterTerm", color = Color.White.copy(alpha = 0.6f))
                            Spacer(Modifier.height(Space.s))
                            Text(String.format("%.2f", calculatedGpa), style = NestifyTheme.type.displaySerif, color = Color.White)
                            Spacer(Modifier.height(Space.m))
                            StatRow(
                                stats = listOf(
                                    "${gradedCourses.size}/${filteredSubjects.size}" to "Graded",
                                    (if (totalCredits % 1f == 0f) totalCredits.toInt().toString() else String.format("%.1f", totalCredits)) to "Credits",
                                    String.format("%.2f", calculatedGpa) to "GPA",
                                ),
                                valueColor = Color.White,
                                labelColor = Color.White.copy(alpha = 0.6f),
                                dividerColor = Color.White.copy(alpha = 0.15f),
                            )
                        }
                        Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.height(56.dp))
                    }
                }
            }
        }

        item { SectionHead(title = "Acquired grades", kicker = "Per subject") }

        if (filteredSubjects.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.WorkspacePremium,
                    title = "No subjects",
                    description = "Add courses to this term to record final grades.",
                )
            }
        } else {
            items(filteredSubjects) { course ->
                var expanded by remember { mutableStateOf(false) }
                NestifyCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            OneLine(course.code, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                            OneLine(course.name, style = NestifyTheme.type.meta, color = c.ink50)
                            Kicker("${course.credits} credits")
                        }
                        Box {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Chip(
                                    course.finalGrade,
                                    tone = gradeTone(course.finalGrade),
                                    onClick = { if (isEditable) expanded = true },
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = c.ink50)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                grades.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g, fontWeight = FontWeight.SemiBold) },
                                        onClick = {
                                            viewModel.updateSubject(course.copy(finalGrade = g))
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Actions & export
        item {
            Spacer(Modifier.height(Space.s))
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Actions & export", style = NestifyTheme.type.h3Serif, color = c.ink)
                    NButton("Save to CGPA dashboard", {
                        viewModel.insertTermReport(
                            TermReportEntity(
                                level = filterLevel,
                                term = filterTerm,
                                gpa = calculatedGpa,
                                pdfLocalPath = "cache/REP_L${filterLevel}T${filterTerm}.pdf",
                                timestamp = System.currentTimeMillis(),
                            )
                        )
                        Toast.makeText(context, "Saved L${filterLevel}T${filterTerm} report to dashboard!", Toast.LENGTH_SHORT).show()
                    }, full = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                        NButton("Share PDF", {
                            val file = AcademicPdfGenerator.generateTermPdf(
                                context = context, level = filterLevel, term = filterTerm, gpa = calculatedGpa,
                                courses = filteredSubjects.map { CourseGrade(it.code, it.name, it.credits, it.finalGrade) },
                            )
                            if (file != null) AcademicPdfGenerator.sharePdf(context, file)
                            else Toast.makeText(context, "Failed to generate report PDF", Toast.LENGTH_SHORT).show()
                        }, modifier = Modifier.weight(1f), variant = BtnVariant.Secondary)
                        NButton("Export PDF", {
                            val file = AcademicPdfGenerator.generateTermPdf(
                                context = context, level = filterLevel, term = filterTerm, gpa = calculatedGpa,
                                courses = filteredSubjects.map { CourseGrade(it.code, it.name, it.credits, it.finalGrade) },
                            )
                            if (file != null) Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
                            else Toast.makeText(context, "Failed to generate report PDF", Toast.LENGTH_SHORT).show()
                        }, modifier = Modifier.weight(1f), variant = BtnVariant.Secondary)
                    }
                }
            }
        }
    }
}
