package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectDetailScreen(navController: NavController) {
    // In a real app, we'd get the projectId from the NavBackStackEntry
    val project = mockProjects.first() // Mocking for Phase 1
    val c = NestifyTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Project Details",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Share,
                    onClick = { /* Share */ },
                    contentDescription = "Share",
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screen)
                .padding(top = Space.l, bottom = Space.xl),
            verticalArrangement = Arrangement.spacedBy(Space.xl),
        ) {
            // ── Dark hero header ──────────────────────────────────────────────
            NestifyCard(
                modifier = Modifier.fillMaxWidth(),
                background = c.surfaceDk,
                padding = Space.xl,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(NestifyGradients.darkHero())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(72.dp)
                                .clip(Radii.l)
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(project.brandLogo),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(16.dp),
                                contentScale = ContentScale.Fit,
                            )
                        }
                        Spacer(Modifier.width(Space.l))
                        Column(Modifier.weight(1f)) {
                            Kicker(project.category, color = Color.White.copy(alpha = 0.6f))
                            Spacer(Modifier.height(Space.xs))
                            Text(
                                project.name,
                                style = NestifyTheme.type.h1Serif,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            DetailSection("Motive") {
                Text(project.motive, style = NestifyTheme.type.body, color = c.ink70)
            }

            DetailSection("Description") {
                Text(project.description, style = NestifyTheme.type.body, color = c.ink50)
            }

            DetailSection("Key Features") {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    project.features.forEach { feature ->
                        Chip(feature, tone = ChipTone.Soft, leadingIcon = Icons.Default.Bolt)
                    }
                }
            }

            DetailSection("Tech Stack") {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    project.techStack.forEach { tech ->
                        Chip(tech, tone = ChipTone.Default)
                    }
                }
            }

            DetailSection("Gallery / Demo") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Space.l),
                    contentPadding = PaddingValues(end = Space.screen)
                ) {
                    items(project.demoImages) { imageRes ->
                        Box(
                            Modifier
                                .width(280.dp)
                                .height(160.dp)
                                .clip(Radii.l)
                                .background(c.surface2)
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
            }

            DetailSection("Where to find") {
                InfoCard(Icons.Default.Map, project.whereToFind)
            }

            if (project.sources.isNotEmpty()) {
                DetailSection("Sources") {
                    Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                        project.sources.forEach { (platform, url) ->
                            InfoCard(Icons.Default.Link, "$platform: $url")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column {
        SectionHead(title = title)
        Spacer(Modifier.height(Space.m))
        content()
    }
}

@Composable
fun InfoCard(icon: ImageVector, text: String) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = c.brand, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Space.m))
            Text(text, style = NestifyTheme.type.body, color = c.ink70)
        }
    }
}
