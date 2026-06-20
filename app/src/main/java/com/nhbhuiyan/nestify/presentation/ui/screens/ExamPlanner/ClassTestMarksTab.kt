package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity

@Composable
fun ClassTestMarksTab(viewModel: ExamPlannerViewModel) {
    val subjectsList by viewModel.subjects.collectAsState()
    val classTestMarksMap by viewModel.classTestMarks.collectAsState()

    var filterLevel by remember { mutableIntStateOf(2) }
    var filterTerm by remember { mutableIntStateOf(2) }

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val safeSelectedIndex = if (selectedIndex >= filteredSubjects.size) 0 else selectedIndex
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filter Header Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = NestifySlate,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Viewing Filter:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NestifySlate
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NestifySlate.copy(alpha = 0.05f))
                            .clickable {
                                filterLevel = if (filterLevel == 4) 1 else filterLevel + 1
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("L$filterLevel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                        Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NestifySlate.copy(alpha = 0.05f))
                            .clickable {
                                filterTerm = if (filterTerm == 2) 1 else 2
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("T$filterTerm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                        Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                    }
                }
            }
        }

        Text(
            "Current Term Internal Marks Grid (L$filterLevel T$filterTerm)",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = NestifySlate
        )

        // Horizontal Scrollable Grid Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(NestifySlate.copy(alpha = 0.05f))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Subject", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(90.dp))
                    Text("Cr", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(45.dp), textAlign = TextAlign.Center)
                    Text("CT 1", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                    Text("CT 2", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                    Text("CT 3", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                    Text("CT 4", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                    Text("Att (30/40)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                    Text("Total (90/120)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate, modifier = Modifier.width(95.dp), textAlign = TextAlign.End)
                }

                // Table Data Rows
                filteredSubjects.forEachIndexed { index, subject ->
                    val isSelected = safeSelectedIndex == index
                    val rowBg = if (isSelected) NestifySkyBlue.copy(alpha = 0.15f) else Color.Transparent

                    val ctMarks = classTestMarksMap[subject.id] ?: emptyList()
                    val ct1 = ctMarks.firstOrNull { it.testIndex == 1 }
                    val ct2 = ctMarks.firstOrNull { it.testIndex == 2 }
                    val ct3 = ctMarks.firstOrNull { it.testIndex == 3 }
                    val ct4 = ctMarks.firstOrNull { it.testIndex == 4 }

                    val ct1Val = ct1?.marks ?: 0f
                    val ct2Val = ct2?.marks ?: 0f
                    val ct3Val = ct3?.marks ?: 0f
                    val ct4Val = ct4?.marks ?: 0f
                    val attVal = subject.attendanceMarks

                    val internalTotal = AcademicGradingEngine.calculateInternalTotal(
                        ctList = listOf(ct1Val, ct2Val, ct3Val, ct4Val),
                        attendance = attVal,
                        credits = subject.credits
                    )

                    val ct1Str = if (ct1Val == 0f) "" else if (ct1Val % 1 == 0f) ct1Val.toInt().toString() else ct1Val.toString()
                    val ct2Str = if (ct2Val == 0f) "" else if (ct2Val % 1 == 0f) ct2Val.toInt().toString() else ct2Val.toString()
                    val ct3Str = if (ct3Val == 0f) "" else if (ct3Val % 1 == 0f) ct3Val.toInt().toString() else ct3Val.toString()
                    val ct4Str = if (ct4Val == 0f) "" else if (ct4Val % 1 == 0f) ct4Val.toInt().toString() else ct4Val.toString()
                    val attStr = if (attVal == 0f) "" else if (attVal % 1 == 0f) attVal.toInt().toString() else attVal.toString()

                    Row(
                        modifier = Modifier
                            .background(rowBg)
                            .clickable { selectedIndex = index }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.width(90.dp)) {
                            Text(subject.code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NestifySlate)
                            Text(subject.name, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                        }

                        Text(
                            subject.credits.toString(),
                            fontSize = 13.sp,
                            color = NestifySlate,
                            modifier = Modifier.width(45.dp),
                            textAlign = TextAlign.Center
                        )

                        // Editable CTs
                        CellTextField(
                            value = ct1Str,
                            onValueChange = { newVal ->
                                val floatVal = newVal.toFloatOrNull() ?: 0f
                                val entity = ct1?.copy(marks = floatVal) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 1, marks = floatVal)
                                viewModel.updateCTMark(entity)
                            },
                            modifier = Modifier.width(55.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        CellTextField(
                            value = ct2Str,
                            onValueChange = { newVal ->
                                val floatVal = newVal.toFloatOrNull() ?: 0f
                                val entity = ct2?.copy(marks = floatVal) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 2, marks = floatVal)
                                viewModel.updateCTMark(entity)
                            },
                            modifier = Modifier.width(55.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        CellTextField(
                            value = ct3Str,
                            onValueChange = { newVal ->
                                val floatVal = newVal.toFloatOrNull() ?: 0f
                                val entity = ct3?.copy(marks = floatVal) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 3, marks = floatVal)
                                viewModel.updateCTMark(entity)
                            },
                            modifier = Modifier.width(55.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        CellTextField(
                            value = ct4Str,
                            onValueChange = { newVal ->
                                val floatVal = newVal.toFloatOrNull() ?: 0f
                                val entity = ct4?.copy(marks = floatVal) ?: ClassTestMarkEntity(subjectId = subject.id, testIndex = 4, marks = floatVal)
                                viewModel.updateCTMark(entity)
                            },
                            modifier = Modifier.width(55.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        CellTextField(
                            value = attStr,
                            onValueChange = { newVal ->
                                val floatVal = newVal.toFloatOrNull() ?: 0f
                                viewModel.updateSubject(subject.copy(attendanceMarks = floatVal))
                            },
                            modifier = Modifier.width(80.dp)
                        )

                        Text(
                            String.format("%.1f", internalTotal),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = NestifySlate,
                            modifier = Modifier.width(95.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = NestifySlate.copy(alpha = 0.03f))
                }
            }
        }

        // Bottom Target Calculations Panel
        if (safeSelectedIndex in filteredSubjects.indices) {
            val selectedSub = filteredSubjects[safeSelectedIndex]

            val ctMarks = classTestMarksMap[selectedSub.id] ?: emptyList()
            val ct1Val = ctMarks.firstOrNull { it.testIndex == 1 }?.marks ?: 0f
            val ct2Val = ctMarks.firstOrNull { it.testIndex == 2 }?.marks ?: 0f
            val ct3Val = ctMarks.firstOrNull { it.testIndex == 3 }?.marks ?: 0f
            val ct4Val = ctMarks.firstOrNull { it.testIndex == 4 }?.marks ?: 0f
            val attVal = selectedSub.attendanceMarks

            val internalTotal = AcademicGradingEngine.calculateInternalTotal(
                ctList = listOf(ct1Val, ct2Val, ct3Val, ct4Val),
                attendance = attVal,
                credits = selectedSub.credits
            )

            val ratio = AcademicGradingEngine.getCreditScalingRatio(selectedSub.credits)
            val maxCourseMarks = selectedSub.credits * 100f
            val maxCT = 60f * ratio
            val maxAtt = 30f * ratio
            val maxWritten = maxCourseMarks - (maxCT + maxAtt)

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "${selectedSub.code}: Grade Predictor",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = NestifySlate
                            )
                            Text(
                                "Based on Internal Score: ${String.format("%.1f", internalTotal)} / ${maxCT + maxAtt}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "${selectedSub.credits} Credits",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = NestifySlate
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F2F3))

                    // Predictor List Table
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val gradeThresholds = listOf(
                            GradeTarget("A+ (80%)", 0.80f, NestifyGreen),
                            GradeTarget("A (75%)", 0.75f, Color(0xFF27AE60)),
                            GradeTarget("A- (70%)", 0.70f, Color(0xFF2980B9)),
                            GradeTarget("B+ (65%)", 0.65f, Color(0xFF8E44AD)),
                            GradeTarget("B (60%)", 0.60f, Color(0xFFD35400)),
                            GradeTarget("Pass (40%)", 0.40f, Color.Gray)
                        )

                        items(gradeThresholds) { target ->
                            val predictorResult = AcademicGradingEngine.predictRequiredWrittenMarks(
                                internalMarks = internalTotal,
                                credits = selectedSub.credits,
                                targetPercentage = target.percentage
                            )

                            val statusText: String
                            val statusColor: Color
                            if (!predictorResult.isPossible) {
                                statusText = "Impossible (Need ${String.format("%.1f", predictorResult.needed)})"
                                statusColor = Color(0xFFC0392B)
                            } else if (predictorResult.isSecured) {
                                statusText = "Already Secured!"
                                statusColor = NestifyGreen
                            } else {
                                statusText = String.format("Requires %.1f / %.0f in Written Exam", predictorResult.needed, predictorResult.maxWritten)
                                statusColor = NestifySlate
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF9FAFB))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(target.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        target.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NestifySlate
                                    )
                                }
                                Text(
                                    statusText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = statusColor
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
fun CellTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .border(1.dp, Color(0xFFECF0F1), RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace,
                color = NestifySlate
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class GradeTarget(
    val name: String,
    val percentage: Float,
    val color: Color
)

