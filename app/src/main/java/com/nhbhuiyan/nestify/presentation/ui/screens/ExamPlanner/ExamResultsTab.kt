package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*
import androidx.compose.runtime.collectAsState
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity

data class CourseGrade(
    val subjectCode: String,
    val subjectName: String,
    val credits: Float,
    var selectedGrade: String = "Pending"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultsTab(viewModel: ExamPlannerViewModel) {
    val context = LocalContext.current
    val grades = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F", "Pending")

    val subjectsList by viewModel.subjects.collectAsState()

    var filterLevel by remember { mutableIntStateOf(2) }
    var filterTerm by remember { mutableIntStateOf(2) }

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    // Calculations
    val gradedCourses = filteredSubjects.filter { it.finalGrade != "Pending" }
    val totalCredits = gradedCourses.map { it.credits }.sum()
    val weightedGpSum = gradedCourses.map { it.credits * (AcademicGradingEngine.gradeToGp(it.finalGrade)) }.sum()
    val calculatedGpa = if (totalCredits > 0) weightedGpSum / totalCredits else 0.00f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filter Header Bar
        item {
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
        }

        item {
            // GPA Display Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF27AE60), Color(0xFF1E8449))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Term GPA Result (L$filterLevel T$filterTerm)",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.2f", calculatedGpa),
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Acquired Grades Setup",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = NestifySlate
            )
        }

        items(filteredSubjects) { course ->
            var expanded by remember { mutableStateOf(false) }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            course.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NestifySlate
                        )
                        Text(
                            course.name,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                        Text(
                            "${course.credits} Credits",
                            fontSize = 10.sp,
                            color = NestifySlate.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Grade Select Box
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.05f))
                                .clickable { expanded = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = Color.Transparent
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    course.finalGrade,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (course.finalGrade == "Pending") Color.Gray else NestifySlate
                                )
                                Icon(Icons.Default.ArrowDropDown, null, tint = NestifySlate)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            grades.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.updateSubject(course.copy(finalGrade = g))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            // PDF & Share Action Cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Actions & Export",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NestifySlate
                    )

                    Button(
                        onClick = {
                            viewModel.insertTermReport(
                                TermReportEntity(
                                    level = filterLevel,
                                    term = filterTerm,
                                    gpa = calculatedGpa,
                                    pdfLocalPath = "cache/REP_L${filterLevel}T${filterTerm}.pdf",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            Toast.makeText(context, "Saved L${filterLevel}T${filterTerm} report to database dashboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to CGPA Dashboard", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val pdfCourses = filteredSubjects.map { subject ->
                                    CourseGrade(
                                        subjectCode = subject.code,
                                        subjectName = subject.name,
                                        credits = subject.credits,
                                        selectedGrade = subject.finalGrade
                                    )
                                }
                                val file = AcademicPdfGenerator.generateTermPdf(
                                    context = context,
                                    level = filterLevel,
                                    term = filterTerm,
                                    gpa = calculatedGpa,
                                    courses = pdfCourses
                                )
                                if (file != null) {
                                    AcademicPdfGenerator.sharePdf(context, file)
                                } else {
                                    Toast.makeText(context, "Failed to generate report PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share PDF")
                        }

                        OutlinedButton(
                            onClick = {
                                val pdfCourses = filteredSubjects.map { subject ->
                                    CourseGrade(
                                        subjectCode = subject.code,
                                        subjectName = subject.name,
                                        credits = subject.credits,
                                        selectedGrade = subject.finalGrade
                                    )
                                }
                                val file = AcademicPdfGenerator.generateTermPdf(
                                    context = context,
                                    level = filterLevel,
                                    term = filterTerm,
                                    gpa = calculatedGpa,
                                    courses = pdfCourses
                                )
                                if (file != null) {
                                    Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Failed to generate report PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF")
                        }
                    }
                }
            }
        }
    }
}
