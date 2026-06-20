package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExamFormSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSaveExam: (MockExamData) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var examType by remember { mutableStateOf("Final") }
    var date by remember { mutableStateOf("Select Date") }
    var time by remember { mutableStateOf("Select Time") }
    var venue by remember { mutableStateOf("") }
    var seatNo by remember { mutableStateOf("") }
    
    var newTopic by remember { mutableStateOf("") }
    val syllabusTopics = remember { mutableStateListOf<String>() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFECF0F1)) },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Add New Exam",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NestifySlate
            )
            Text(
                text = "Let's organize your study plan",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NestifySlate,
                unfocusedBorderColor = Color(0xFFECF0F1),
                focusedLabelColor = NestifySlate,
                cursorColor = NestifySlate,
                unfocusedContainerColor = Color(0xFFF8FAFB),
                focusedContainerColor = Color.White
            )

            // Subject Name
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject Name") },
                placeholder = { Text("e.g. Data Structures") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.MenuBook, null, tint = NestifySlate) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exam Type Selector
            Text("Exam Type", fontWeight = FontWeight.Bold, color = NestifySlate, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf("Quiz", "Midterm", "Final", "Lab")
                types.forEach { type ->
                    val isSelected = examType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NestifySlate else Color(0xFFF8FAFB))
                            .clickable { examType = type }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date and Time
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { /* Show Date Picker */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = NestifySlate, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Date", fontSize = 10.sp, color = Color.Gray)
                            Text(date, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                OutlinedCard(
                    onClick = { /* Show Time Picker */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = NestifySlate, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Time", fontSize = 10.sp, color = Color.Gray)
                            Text(time, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Venue and Seat
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    placeholder = { Text("Room 101") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = seatNo,
                    onValueChange = { seatNo = it },
                    label = { Text("Seat / Roll") },
                    placeholder = { Text("A-12") },
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Syllabus Builder
            Text("Syllabus / Topics", fontWeight = FontWeight.Bold, color = NestifySlate, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTopic,
                    onValueChange = { newTopic = it },
                    placeholder = { Text("Add study topic") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newTopic.isNotBlank()) {
                            syllabusTopics.add(newTopic)
                            newTopic = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = NestifySlate)
                ) {
                    Icon(Icons.Default.Add, "Add Topic", tint = Color.White)
                }
            }

            // Topic Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                syllabusTopics.forEach { topic ->
                    InputChip(
                        selected = false,
                        onClick = { syllabusTopics.remove(topic) },
                        label = { Text(topic) },
                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = NestifySkyBlue.copy(alpha = 0.2f),
                            labelColor = NestifySlate
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NestifySlate.copy(alpha = 0.1f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val exam = MockExamData(
                        subject = subject,
                        date = date,
                        time = time,
                        progress = 0f,
                        venue = venue,
                        type = examType
                    )
                    onSaveExam(exam)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
            ) {
                Text("Schedule Exam", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

data class MockExamData(
    val subject: String,
    val date: String,
    val time: String,
    val progress: Float,
    val venue: String,
    val type: String
)
