package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*

import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsDetailsTab(viewModel: ExamPlannerViewModel) {
    var showAddForm by remember { mutableStateOf(false) }

    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var creditsInput by remember { mutableStateOf("3.0") }
    var selectedLevel by remember { mutableStateOf(2) }
    var selectedTerm by remember { mutableStateOf(2) }

    // Filter states
    var filterLevel by remember { mutableStateOf(2) }
    var filterTerm by remember { mutableStateOf(2) }

    // Core list state populated from DB
    val coursesList by viewModel.subjects.collectAsState()

    val levels = listOf(1, 2, 3, 4)
    val terms = listOf(1, 2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form toggle button / form block
        AnimatedVisibility(
            visible = showAddForm,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
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
                        "Add New Course",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NestifySlate
                    )

                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Course Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NestifySlate,
                            unfocusedBorderColor = Color(0xFFECF0F1)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = courseCode,
                            onValueChange = { courseCode = it },
                            label = { Text("Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NestifySlate,
                                unfocusedBorderColor = Color(0xFFECF0F1)
                            )
                        )

                        OutlinedTextField(
                            value = creditsInput,
                            onValueChange = { creditsInput = it },
                            label = { Text("Credits") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NestifySlate,
                                unfocusedBorderColor = Color(0xFFECF0F1)
                            )
                        )
                    }

                    // Level/Term selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Level", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                levels.forEach { lvl ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selectedLevel == lvl) NestifySlate else Color(0xFFF0F2F3))
                                            .clickable { selectedLevel = lvl },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            lvl.toString(),
                                            color = if (selectedLevel == lvl) Color.White else Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Term", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                terms.forEach { trm ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selectedTerm == trm) NestifySlate else Color(0xFFF0F2F3))
                                            .clickable { selectedTerm = trm },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            trm.toString(),
                                            color = if (selectedTerm == trm) Color.White else Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddForm = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (courseName.isNotBlank() && courseCode.isNotBlank()) {
                                    val credits = creditsInput.toFloatOrNull() ?: 3.0f
                                    viewModel.insertSubject(
                                        SubjectEntity(
                                            name = courseName,
                                            code = courseCode.uppercase(),
                                            credits = credits,
                                            level = selectedLevel,
                                            term = selectedTerm
                                        )
                                    )
                                    // Reset inputs
                                    courseName = ""
                                    courseCode = ""
                                    creditsInput = "3.0"
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
                        ) {
                            Text("Save Course")
                        }
                    }
                }
            }
        }

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
                    // Level Filter Selector
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

                    // Term Filter Selector
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

        // Action Header list view
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Courses in L$filterLevel T$filterTerm",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = NestifySlate
            )
            IconButton(
                onClick = { showAddForm = !showAddForm },
                colors = IconButtonDefaults.iconButtonColors(containerColor = NestifySlate)
            ) {
                Icon(
                    imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Add Course",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        val filteredCourses = coursesList.filter { it.level == filterLevel && it.term == filterTerm }

        if (filteredCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFECF0F1), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No registered courses found.", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCourses) { course ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NestifySlate.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    course.credits.toString(),
                                    fontWeight = FontWeight.Black,
                                    color = NestifySlate,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    course.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NestifySlate
                                )
                                Text(
                                    course.code,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteSubject(course) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
