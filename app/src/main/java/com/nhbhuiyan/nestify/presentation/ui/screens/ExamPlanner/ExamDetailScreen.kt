package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    navController: NavController,
    subjectName: String,
    viewModel: ExamPlannerViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val subject = remember(subjects, subjectName) {
        subjects.firstOrNull { it.name == subjectName }
    }

    var selectedSection by remember { mutableStateOf("A") }
    var newTopicTitle by remember { mutableStateOf("") }
    var newTopicPriority by remember { mutableIntStateOf(3) }

    if (subject == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Loading...", fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NestifySurface)
                )
            },
            containerColor = NestifySurface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NestifySlate)
            }
        }
        return
    }

    val allTopicsMap by viewModel.syllabusTopics.collectAsState()
    val syllabusTopics = remember(allTopicsMap, subject.id) {
        allTopicsMap[subject.id] ?: emptyList()
    }

    // Calculations
    val sectionTopics = syllabusTopics.filter { it.section == selectedSection }
    val completedCount = syllabusTopics.count { it.isCompleted }
    val revisedCount = syllabusTopics.count { it.isRevised }
    val totalCount = syllabusTopics.size
    val prepProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0.0f
    val revisionProgress = if (totalCount > 0) revisedCount.toFloat() / totalCount else 0.0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subjectName, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NestifySurface)
            )
        },
        containerColor = NestifySurface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Progress Card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { prepProgress },
                                modifier = Modifier.size(100.dp),
                                color = NestifyGreen,
                                strokeWidth = 8.dp,
                                trackColor = NestifyGreen.copy(alpha = 0.1f)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(prepProgress * 100).toInt()}%",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NestifySlate
                                )
                                Text("Done", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Syllabus Milestones", fontWeight = FontWeight.Black, fontSize = 16.sp, color = NestifySlate)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Completed: $completedCount / $totalCount topics", fontSize = 12.sp, color = Color.DarkGray)
                            Text("Revised: $revisedCount / $totalCount topics", fontSize = 12.sp, color = Color.DarkGray)

                            Spacer(modifier = Modifier.height(10.dp))
                            // Revision mini progress
                            LinearProgressIndicator(
                                progress = { revisionProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = NestifySlate,
                                trackColor = NestifySlate.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Revision Coverage: ${(revisionProgress * 100).toInt()}%", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Topic Ingestion Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Add Syllabus Topic", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NestifySlate)
                        OutlinedTextField(
                            value = newTopicTitle,
                            onValueChange = { newTopicTitle = it },
                            placeholder = { Text("Topic name or chapter...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NestifySlate,
                                unfocusedBorderColor = Color(0xFFECF0F1)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Priority Selector
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Priority: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Row {
                                    (1..5).forEach { star ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (star <= newTopicPriority) Color(0xFFF1C40F) else Color.LightGray,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clickable { newTopicPriority = star }
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (newTopicTitle.isNotBlank()) {
                                        viewModel.insertSyllabusTopic(
                                            SyllabusTopicEntity(
                                                subjectId = subject.id,
                                                section = selectedSection,
                                                title = newTopicTitle,
                                                priority = newTopicPriority
                                            )
                                        )
                                        newTopicTitle = ""
                                        newTopicPriority = 3
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section Tabs switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NestifySlate.copy(alpha = 0.05f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedSection == "A") Color.White else Color.Transparent)
                            .clickable { selectedSection = "A" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Question Section A",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSection == "A") NestifySlate else Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedSection == "B") Color.White else Color.Transparent)
                            .clickable { selectedSection = "B" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Question Section B",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSection == "B") NestifySlate else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Syllabus Checklist Items
            if (sectionTopics.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFECF0F1), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No topics listed for Section $selectedSection yet.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                items(sectionTopics) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.03f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = topic.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = NestifySlate,
                                    modifier = Modifier.weight(1f)
                                )
                                // Star Priority Indicators
                                Row {
                                    (1..5).forEach { star ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (star <= topic.priority) Color(0xFFF1C40F) else Color(0xFFECEFF1),
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    viewModel.updateSyllabusTopic(topic.copy(priority = star))
                                                }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Study completed
                                    FilterChip(
                                        selected = topic.isCompleted,
                                        onClick = {
                                            viewModel.updateSyllabusTopic(topic.copy(isCompleted = !topic.isCompleted))
                                        },
                                        label = { Text("Study Done", fontSize = 11.sp) },
                                        leadingIcon = {
                                            if (topic.isCompleted) {
                                                Icon(Icons.Default.Done, null, modifier = Modifier.size(12.dp))
                                            }
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NestifyGreen.copy(alpha = 0.15f),
                                            selectedLabelColor = Color(0xFF27AE60)
                                        )
                                    )

                                    // Revised
                                    FilterChip(
                                        selected = topic.isRevised,
                                        onClick = {
                                            viewModel.updateSyllabusTopic(topic.copy(isRevised = !topic.isRevised))
                                        },
                                        label = { Text("Revised", fontSize = 11.sp) },
                                        leadingIcon = {
                                            if (topic.isRevised) {
                                                Icon(Icons.Default.Sync, null, modifier = Modifier.size(12.dp))
                                            }
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NestifySlate.copy(alpha = 0.1f),
                                            selectedLabelColor = NestifySlate
                                        )
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteSyllabusTopic(topic) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}
