package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.StatRow
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

data class AcademicReport(
    val id: String,
    val universityName: String,
    val studentName: String,
    val studentId: String,
    val batch: String,
    val term: String,
    val academicYear: String,
    val courses: List<ReportCourse>,
    val totalGpa: Float,
)

data class ReportCourse(
    val code: String,
    val name: String,
    val credit: Float,
    val grade: String,
    val gp: Float,
)

data class TermGpaData(
    val name: String,
    var gpa: Float,
    var isCompleted: Boolean,
)

@Composable
fun CGPADashboardTab(viewModel: ExamPlannerViewModel) {
    val c = NestifyTheme.colors
    val context = LocalContext.current
    var targetCgpaInput by remember { mutableStateOf("3.80") }
    var selectedReport by remember { mutableStateOf<AcademicReport?>(null) }

    val dbReports by viewModel.termReports.collectAsState()

    val termsState = remember(dbReports) {
        val list = mutableListOf(
            TermGpaData("L1 T1", 3.61f, false),
            TermGpaData("L1 T2", 3.71f, false),
            TermGpaData("L2 T1", 3.87f, false),
            TermGpaData("L2 T2", 0.00f, false),
            TermGpaData("L3 T1", 0.00f, false),
            TermGpaData("L3 T2", 0.00f, false),
            TermGpaData("L4 T1", 0.00f, false),
            TermGpaData("L4 T2", 0.00f, false),
        )
        dbReports.forEach { report ->
            val index = when {
                report.level == 1 && report.term == 1 -> 0
                report.level == 1 && report.term == 2 -> 1
                report.level == 2 && report.term == 1 -> 2
                report.level == 2 && report.term == 2 -> 3
                report.level == 3 && report.term == 1 -> 4
                report.level == 3 && report.term == 2 -> 5
                report.level == 4 && report.term == 1 -> 6
                report.level == 4 && report.term == 2 -> 7
                else -> -1
            }
            if (index in 0..7) list[index] = TermGpaData(list[index].name, report.gpa, true)
        }
        mutableStateListOf(*list.toTypedArray())
    }

    val mockReports = remember(dbReports) {
        dbReports.map { report ->
            AcademicReport(
                id = report.id.toString(),
                universityName = "State University of Technology",
                studentName = "Nehal Ahmed",
                studentId = "2021-CSE-087",
                batch = "Batch 48",
                term = "Level ${report.level} Term ${report.term}",
                academicYear = "2024-2025",
                courses = listOf(
                    ReportCourse("CSE-111", "Introduction to Programming", 3.0f, "A+", 4.00f),
                    ReportCourse("MATH-113", "Differential Calculus", 3.0f, "A-", 3.50f),
                    ReportCourse("PHY-115", "Physics & Electromagnetics", 3.0f, "B+", 3.25f),
                    ReportCourse("CSE-112", "Programming Lab", 1.5f, "A+", 4.00f),
                ),
                totalGpa = report.gpa,
            )
        }
    }

    val completedTerms = termsState.filter { it.isCompleted }
    val runningAvg = if (completedTerms.isNotEmpty()) completedTerms.map { it.gpa }.sum() / completedTerms.size else 0f
    val targetCgpa = targetCgpaInput.toFloatOrNull() ?: 3.80f
    val remainingTerms = 8 - completedTerms.size
    val maxPossibleCgpa = if (remainingTerms > 0) (completedTerms.map { it.gpa }.sum() + (4.00f * remainingTerms)) / 8f else runningAvg
    val requiredFutureGpa = if (remainingTerms > 0) ((targetCgpa * 8) - completedTerms.map { it.gpa }.sum()) / remainingTerms else 0f

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        // Dark CGPA hero
        item {
            NestifyCard(Modifier.fillMaxWidth(), background = c.surfaceDk, padding = Space.xl) {
                Box(Modifier.fillMaxWidth().background(NestifyGradients.darkHero())) {
                    Column {
                        Kicker("RUNNING CGPA", color = Color.White.copy(alpha = 0.6f))
                        Spacer(Modifier.height(Space.s))
                        Text(String.format("%.2f", runningAvg), style = NestifyTheme.type.displaySerif, color = Color.White)
                        Spacer(Modifier.height(Space.m))
                        StatRow(
                            stats = listOf(
                                "${completedTerms.size}/8" to "Completed",
                                String.format("%.2f", targetCgpa) to "Target",
                                String.format("%.2f", maxPossibleCgpa) to "Max",
                            ),
                            valueColor = Color.White,
                            labelColor = Color.White.copy(alpha = 0.6f),
                            dividerColor = Color.White.copy(alpha = 0.15f),
                        )
                    }
                }
            }
        }

        // Target track analysis
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Target track analysis", style = NestifyTheme.type.h3Serif, color = c.ink)
                    OutlinedTextField(
                        value = targetCgpaInput,
                        onValueChange = { targetCgpaInput = it },
                        label = { Text("Set target CGPA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    val (tone, msg) = when {
                        targetCgpa > maxPossibleCgpa -> ChipTone.Coral to "Mathematically impossible — target exceeds the max achievable CGPA (${String.format("%.2f", maxPossibleCgpa)})."
                        remainingTerms > 0 -> ChipTone.Ok to "Need a GPA of ${String.format("%.2f", requiredFutureGpa)} in each of the remaining $remainingTerms terms."
                        else -> ChipTone.Default to "All terms completed. Final CGPA is ${String.format("%.2f", runningAvg)}."
                    }
                    Column(Modifier.fillMaxWidth().clip(Radii.m).background(c.surface2).padding(12.dp)) {
                        Chip(if (tone == ChipTone.Coral) "Alert" else if (tone == ChipTone.Ok) "On track" else "Done", tone = tone)
                        Spacer(Modifier.height(6.dp))
                        Text(msg, style = NestifyTheme.type.body, color = c.ink70)
                    }
                }
            }
        }

        // Transcript reports
        if (mockReports.isNotEmpty()) {
            item { SectionHead(title = "Transcript reports", kicker = "Saved") }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    mockReports.take(2).forEach { report ->
                        NestifyCard(Modifier.weight(1f), onClick = { selectedReport = report }) {
                            Column {
                                IconTile(Icons.Default.PictureAsPdf, background = c.coralSoft, tint = c.coral)
                                Spacer(Modifier.height(Space.s))
                                OneLine(report.term, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                                Kicker("GPA ${String.format("%.2f", report.totalGpa)} · ${report.academicYear}")
                            }
                        }
                    }
                    if (mockReports.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        // 8-term breakdown
        item { SectionHead(title = "8-term breakdown", kicker = "GPA grid") }
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                    termsState.forEachIndexed { index, term ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = term.isCompleted,
                                onCheckedChange = { termsState[index] = term.copy(isCompleted = it) },
                                colors = CheckboxDefaults.colors(checkedColor = c.brand),
                            )
                            Text(term.name, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink, modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = if (term.gpa == 0f) "" else term.gpa.toString(),
                                onValueChange = { value ->
                                    val cleaned = value.toFloatOrNull() ?: 0.0f
                                    if (cleaned in 0.0f..4.0f) termsState[index] = term.copy(gpa = cleaned)
                                },
                                placeholder = { Text("0.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.width(96.dp).height(56.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    // PDF transcript viewer dialog (kept as a "printed paper" look)
    if (selectedReport != null) {
        val report = selectedReport!!
        Dialog(onDismissRequest = { selectedReport = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f).clip(Radii.l),
                color = c.surface,
            ) {
                Column(Modifier.fillMaxSize().padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = c.coral)
                            Spacer(Modifier.width(8.dp))
                            Text("PDF report viewer", style = NestifyTheme.type.h3Serif, color = c.ink)
                        }
                        IconButton(onClick = { selectedReport = null }) { Icon(Icons.Default.Close, null, tint = c.ink70) }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = c.hair2)
                    Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFFAFAFA)).border(1.dp, Color(0xFFE0E0E0)).padding(16.dp)) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.universityName.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
                            Text("OFFICIAL ACADEMIC TRANSCRIPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Student: ${report.studentName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("ID: ${report.studentId}", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("Batch: ${report.batch}", fontSize = 10.sp, color = Color.DarkGray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Term: ${report.term}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("Academic Year: ${report.academicYear}", fontSize = 10.sp, color = Color.DarkGray)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth().background(Color(0xFFEEEEEE)).padding(vertical = 6.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Code", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                Text("Course", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(2.5f))
                                Text("Cr", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                Text("Grade", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                Text("GP", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                            }
                            report.courses.forEach { course ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(course.code, fontSize = 9.sp, modifier = Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                                    Text(course.name, fontSize = 9.sp, modifier = Modifier.weight(2.5f))
                                    Text(course.credit.toString(), fontSize = 9.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                    Text(course.grade, fontSize = 9.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                    Text(String.format("%.2f", course.gp), fontSize = 9.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                                }
                                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                            }
                            Spacer(Modifier.height(24.dp))
                            Row(Modifier.fillMaxWidth().border(1.dp, Color.Black).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("TERM GPA:", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.Black)
                                Text(String.format("%.2f", report.totalGpa), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                        NButton("Dismiss", { selectedReport = null }, modifier = Modifier.weight(1f), variant = BtnVariant.Secondary)
                        NButton("Download PDF", {
                            Toast.makeText(context, "PDF downloaded to Downloads/Nestify_${report.term.replace(" ", "_")}.pdf", Toast.LENGTH_LONG).show()
                        }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
