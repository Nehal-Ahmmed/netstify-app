package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectDetailScreen(navController: NavController) {
    // In a real app, we'd get the projectId from the NavBackStackEntry
    val project = mockProjects.first() // Mocking for Phase 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Details", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, "Share")
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
            // Header Image / Brand Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(NestifyGradients.meshGradient())
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White,
                    shadowElevation = 16.dp
                ) {
                    Image(
                        painter = painterResource(project.brandLogo),
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = project.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NestifySlate
                )
                Text(
                    text = project.category,
                    fontSize = 16.sp,
                    color = NestifyGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                DetailSectionTitle("Motive")
                Text(
                    text = project.motive,
                    fontSize = 16.sp,
                    color = NestifySlate.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                DetailSectionTitle("Description")
                Text(
                    text = project.description,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                DetailSectionTitle("Key Features")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    project.features.forEach { feature ->
                        FeatureChip(feature)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                DetailSectionTitle("Tech Stack")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    project.techStack.forEach { tech ->
                        TechChip(tech)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                DetailSectionTitle("Gallery / Demo")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 24.dp)
                ) {
                    items(project.demoImages) { imageRes ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .height(160.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                DetailSectionTitle("Where to find")
                InfoCard(Icons.Default.Map, project.whereToFind)

                Spacer(modifier = Modifier.height(16.dp))

                DetailSectionTitle("Sources")
                project.sources.forEach { (platform, url) ->
                    InfoCard(Icons.Default.Link, "$platform: $url")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = NestifySlate,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun FeatureChip(feature: String) {
    Surface(
        color = NestifySkyBlue.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Bolt, null, tint = NestifySlate, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(feature, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
        }
    }
}

@Composable
fun TechChip(tech: String) {
    Surface(
        color = NestifySlate.copy(alpha = 0.05f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, NestifySlate.copy(alpha = 0.1f))
    ) {
        Text(
            tech,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = NestifySlate,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun InfoCard(icon: ImageVector, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = NestifySlate, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}
