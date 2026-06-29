package com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ProgressBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun ProjectPlannerScreen(navController: NavController) {
    val c = NestifyTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Project Planner",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Dashboard,
                    onClick = { /* TODO: View Mode */ },
                    contentDescription = "View Mode",
                )
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.l,
                bottom = Space.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.l),
        ) {
            item { PlannerHeroCard() }

            item {
                SectionHead(title = "Ongoing Strategies", kicker = "Roadmap")
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Kicker("ACTIVE PLANS", color = Color.White.copy(alpha = 0.6f))
                        Spacer(Modifier.height(Space.xs))
                        Text(
                            "${mockProjectPlans.size} Projects",
                            style = NestifyTheme.type.displaySerif,
                            color = Color.White,
                        )
                    }
                    Icon(
                        Icons.Default.Timeline,
                        null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(Space.l))
                Chip("On track · 85% efficiency", tone = ChipTone.Ok)
            }
        }
    }
}

@Composable
fun ProjectPlanCard(plan: ProjectPlanModel, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryBadge(plan.category)
                Spacer(Modifier.weight(1f))
                PriorityBadge(plan.priority)
            }

            Spacer(Modifier.height(Space.m))

            OneLine(
                plan.name,
                style = NestifyTheme.type.h3Serif,
                color = c.ink,
            )

            Spacer(Modifier.height(Space.xs))

            OneLine(
                plan.motive,
                style = NestifyTheme.type.body,
                color = c.ink50,
            )

            Spacer(Modifier.height(Space.l))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressBar(value = plan.progress, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(Space.m))
                Text(
                    "${(plan.progress * 100).toInt()}%",
                    style = NestifyTheme.type.label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = c.ink,
                )
            }

            Spacer(Modifier.height(Space.m))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = c.ink50, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(Space.xs))
                    Text(plan.deadline, style = NestifyTheme.type.meta, color = c.ink50)
                }

                Kicker("${plan.phases.size} Phases")
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val tone = when (category) {
        "Mobile App" -> ChipTone.Brand
        "Web Dev" -> ChipTone.Warn
        "AI/ML" -> ChipTone.Soft
        "Backend" -> ChipTone.Ok
        else -> ChipTone.Default
    }
    Chip(category.uppercase(), tone = tone)
}

@Composable
fun PriorityBadge(priority: String) {
    val c = NestifyTheme.colors
    val color = when (priority) {
        "High" -> c.coral
        "Medium" -> c.warn
        else -> c.ok
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(Modifier.width(Space.xs))
        Text(priority, style = NestifyTheme.type.meta, color = c.ink50)
    }
}
