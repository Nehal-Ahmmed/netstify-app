package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.presentation.ui.components.RoleGate
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun SubjectsDetailsTab(
    viewModel: ExamPlannerViewModel,
    defaultLevel: Int,
    defaultTerm: Int,
) {
    val c = NestifyTheme.colors
    val session by viewModel.sessionFlow.collectAsState(initial = null)
    val currentRole = session?.role ?: UserRole.STUDENT

    var showAddForm by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var creditsInput by remember { mutableStateOf("3.0") }
    var selectedLevel by remember(defaultLevel) { mutableStateOf(defaultLevel) }
    var selectedTerm by remember(defaultTerm) { mutableStateOf(defaultTerm) }

    var filterLevel by remember(defaultLevel) { mutableStateOf(defaultLevel) }
    var filterTerm by remember(defaultTerm) { mutableStateOf(defaultTerm) }

    val coursesList by viewModel.subjects.collectAsState()
    val isEditable = filterLevel == defaultLevel && filterTerm == defaultTerm
    val filteredCourses = coursesList.filter { it.level == filterLevel && it.term == filterTerm }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        item {
            AnimatedVisibility(
                visible = showAddForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                NestifyCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                        Text("Add new course", style = NestifyTheme.type.h3Serif, color = c.ink)
                        NestifyInput(courseName, { courseName = it }, label = "Course name", placeholder = "Operating Systems")
                        Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                            NestifyInput(courseCode, { courseCode = it }, modifier = Modifier.weight(1f), label = "Code", placeholder = "CSE-303")
                            NestifyInput(creditsInput, { creditsInput = it }, modifier = Modifier.weight(1f), label = "Credits", placeholder = "3.0")
                        }
                        Column {
                            Kicker("Level")
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (1..4).forEach { lvl ->
                                    Chip("L$lvl", active = selectedLevel == lvl, onClick = { selectedLevel = lvl })
                                }
                            }
                        }
                        Column {
                            Kicker("Term")
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (1..2).forEach { trm ->
                                    Chip("T$trm", active = selectedTerm == trm, onClick = { selectedTerm = trm })
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                            NButton("Cancel", { showAddForm = false }, modifier = Modifier.weight(1f), variant = BtnVariant.Secondary)
                            NButton("Save course", {
                                if (courseName.isNotBlank() && courseCode.isNotBlank()) {
                                    viewModel.insertSubject(
                                        SubjectEntity(
                                            name = courseName,
                                            code = courseCode.uppercase(),
                                            credits = creditsInput.toFloatOrNull() ?: 3.0f,
                                            level = selectedLevel,
                                            term = selectedTerm,
                                        )
                                    )
                                    courseName = ""; courseCode = ""; creditsInput = "3.0"; showAddForm = false
                                }
                            }, modifier = Modifier.weight(1.4f))
                        }
                    }
                }
            }
        }

        item {
            LevelTermFilter(
                level = filterLevel, term = filterTerm,
                onLevel = { filterLevel = it }, onTerm = { filterTerm = it },
            )
        }

        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionHead(title = "L$filterLevel · T$filterTerm", kicker = "Registered courses", modifier = Modifier.weight(1f))
                if (isEditable) {
                    RoleGate(currentRole = currentRole, requiredRole = UserRole.CR) {
                        IconButtonChrome(
                            if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                            onClick = { showAddForm = !showAddForm },
                            tint = c.brand,
                            contentDescription = "Add course",
                        )
                    }
                }
            }
        }

        if (filteredCourses.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.MenuBook,
                    title = "No courses yet",
                    description = "No registered courses found for this level and term.",
                )
            }
        } else {
            items(filteredCourses) { course ->
                NestifyCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                        CreditBadge(course.credits)
                        Column(Modifier.weight(1f)) {
                            OneLine(course.name, style = NestifyTheme.type.label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = c.ink)
                            Kicker(course.code)
                        }
                        if (isEditable) {
                            RoleGate(currentRole = currentRole, requiredRole = UserRole.CR) {
                                IconButtonChrome(
                                    Icons.Default.DeleteOutline,
                                    onClick = { viewModel.deleteSubject(course) },
                                    tint = c.coral,
                                    contentDescription = "Delete",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CreditBadge(credits: Float) {
    val c = NestifyTheme.colors
    Box(
        Modifier
            .size(44.dp)
            .clip(Radii.m)
            .background(c.brandSoft),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (credits % 1f == 0f) credits.toInt().toString() else credits.toString(),
                style = NestifyTheme.type.h3Serif,
                color = c.brand,
            )
            Text("cr", style = NestifyTheme.type.meta, color = c.brand, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
