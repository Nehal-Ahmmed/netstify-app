package com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectPlanDetailScreen(navController: NavController, planId: String) {
    val plan = mockProjectPlans.find { it.id == planId } ?: mockProjectPlans.first()
    val c = NestifyTheme.colors

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Strategy Detail",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Edit,
                    onClick = { /* Edit */ },
                    contentDescription = "Edit",
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
            // ── Dark hero header with circular progress ───────────────────────
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Kicker(plan.category.uppercase(), color = Color.White.copy(alpha = 0.6f))
                            Spacer(Modifier.height(Space.xs))
                            Text(
                                plan.name,
                                style = NestifyTheme.type.h1Serif,
                                color = Color.White,
                            )
                        }
                        Spacer(Modifier.width(Space.l))
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { plan.progress },
                                modifier = Modifier.size(60.dp),
                                color = Color.White,
                                strokeWidth = 6.dp,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )
                            Text(
                                "${(plan.progress * 100).toInt()}%",
                                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // ── Info rows ─────────────────────────────────────────────────────
            NestifyCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.l)) {
                    PlanInfoRow(Icons.Default.Flag, "Motive", plan.motive)
                    PlanInfoRow(Icons.Default.DateRange, "Timeline", "${plan.startDate} - ${plan.deadline}")
                }
            }

            // ── Technical roadmap ─────────────────────────────────────────────
            Column {
                SectionHead(title = "Technical Roadmap")
                Spacer(Modifier.height(Space.m))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    plan.techStack.forEach { tech ->
                        Chip(tech, tone = ChipTone.Soft)
                    }
                }
            }

            // ── Execution phases ──────────────────────────────────────────────
            Column {
                SectionHead(title = "Execution Phases")
                Spacer(Modifier.height(Space.m))
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    plan.phases.forEach { phase ->
                        PhaseItem(phase)
                    }
                }
            }
        }
    }
}

@Composable
fun PlanInfoRow(icon: ImageVector, label: String, value: String) {
    val c = NestifyTheme.colors
    Row(verticalAlignment = Alignment.Top) {
        IconTile(icon, size = 40.dp)
        Spacer(Modifier.width(Space.m))
        Column {
            Kicker(label)
            Spacer(Modifier.height(2.dp))
            Text(value, style = NestifyTheme.type.body, color = c.ink)
        }
    }
}

@Composable
fun PhaseItem(phase: ProjectPhase) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (phase.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    null,
                    tint = if (phase.isCompleted) c.ok else c.ink30,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(Space.m))
                Text(
                    phase.name,
                    style = NestifyTheme.type.h3Serif,
                    color = c.ink,
                )
            }

            if (phase.tasks.isNotEmpty()) {
                Spacer(Modifier.height(Space.m))
                phase.tasks.forEach { task ->
                    Row(
                        modifier = Modifier.padding(start = 34.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (task.isDone) c.ok else c.ink30, CircleShape)
                        )
                        Spacer(Modifier.width(Space.s))
                        Text(
                            task.name,
                            style = NestifyTheme.type.body,
                            color = if (task.isDone) c.ink50 else c.ink70,
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                        )
                    }
                }
            }
        }
    }
}
