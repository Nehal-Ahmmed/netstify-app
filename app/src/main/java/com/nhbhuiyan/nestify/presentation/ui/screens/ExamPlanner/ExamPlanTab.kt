package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.ui.theme.*

import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import androidx.compose.material.icons.filled.*

@Composable
fun ExamPlanTab(
    navController: NavController,
    viewModel: ExamPlannerViewModel
) {
    val subjectsList by viewModel.subjects.collectAsState()
    val syllabusTopicsMap by viewModel.syllabusTopics.collectAsState()

    var filterLevel by remember { mutableIntStateOf(2) }
    var filterTerm by remember { mutableIntStateOf(2) }

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Filter Header Bar
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NestifySlate,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Viewing Filter:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NestifySlate
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.05f))
                                .clickable {
                                    filterLevel = if (filterLevel == 4) 1 else filterLevel + 1
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("L$filterLevel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.05f))
                                .clickable {
                                    filterTerm = if (filterTerm == 2) 1 else 2
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("T$filterTerm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                        }
                    }
                }
            }

            Text(
                "Academic Exam Schedule & Goals",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = NestifySlate,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "Tap a subject to configure Section A/B syllabus checkpoints and revisions.",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filteredSubjects) { subject ->
            val topics = syllabusTopicsMap[subject.id] ?: emptyList()
            val completedCount = topics.count { it.isCompleted }
            val totalCount = topics.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0.0f

            val daysRemainingStr = getDaysRemaining(subject.examDate)
            val daysRemainingInt = daysRemainingStr.toIntOrNull() ?: 99

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Route.ExamDetail.createRoute(subject.name))
                    },
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Days Countdown Indicator
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    daysRemainingStr == "--" -> Color(0xFFF2F4F5)
                                    daysRemainingInt <= 2 -> Color(0xFFFDE8E8)
                                    daysRemainingInt <= 7 -> Color(0xFFFEF3C7)
                                    else -> NestifyGreen.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (daysRemainingStr == "--") "--" else "${daysRemainingStr}d",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = when {
                                    daysRemainingStr == "--" -> Color.Gray
                                    daysRemainingInt <= 2 -> Color(0xFFC0392B)
                                    daysRemainingInt <= 7 -> Color(0xFFD35400)
                                    else -> Color(0xFF27AE60)
                                }
                            )
                            Text(
                                text = "left",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Center Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NestifySlate
                        )
                        Text(
                            text = subject.name,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (subject.examDate.isBlank()) "Date Not Set" else subject.examDate,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Progress Arc
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(42.dp),
                            color = NestifySlate,
                            trackColor = Color(0xFFF1F4F5),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NestifySlate
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

private fun getDaysRemaining(examDateStr: String): String {
    if (examDateStr.isBlank()) return "--"
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val examDate = sdf.parse(examDateStr)
        val diffMs = examDate.time - System.currentTimeMillis()
        val days = (diffMs / (1000L * 60L * 60L * 24L)).toInt()
        if (days < 0) "0" else days.toString()
    } catch (e: Exception) {
        "--"
    }
}
