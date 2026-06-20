package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.viewmodel.MyProjectsViewModel
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectsScreen(
    navController: NavController,
    viewModel: MyProjectsViewModel = hiltViewModel()
) {
    val projectsList by viewModel.projects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Masterpieces", fontWeight = FontWeight.Black, fontSize = 24.sp, color = NestifySlate)
                        Text("Showcasing innovation and logic", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NestifySlate)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Adding a test project for demonstration
                        viewModel.addProject(
                            ProjectModel(
                                id = java.util.UUID.randomUUID().toString(),
                                name = "Test Showcase Project",
                                motive = "Automatically generated professional showcase",
                                description = "Added by ViewModel to test dynamic flow",
                                features = listOf("Feature 1"),
                                specialities = listOf("Speciality 1"),
                                techStack = listOf("Kotlin", "Compose"),
                                libraries = listOf("Hilt"),
                                sources = emptyMap(),
                                brandLogo = com.nhbhuiyan.nestify.R.drawable.nestifyappicon,
                                demoImages = emptyList(),
                                whereToFind = "Local DB",
                                category = "Showcase"
                            )
                        )
                    }) {
                        Icon(Icons.Default.Add, "Add Project", tint = NestifySlate)
                    }
                    IconButton(onClick = { /* TODO: Sort/Filter */ }) {
                        Icon(Icons.Default.FilterList, "Filter", tint = NestifySlate)
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                ProjectHeroSection()
            }

            item {
                Text(
                    "All Projects",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NestifySlate,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(projectsList.ifEmpty { mockProjects }) { project ->
                ProjectCard(project = project) {
                    navController.navigate(Route.ProjectDetail.createRoute(project.id))
                }
            }
        }
    }
}

@Composable
fun ProjectHeroSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NestifyGradients.meshGradient())
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    "Building the Future",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Explore my technical journey\nthrough these projects.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            Icon(
                Icons.Default.Code,
                null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp)
            )
        }
    }
}

@Composable
fun ProjectCard(project: ProjectModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = NestifySlate.copy(alpha = 0.1f),
                spotColor = NestifySlate.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = NestifySkyBlue.copy(alpha = 0.2f)
                ) {
                    Image(
                        painter = painterResource(project.brandLogo),
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        project.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NestifySlate
                    )
                    Text(
                        project.category,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = NestifyGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        project.status,
                        color = NestifyGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                project.motive,
                fontSize = 14.sp,
                color = NestifySlate.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tech Stack Chips (Mini)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    project.techStack.take(3).forEach { tech ->
                        Surface(
                            color = Color(0xFFF5F7F9),
                            shape = CircleShape
                        ) {
                            Text(
                                tech,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NestifySlate,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (project.techStack.size > 3) {
                        Text("+${project.techStack.size - 3}", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
                
                Icon(
                    Icons.Default.ArrowForward,
                    null,
                    tint = NestifySlate,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
