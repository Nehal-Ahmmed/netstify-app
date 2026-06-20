package com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectPlannerScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Project Planner", fontWeight = FontWeight.Black, fontSize = 24.sp, color = NestifySlate)
                        Text("Strategize your next big thing", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NestifySlate)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Dashboard, "View Mode", tint = NestifySlate)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NestifySurface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Open Form */ },
                containerColor = NestifySlate,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Plan Project", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = NestifySurface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                PlannerHeroCard()
            }

            item {
                SectionHeader("Ongoing Strategies")
            }

            items(mockProjectPlans) { plan ->
                ProjectPlanCard(plan = plan) {
                    navController.navigate(Route.ProjectPlanDetail.createRoute(plan.id))
                }
            }
        }
    }
}

@Composable
fun PlannerHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(NestifyGradients.meshGradient())
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Plans", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("05 Projects", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                    Icon(
                        Icons.Default.Timeline, 
                        null, 
                        tint = Color.White.copy(alpha = 0.3f), 
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "On track with 85% efficiency", 
                        color = Color.White, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectPlanCard(plan: ProjectPlanModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryBadge(plan.category)
                Spacer(Modifier.weight(1f))
                PriorityBadge(plan.priority)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                plan.name,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = NestifySlate
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                plan.motive,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { plan.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = NestifySlate,
                    trackColor = NestifySlate.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "${(plan.progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = NestifySlate
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(plan.deadline, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                
                Surface(
                    color = NestifySlate.copy(alpha = 0.05f),
                    shape = CircleShape
                ) {
                    Text(
                        "${plan.phases.size} Phases",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NestifySlate,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val color = when(category) {
        "Mobile App" -> Color(0xFF3498DB)
        "Web Dev" -> Color(0xFFE67E22)
        "AI/ML" -> Color(0xFF9B59B6)
        "Backend" -> Color(0xFF27AE60)
        else -> Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            category.uppercase(),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when(priority) {
        "High" -> Color(0xFFE74C3C)
        "Medium" -> Color(0xFFF1C40F)
        else -> Color(0xFF2ECC71)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(priority, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = NestifySlate,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
