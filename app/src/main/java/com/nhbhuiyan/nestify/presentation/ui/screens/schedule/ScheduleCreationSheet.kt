package com.nhbhuiyan.nestify.presentation.ui.screens.schedule

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.nhbhuiyan.nestify.domain.model.ReminderType
import com.nhbhuiyan.nestify.domain.model.RepeatStrategy
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleCreationSheet(
    sheetState: SheetState,
    categoryId: Long,
    editingItem: ScheduleItem? = null,
    onDismissRequest: () -> Unit,
    onSaveSchedule: (ScheduleItem) -> Unit
) {
    var title by remember { mutableStateOf(editingItem?.title ?: "") }
    var description by remember { mutableStateOf(editingItem?.description ?: "") }
    
    // Time picker states
    val fromTimeState = rememberTimePickerState(
        initialHour = editingItem?.fromTime?.let { it / 60 } ?: 8,
        initialMinute = editingItem?.fromTime?.let { it % 60 } ?: 0
    )
    val toTimeState = rememberTimePickerState(
        initialHour = editingItem?.toTime?.let { it / 60 } ?: 10,
        initialMinute = editingItem?.toTime?.let { it % 60 } ?: 0
    )
    var showFromTimePicker by remember { mutableStateOf(false) }
    var showToTimePicker by remember { mutableStateOf(false) }

    var isAutoTask by remember { mutableStateOf(editingItem?.isAutoTask ?: false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(editingItem?.attachmentUri?.let { Uri.parse(it) }) }
    
    var selectedDays by remember { mutableStateOf(editingItem?.daysOfWeek?.toSet() ?: setOf(1)) }
    var selectedDayOfMonth by remember { mutableStateOf(editingItem?.date?.toIntOrNull() ?: 1) }
    var selectedMonthOfYear by remember { mutableStateOf(editingItem?.date?.split("/")?.getOrNull(1)?.toIntOrNull() ?: 1) }
    var selectedDayOfYear by remember { mutableStateOf(editingItem?.date?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 1) }

    var alarmOn by remember { mutableStateOf(editingItem?.reminderType == ReminderType.ALARM || editingItem?.reminderType == ReminderType.BOTH) }
    var notificationOn by remember { mutableStateOf(editingItem?.reminderType == ReminderType.NOTIFICATION || editingItem?.reminderType == ReminderType.BOTH) }
    var selectedAudioUri by remember { mutableStateOf<String?>(editingItem?.customAudioUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedAudioUri = uri?.toString() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = Color(0xFFECF0F1)) },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (editingItem == null) "New Schedule" else "Edit Schedule",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = "Plan your productivity",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2C3E50),
                unfocusedBorderColor = Color(0xFFECF0F1),
                focusedLabelColor = Color(0xFF2C3E50),
                cursorColor = Color(0xFF2C3E50),
                unfocusedContainerColor = Color(0xFFF8FAFB),
                focusedContainerColor = Color.White
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                placeholder = { Text("What are you planning?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Time Selection Section
            Text("Set Duration", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50), fontSize = 16.sp)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                TimeBlock(
                    label = "From",
                    hour = fromTimeState.hour,
                    minute = fromTimeState.minute,
                    modifier = Modifier.weight(1f),
                    onClick = { showFromTimePicker = true }
                )
                Spacer(modifier = Modifier.width(16.dp))
                TimeBlock(
                    label = "To",
                    hour = toTimeState.hour,
                    minute = toTimeState.minute,
                    modifier = Modifier.weight(1f),
                    onClick = { showToTimePicker = true }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Reminder Settings
            Text("Reminders", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Alarm Toggle
            Surface(
                color = if (alarmOn) Color(0xFFFEF5E7) else Color(0xFFF8FAFB),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp).clickable { alarmOn = !alarmOn }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (alarmOn) Color(0xFFE67E22) else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Set Alarm", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                            Text("Trigger an alarm sound", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = alarmOn,
                            onCheckedChange = { alarmOn = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE67E22), checkedTrackColor = Color(0xFFFAD7A0))
                        )
                    }
                    
                    if (alarmOn) {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                            Text("Custom Alarm Sound", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2C3E50))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Button(
                                    onClick = { audioPickerLauncher.launch("audio/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pick Sound", color = Color(0xFF2C3E50), fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { /* Implement Recording Logic */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record", color = Color(0xFF2C3E50), fontSize = 12.sp)
                                }
                            }
                            if (selectedAudioUri != null) {
                                Text(
                                    text = "Selected: ${selectedAudioUri?.split("/")?.lastOrNull()}",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Notification Toggle
            Surface(
                color = if (notificationOn) Color(0xFFEBF5FB) else Color(0xFFF8FAFB),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable { notificationOn = !notificationOn }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (notificationOn) Color(0xFF3498DB) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Set Notification", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                        Text("Show a system notification", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = notificationOn,
                        onCheckedChange = { notificationOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3498DB), checkedTrackColor = Color(0xFFAED6F1))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Repeat Selection
            Text("Repeat Pattern", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFF8FAFB),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when(categoryId) {
                        1L -> {
                            val days = listOf("S", "M", "T", "W", "T", "F", "S")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                days.forEachIndexed { index, day ->
                                    val dayNum = index + 1
                                    val isSelected = selectedDays.contains(dayNum)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(if (isSelected) Color(0xFF2C3E50) else Color.White)
                                            .clickable { selectedDays = if (isSelected) selectedDays - dayNum else selectedDays + dayNum },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        2L -> {
                            FlowRow(modifier = Modifier.fillMaxWidth()) {
                                for (day in 1..31) {
                                    val isSelected = selectedDayOfMonth == day
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) Color(0xFF9B59B6) else Color.White)
                                            .clickable { selectedDayOfMonth = day },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day.toString(), fontSize = 10.sp, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        3L -> {
                            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { selectedMonthOfYear = if(selectedMonthOfYear < 12) selectedMonthOfYear + 1 else 1 }) {
                                    Text("${months[selectedMonthOfYear - 1]}", fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { selectedDayOfYear = if(selectedDayOfYear < 31) selectedDayOfYear + 1 else 1 }) {
                                    Text("Day $selectedDayOfYear", fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Auto Task Toggle
            Surface(
                color = if (isAutoTask) Color(0xFFFDF2FA) else Color(0xFFF8FAFB),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().clickable { isAutoTask = !isAutoTask }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isAutoTask) Color(0xFF9B59B6) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Task", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                        Text("Let Nestify handle this automatically", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isAutoTask,
                        onCheckedChange = { isAutoTask = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF9B59B6), checkedTrackColor = Color(0xFFF5EEF8))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val fromTimeMinutes = fromTimeState.hour * 60 + fromTimeState.minute
                    val toTimeMinutes = toTimeState.hour * 60 + toTimeState.minute

                    val reminderType = when {
                        alarmOn && notificationOn -> ReminderType.BOTH
                        alarmOn -> ReminderType.ALARM
                        notificationOn -> ReminderType.NOTIFICATION
                        else -> ReminderType.NONE
                    }

                    val item = ScheduleItem(
                        id = editingItem?.id ?: 0,
                        categoryId = categoryId,
                        title = title.ifBlank { "Untitled" },
                        description = description,
                        fromTime = fromTimeMinutes,
                        toTime = toTimeMinutes,
                        date = when(categoryId) {
                            2L -> selectedDayOfMonth.toString().padStart(2, '0')
                            3L -> "${selectedDayOfYear.toString().padStart(2, '0')}/${selectedMonthOfYear.toString().padStart(2, '0')}"
                            else -> editingItem?.date
                        },
                        daysOfWeek = if(categoryId == 1L) selectedDays.toList() else emptyList(),
                        repeatStrategy = when(categoryId) {
                            1L -> RepeatStrategy.WEEKLY
                            2L -> RepeatStrategy.MONTHLY
                            3L -> RepeatStrategy.ANNUALLY
                            else -> RepeatStrategy.ONCE
                        },
                        reminderType = reminderType,
                        customAudioUri = selectedAudioUri,
                        attachmentUri = selectedImageUri?.toString(),
                        isAutoTask = isAutoTask,
                        createdAt = editingItem?.createdAt ?: Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                    onSaveSchedule(item)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
            ) {
                Text(if (editingItem == null) "Create Schedule" else "Update Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showFromTimePicker || showToTimePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showFromTimePicker = false; showToTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Time", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    TimePicker(state = if (showFromTimePicker) fromTimeState else toTimeState)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showFromTimePicker = false; showToTimePicker = false }) {
                            Text("Done", color = Color(0xFF2C3E50), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeBlock(label: String, hour: Int, minute: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFF8FAFB),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2C3E50)
            )
        }
    }
}
