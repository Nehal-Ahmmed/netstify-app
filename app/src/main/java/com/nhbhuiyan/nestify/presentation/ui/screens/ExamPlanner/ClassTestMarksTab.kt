package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun ClassTestMarksTab(
    viewModel: ExamPlannerViewModel,
    defaultLevel: Int,
    defaultTerm: Int,
) {
    val c = NestifyTheme.colors
    val session by viewModel.sessionFlow.collectAsState(initial = null)
    val currentRole = session?.role ?: UserRole.STUDENT
    val classRoster by viewModel.classRoster.collectAsState()
    val selectedStudentId by viewModel.selectedStudentIdForCT.collectAsState()

    val subjectsList by viewModel.subjects.collectAsState()
    val classTestMarksMap by viewModel.classTestMarks.collectAsState()

    var filterLevel by remember(defaultLevel) { mutableIntStateOf(defaultLevel) }
    var filterTerm by remember(defaultTerm) { mutableIntStateOf(defaultTerm) }
    val isEditable = filterLevel == defaultLevel && filterTerm == defaultTerm && (currentRole.rank >= UserRole.CR.rank)

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val safeSelectedIndex = if (selectedIndex >= filteredSubjects.size) 0 else selectedIndex
    val horizontalScrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        item {
            LevelTermFilter(
                level = filterLevel, term = filterTerm,
                onLevel = { filterLevel = it }, onTerm = { filterTerm = it },
            )
        }

        if (currentRole.rank >= UserRole.CR.rank) {
            item {
                var expandedRoster by remember { mutableStateOf(false) }
                val selectedStudentName = classRoster.firstOrNull { it.rollNumber == selectedStudentId }?.displayName ?: "Myself"
                Box(Modifier.fillMaxWidth()) {
                    NButton(
                        "$selectedStudentName · ${selectedStudentId.ifEmpty { session?.rollNumber ?: "" }}",
                        onClick = { expandedRoster = true },
                        full = true,
                        variant = BtnVariant.Secondary,
                        trailingIcon = Icons.Default.ArrowDropDown,
                    )
                    DropdownMenu(expanded = expandedRoster, onDismissRequest = { expandedRoster = false }) {
                        DropdownMenuItem(
                            text = { Text("Myself (${session?.rollNumber ?: ""})") },
                            onClick = { viewModel.selectStudentIdForCT(""); expandedRoster = false },
                        )
                        classRoster.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.displayName} (${student.rollNumber})") },
                                onClick = { viewModel.selectStudentIdForCT(student.rollNumber); expandedRoster = false },
                            )
                        }
                    }
                }
            }
        }

        item { SectionHead(title = "Internal marks", kicker = "L$filterLevel · T$filterTerm grid") }

        // Horizontally-scrollable marks grid
        item {
            NestifyCard(Modifier.fillMaxWidth(), padding = 0.dp) {
                Column(Modifier.horizontalScroll(horizontalScrollState)) {
                    Row(
                        Modifier.background(c.surface2).padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HeadCell("Subject", 90.dp)
                        HeadCell("Cr", 40.dp, TextAlign.Center)
                        HeadCell("CT1", 52.dp, TextAlign.Center)
                        HeadCell("CT2", 52.dp, TextAlign.Center)
                        HeadCell("CT3", 52.dp, TextAlign.Center)
                        HeadCell("CT4", 52.dp, TextAlign.Center)
                        HeadCell("Att", 70.dp, TextAlign.Center)
                        HeadCell("Total", 80.dp, TextAlign.End)
                    }
                    HorizontalDivider(color = c.hair2)
                    filteredSubjects.forEachIndexed { index, subject ->
                        val isSelected = safeSelectedIndex == index
                        val ctMarks = classTestMarksMap[subject.id] ?: emptyList()
                        val ct1 = ctMarks.firstOrNull { it.testIndex == 1 }
                        val ct2 = ctMarks.firstOrNull { it.testIndex == 2 }
                        val ct3 = ctMarks.firstOrNull { it.testIndex == 3 }
                        val ct4 = ctMarks.firstOrNull { it.testIndex == 4 }
                        val attVal = subject.attendanceMarks
                        val internalTotal = AcademicGradingEngine.calculateInternalTotal(
                            ctList = listOf(ct1?.marks ?: 0f, ct2?.marks ?: 0f, ct3?.marks ?: 0f, ct4?.marks ?: 0f),
                            attendance = attVal, credits = subject.credits,
                        )

                        Row(
                            Modifier
                                .background(if (isSelected) c.brandSoft else Color.Transparent)
                                .clickable { selectedIndex = index }
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.width(90.dp)) {
                                Text(subject.code, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                                OneLine(subject.name, style = NestifyTheme.type.meta, color = c.ink50)
                            }
                            Text(displayMark(subject.credits), style = NestifyTheme.type.label, color = c.ink70, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                            CellTextField(displayMark(ct1?.marks ?: 0f), { v ->
                                val f = v.toFloatOrNull() ?: 0f
                                viewModel.updateCTMark(ct1?.copy(marks = f) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 1, marks = f))
                            }, enabled = isEditable, modifier = Modifier.width(52.dp))
                            Spacer(Modifier.width(4.dp))
                            CellTextField(displayMark(ct2?.marks ?: 0f), { v ->
                                val f = v.toFloatOrNull() ?: 0f
                                viewModel.updateCTMark(ct2?.copy(marks = f) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 2, marks = f))
                            }, enabled = isEditable, modifier = Modifier.width(52.dp))
                            Spacer(Modifier.width(4.dp))
                            CellTextField(displayMark(ct3?.marks ?: 0f), { v ->
                                val f = v.toFloatOrNull() ?: 0f
                                viewModel.updateCTMark(ct3?.copy(marks = f) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 3, marks = f))
                            }, enabled = isEditable, modifier = Modifier.width(52.dp))
                            Spacer(Modifier.width(4.dp))
                            CellTextField(displayMark(ct4?.marks ?: 0f), { v ->
                                val f = v.toFloatOrNull() ?: 0f
                                viewModel.updateCTMark(ct4?.copy(marks = f) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 4, marks = f))
                            }, enabled = isEditable, modifier = Modifier.width(52.dp))
                            Spacer(Modifier.width(4.dp))
                            CellTextField(displayMark(attVal), { v ->
                                viewModel.updateSubject(subject.copy(attendanceMarks = v.toFloatOrNull() ?: 0f))
                            }, enabled = isEditable, modifier = Modifier.width(70.dp))
                            Text(
                                String.format("%.1f", internalTotal),
                                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                                color = c.ink,
                                modifier = Modifier.width(80.dp),
                                textAlign = TextAlign.End,
                            )
                        }
                        HorizontalDivider(color = c.hair2)
                    }
                }
            }
        }

        // Grade predictor for the selected subject
        if (isEditable && safeSelectedIndex in filteredSubjects.indices) {
            val selectedSub = filteredSubjects[safeSelectedIndex]
            val ctMarks = classTestMarksMap[selectedSub.id] ?: emptyList()
            val internalTotal = AcademicGradingEngine.calculateInternalTotal(
                ctList = listOf(
                    ctMarks.firstOrNull { it.testIndex == 1 }?.marks ?: 0f,
                    ctMarks.firstOrNull { it.testIndex == 2 }?.marks ?: 0f,
                    ctMarks.firstOrNull { it.testIndex == 3 }?.marks ?: 0f,
                    ctMarks.firstOrNull { it.testIndex == 4 }?.marks ?: 0f,
                ),
                attendance = selectedSub.attendanceMarks, credits = selectedSub.credits,
            )
            val ratio = AcademicGradingEngine.getCreditScalingRatio(selectedSub.credits)
            val maxInternal = (60f * ratio) + (30f * ratio)

            item {
                NestifyCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${selectedSub.code} · Grade predictor", style = NestifyTheme.type.h3Serif, color = c.ink)
                                OneLine("Internal: ${String.format("%.1f", internalTotal)} / ${String.format("%.0f", maxInternal)}", style = NestifyTheme.type.meta, color = c.ink50)
                            }
                            Chip("${selectedSub.credits} cr", tone = ChipTone.Soft)
                        }
                        HorizontalDivider(color = c.hair2)
                        val targets = listOf(
                            "A+ (80%)" to 0.80f, "A (75%)" to 0.75f, "A- (70%)" to 0.70f,
                            "B+ (65%)" to 0.65f, "B (60%)" to 0.60f, "Pass (40%)" to 0.40f,
                        )
                        targets.forEach { (name, pct) ->
                            val res = AcademicGradingEngine.predictRequiredWrittenMarks(internalTotal, selectedSub.credits, pct)
                            val (tone, statusColor, statusText) = when {
                                !res.isPossible -> Triple(ChipTone.Coral, c.coral, "Impossible · need ${String.format("%.1f", res.needed)}")
                                res.isSecured -> Triple(ChipTone.Ok, c.ok, "Already secured")
                                else -> Triple(ChipTone.Default, c.ink70, "Need ${String.format("%.1f", res.needed)} / ${String.format("%.0f", res.maxWritten)} written")
                            }
                            Row(
                                Modifier.fillMaxWidth().clip(Radii.s).background(c.surface2).padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Chip(name, tone = tone)
                                Text(statusText, style = NestifyTheme.type.meta, color = statusColor, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeadCell(text: String, width: androidx.compose.ui.unit.Dp, align: TextAlign = TextAlign.Start) {
    Text(
        text,
        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
        color = NestifyTheme.colors.ink70,
        modifier = Modifier.width(width),
        textAlign = align,
    )
}

private fun displayMark(value: Float): String =
    if (value == 0f) "" else if (value % 1f == 0f) value.toInt().toString() else value.toString()

@Composable
fun CellTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val c = NestifyTheme.colors
    Box(
        modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, c.hair, RoundedCornerShape(8.dp))
            .background(c.surface2)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = NestifyTheme.type.label.fontSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace,
                color = c.ink,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

data class GradeTarget(
    val name: String,
    val percentage: Float,
)
