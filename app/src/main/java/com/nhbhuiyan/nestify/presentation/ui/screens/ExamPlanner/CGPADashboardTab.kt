package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nhbhuiyan.nestify.ui.theme.*

data class AcademicReport(
    val id: String,
    val universityName: String,
    val studentName: String,
    val studentId: String,
    val batch: String,
    val term: String,
    val academicYear: String,
    val courses: List<ReportCourse>,
    val totalGpa: Float
)

data class ReportCourse(
    val code: String,
    val name: String,
    val credit: Float,
    val grade: String,
    val gp: Float
)

data class TermGpaData(
    val name: String,
    var gpa: Float,
    var isCompleted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CGPADashboardTab(viewModel: ExamPlannerViewModel) {
    val context = LocalContext.current
    var targetCgpaInput by remember { mutableStateOf("3.80") }
    var selectedReport by remember { mutableStateOf<AcademicReport?>(null) }

    val dbReports by viewModel.termReports.collectAsState()

    // 8-Term CGPA state reactive to DB
    val termsState = remember(dbReports) {
        val list = mutableListOf(
            TermGpaData("L1 T1 (Level 1 Term 1)", 3.61f, false),
            TermGpaData("L1 T2 (Level 1 Term 2)", 3.71f, false),
            TermGpaData("L2 T1 (Level 2 Term 1)", 3.87f, false),
            TermGpaData("L2 T2 (Level 2 Term 2)", 0.00f, false),
            TermGpaData("L3 T1 (Level 3 Term 1)", 0.00f, false),
            TermGpaData("L3 T2 (Level 3 Term 2)", 0.00f, false),
            TermGpaData("L4 T1 (Level 4 Term 1)", 0.00f, false),
            TermGpaData("L4 T2 (Level 4 Term 2)", 0.00f, false)
        )
        dbReports.forEach { report ->
            val index = when {
                report.level == 1 && report.term == 1 -> 0
                report.level == 1 && report.term == 2 -> 1
                report.level == 2 && report.term == 1 -> 2
                report.level == 2 && report.term == 2 -> 3
                report.level == 3 && report.term == 1 -> 4
                report.level == 3 && report.term == 2 -> 5
                report.level == 4 && report.term == 1 -> 6
                report.level == 4 && report.term == 2 -> 7
                else -> -1
            }
            if (index in 0..7) {
                list[index] = TermGpaData(list[index].name, report.gpa, true)
            }
        }
        mutableStateListOf(*list.toTypedArray())
    }

    // Dynamic reports loaded from Room DB
    val mockReports = remember(dbReports) {
        dbReports.map { report ->
            AcademicReport(
                id = report.id.toString(),
                universityName = "State University of Technology",
                studentName = "Nehal Ahmed",
                studentId = "2021-CSE-087",
                batch = "Batch 48",
                term = "Level ${report.level} Term ${report.term}",
                academicYear = "2024-2025",
                courses = listOf(
                    ReportCourse("CSE-111", "Introduction to Programming", 3.0f, "A+", 4.00f),
                    ReportCourse("MATH-113", "Differential Calculus", 3.0f, "A-", 3.50f),
                    ReportCourse("PHY-115", "Physics & Electromagnetics", 3.0f, "B+", 3.25f),
                    ReportCourse("CSE-112", "Programming Lab", 1.5f, "A+", 4.00f)
                ),
                totalGpa = report.gpa
            )
        }
    }

    // Calculations
    val completedTerms = termsState.filter { it.isCompleted }
    val runningAvg = if (completedTerms.isNotEmpty()) {
        completedTerms.map { it.gpa }.sum() / completedTerms.size
    } else {
        0.00f
    }

    val targetCgpa = targetCgpaInput.toFloatOrNull() ?: 3.80f
    val remainingTerms = 8 - completedTerms.size
    val maxPossibleCgpa = if (remainingTerms > 0) {
        (completedTerms.map { it.gpa }.sum() + (4.00f * remainingTerms)) / 8f
    } else {
        runningAvg
    }

    val requiredFutureGpa = if (remainingTerms > 0) {
        ((targetCgpa * 8) - completedTerms.map { it.gpa }.sum()) / remainingTerms
    } else {
        0.00f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section: Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NestifySlate, Color(0xFF1E272C))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Running CGPA",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.2f", runningAvg),
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = NestifyPeach,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Completed Terms",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                            Text(
                                "${completedTerms.size} of 8",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Target CGPA",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                            Text(
                                String.format("%.2f", targetCgpa),
                                color = NestifyGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Target CGPA Projection & Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "Target Track Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NestifySlate
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = targetCgpaInput,
                        onValueChange = { targetCgpaInput = it },
                        label = { Text("Set Target CGPA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NestifySlate,
                            unfocusedBorderColor = Color(0xFFECF0F1)
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Max Possible:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            String.format("%.2f", maxPossibleCgpa),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = NestifySlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Condition check alert
                if (targetCgpa > maxPossibleCgpa) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFDE8E8))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Mathematically impossible! Target exceeds max achievable CGPA (${String.format("%.2f", maxPossibleCgpa)}).",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (remainingTerms > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NestifyGreen.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, null, tint = NestifyGreen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "To achieve $targetCgpa, you need a GPA of ${String.format("%.2f", requiredFutureGpa)} in each of the remaining $remainingTerms terms.",
                                color = NestifySlate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NestifySkyBlue.copy(alpha = 0.2f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DoneAll, null, tint = NestifySlate)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "All terms completed. Overall final CGPA is ${String.format("%.2f", runningAvg)}",
                                color = NestifySlate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Academic Reports list (PDF Layouts)
        Text(
            "Academic Transcript Reports",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = NestifySlate
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mockReports.forEach { report ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, NestifySlate.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .clickable { selectedReport = report }
                        .padding(12.dp)
                ) {
                    Column {
                        // PDF Sheet look
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(1.dp, Color(0xFFECF0F1), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFC0392B),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    report.term,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NestifySlate,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "GPA: ${report.totalGpa}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NestifySlate
                        )
                        Text(
                            report.academicYear,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = NestifySlate.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        Toast
                                            .makeText(
                                                context,
                                                "Sharing ${report.term} PDF transcript...",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Section: 8 Terms Calculator Grid
        Text(
            "8-Term GPA Breakdown",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = NestifySlate
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                termsState.forEachIndexed { index, term ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = term.isCompleted,
                            onCheckedChange = { isChecked ->
                                termsState[index] = term.copy(isCompleted = isChecked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = NestifySlate)
                        )
                        Text(
                            text = term.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NestifySlate,
                            modifier = Modifier.weight(1.3f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = if (term.gpa == 0f) "" else term.gpa.toString(),
                            onValueChange = { value ->
                                val cleanedVal = value.toFloatOrNull() ?: 0.0f
                                if (cleanedVal in 0.0f..4.0f) {
                                    termsState[index] = term.copy(gpa = cleanedVal)
                                }
                            },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.7f)
                                .height(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NestifySlate,
                                unfocusedBorderColor = Color(0xFFECF0F1)
                            )
                        )
                    }
                }
            }
        }
    }

    // Modal PDF Viewer Dialog
    if (selectedReport != null) {
        val report = selectedReport!!
        Dialog(
            onDismissRequest = { selectedReport = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFFC0392B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "PDF Report Viewer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        IconButton(onClick = { selectedReport = null }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Simulated Printed Transcript Page
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA))
                            .border(1.dp, Color(0xFFE0E0E0))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = report.universityName.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "OFFICIAL ACADEMIC TRANSCRIPT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Metadata Grid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Student: ${report.studentName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("ID: ${report.studentId}", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("Batch: ${report.batch}", fontSize = 10.sp, color = Color.DarkGray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Term: ${report.term}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("Academic Year: ${report.academicYear}", fontSize = 10.sp, color = Color.DarkGray)
                                    Text("Session: Regular", fontSize = 10.sp, color = Color.DarkGray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Course Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEEEEEE))
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Code", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                Text("Course Name", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(2.5f))
                                Text("Cr", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                Text("Grade", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                Text("GP", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                            }

                            // Courses details
                            report.courses.forEach { course ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(course.code, fontSize = 9.sp, modifier = Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                                    Text(course.name, fontSize = 9.sp, modifier = Modifier.weight(2.5f))
                                    Text(course.credit.toString(), fontSize = 9.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                    Text(course.grade, fontSize = 9.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                    Text(String.format("%.2f", course.gp), fontSize = 9.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                                }
                                Divider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Summary Result Block
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Black)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "TERM GRADE POINT AVERAGE (GPA):",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = String.format("%.2f", report.totalGpa),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // Signature
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.width(100.dp).height(30.dp), contentAlignment = Alignment.Center) {
                                        Text("Signed (Dean)", fontSize = 8.sp, color = Color.LightGray)
                                    }
                                    Divider(modifier = Modifier.width(120.dp), color = Color.Black, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Controller of Examinations", fontSize = 9.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedReport = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss")
                        }
                        Button(
                            onClick = {
                                Toast
                                    .makeText(
                                        context,
                                        "PDF downloaded to Downloads/Nestify_${report.term.replace(" ", "_")}.pdf",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download PDF")
                        }
                    }
                }
            }
        }
    }
}
