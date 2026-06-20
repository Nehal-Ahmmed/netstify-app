package com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectPlanDetailScreen(navController: NavController, planId: String) {
    val plan = mockProjectPlans.find { it.id == planId } ?: mockProjectPlans.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strategy Detail", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NestifySurface)
            )
        },
        containerColor = NestifySurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Stats
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NestifySlate)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(plan.category.uppercase(), color = NestifyGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(plan.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { plan.progress },
                                modifier = Modifier.size(60.dp),
                                color = NestifyGreen,
                                strokeWidth = 6.dp,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Text("${(plan.progress * 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                PlanInfoRow(Icons.Default.Flag, "Motive", plan.motive)
                Spacer(modifier = Modifier.height(16.dp))
                PlanInfoRow(Icons.Default.DateRange, "Timeline", "${plan.startDate} - ${plan.deadline}")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Technical Roadmap", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NestifySlate)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    plan.techStack.forEach { tech ->
                        Surface(
                            color = NestifySkyBlue.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Text(tech, color = NestifySlate, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Execution Phases", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NestifySlate)
                Spacer(modifier = Modifier.height(16.dp))
                
                plan.phases.forEach { phase ->
                    PhaseItem(phase)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PlanInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = NestifySlate, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 14.sp, color = NestifySlate, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PhaseItem(phase: ProjectPhase) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (phase.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    null,
                    tint = if (phase.isCompleted) NestifyGreen else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(phase.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
            }
            
            if (phase.tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                phase.tasks.forEach { task ->
                    Row(
                        modifier = Modifier.padding(start = 32.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(if (task.isDone) NestifyGreen else Color.LightGray, CircleShape))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            task.name, 
                            fontSize = 13.sp, 
                            color = if (task.isDone) Color.Gray else NestifySlate,
                            textDecoration = if (task.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    }
                }
            }
        }
    }
}
