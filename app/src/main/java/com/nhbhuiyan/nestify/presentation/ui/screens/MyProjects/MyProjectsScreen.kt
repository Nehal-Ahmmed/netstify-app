package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.viewmodel.MyProjectsViewModel
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun MyProjectsScreen(
    navController: NavController,
    viewModel: MyProjectsViewModel = hiltViewModel()
) {
    val projectsList by viewModel.projects.collectAsState()
    val c = NestifyTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "My Masterpieces",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Add,
                    onClick = {
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
                    },
                    contentDescription = "Add Project",
                )
                IconButtonChrome(
                    Icons.Default.FilterList,
                    onClick = { /* TODO: Sort/Filter */ },
                    contentDescription = "Filter",
                )
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.l,
                bottom = GlassNavSpace,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.l),
        ) {
            item { ProjectHeroSection() }

            item {
                SectionHead(title = "All Projects", kicker = "Showcase")
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
    val c = NestifyTheme.colors
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
            Column {
                Kicker("BUILDING THE FUTURE", color = Color.White.copy(alpha = 0.6f))
                Spacer(Modifier.height(Space.s))
                Text(
                    "Explore my technical\njourney through projects.",
                    style = NestifyTheme.type.h2Serif,
                    color = Color.White,
                )
            }
            Icon(
                Icons.Default.Code,
                null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 16.dp)
            )
        }
    }
}

@Composable
fun ProjectCard(project: ProjectModel, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(Radii.m)
                        .background(c.brandSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(project.brandLogo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(Modifier.width(Space.m))
                Column(Modifier.weight(1f)) {
                    OneLine(
                        project.name,
                        style = NestifyTheme.type.h3Serif,
                        color = c.ink,
                    )
                    Kicker(project.category)
                }
                Chip(project.status, tone = ChipTone.Ok)
            }

            Spacer(Modifier.height(Space.m))

            Text(
                project.motive,
                style = NestifyTheme.type.body,
                color = c.ink70,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(Space.l))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.xs)) {
                    project.techStack.take(3).forEach { tech ->
                        Chip(tech, tone = ChipTone.Soft)
                    }
                    if (project.techStack.size > 3) {
                        Text(
                            "+${project.techStack.size - 3}",
                            style = NestifyTheme.type.meta,
                            color = c.ink50,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    null,
                    tint = c.ink50,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
