package com.nhbhuiyan.nestify.presentation.ui.screens.schedule

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.ReminderType
import com.nhbhuiyan.nestify.domain.model.RepeatStrategy
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
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
    val c = NestifyTheme.colors
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
        containerColor = c.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = c.hair2) },
        shape = Radii.xl
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space.screen)
                .verticalScroll(rememberScrollState())
        ) {
            Kicker(if (editingItem == null) "New schedule" else "Edit schedule")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (editingItem == null) "Plan your productivity" else "Update this routine",
                style = NestifyTheme.type.h2Serif,
                color = c.ink
            )
            Spacer(modifier = Modifier.height(Space.xl))

            NestifyInput(
                value = title,
                onValueChange = { title = it },
                label = "Task Title",
                placeholder = "What are you planning?",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(Space.l))

            Text("Notes (Optional)", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium), color = c.ink70)
            Spacer(modifier = Modifier.height(Space.xs))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .clip(Radii.m)
                    .background(c.surface)
                    .border(1.5.dp, c.hair, Radii.m)
                    .padding(Space.m14)
            ) {
                if (description.isEmpty()) Text("Add any details…", style = NestifyTheme.type.body, color = c.ink50)
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = NestifyTheme.type.body.copy(color = c.ink),
                    cursorBrush = SolidColor(c.brand),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(Space.xl))

            // Time Selection Section
            Text("Set Duration", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
            Row(modifier = Modifier.fillMaxWidth().padding(top = Space.s)) {
                TimeBlock(
                    label = "From",
                    hour = fromTimeState.hour,
                    minute = fromTimeState.minute,
                    modifier = Modifier.weight(1f),
                    onClick = { showFromTimePicker = true }
                )
                Spacer(modifier = Modifier.width(Space.l))
                TimeBlock(
                    label = "To",
                    hour = toTimeState.hour,
                    minute = toTimeState.minute,
                    modifier = Modifier.weight(1f),
                    onClick = { showToTimePicker = true }
                )
            }
            Spacer(modifier = Modifier.height(Space.xl))

            // Reminder Settings
            Text("Reminders", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
            Spacer(modifier = Modifier.height(Space.m))

            // Alarm Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radii.l)
                    .background(if (alarmOn) c.warnSoft else c.surface2)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(Space.l).clickable { alarmOn = !alarmOn }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (alarmOn) c.warn else c.ink50
                        )
                        Spacer(modifier = Modifier.width(Space.m))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Set Alarm", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                            Text("Trigger an alarm sound", style = NestifyTheme.type.meta, color = c.ink50)
                        }
                        Switch(
                            checked = alarmOn,
                            onCheckedChange = { alarmOn = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = c.brand)
                        )
                    }

                    if (alarmOn) {
                        Column(modifier = Modifier.padding(start = Space.l, end = Space.l, bottom = Space.l)) {
                            Text("Custom Alarm Sound", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium), color = c.ink70)
                            Spacer(modifier = Modifier.height(Space.s))
                            Row {
                                NButton(
                                    label = "Pick Sound",
                                    onClick = { audioPickerLauncher.launch("audio/*") },
                                    variant = com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant.Secondary,
                                    size = BtnSize.Sm,
                                    leadingIcon = Icons.Default.AttachFile,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(Space.s))
                                NButton(
                                    label = "Record",
                                    onClick = { /* Implement Recording Logic */ },
                                    variant = com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant.Secondary,
                                    size = BtnSize.Sm,
                                    leadingIcon = Icons.Default.Mic,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (selectedAudioUri != null) {
                                Text(
                                    text = "Selected: ${selectedAudioUri?.split("/")?.lastOrNull()}",
                                    style = NestifyTheme.type.meta,
                                    color = c.ink50,
                                    modifier = Modifier.padding(top = Space.xs)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Space.m))

            // Notification Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radii.l)
                    .background(if (notificationOn) c.brandSoft else c.surface2)
                    .clickable { notificationOn = !notificationOn }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(Space.l)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (notificationOn) c.brand else c.ink50
                    )
                    Spacer(modifier = Modifier.width(Space.m))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Set Notification", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                        Text("Show a system notification", style = NestifyTheme.type.meta, color = c.ink50)
                    }
                    Switch(
                        checked = notificationOn,
                        onCheckedChange = { notificationOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = c.brand)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Space.xl))

            // Dynamic Repeat Selection
            Text("Repeat Pattern", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
            Spacer(modifier = Modifier.height(Space.m))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radii.l)
                    .background(c.surface2)
            ) {
                Column(modifier = Modifier.padding(Space.l)) {
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
                                            .clip(CircleShape)
                                            .background(if (isSelected) c.brand else c.surface)
                                            .clickable { selectedDays = if (isSelected) selectedDays - dayNum else selectedDays + dayNum },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = if (isSelected) Color.White else c.ink50)
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
                                            .clip(Radii.xs)
                                            .background(if (isSelected) c.brand else c.surface)
                                            .clickable { selectedDayOfMonth = day },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(day.toString(), style = NestifyTheme.type.meta.copy(fontWeight = FontWeight.Medium), color = if (isSelected) Color.White else c.ink)
                                    }
                                }
                            }
                        }
                        3L -> {
                            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { selectedMonthOfYear = if(selectedMonthOfYear < 12) selectedMonthOfYear + 1 else 1 }) {
                                    Text("${months[selectedMonthOfYear - 1]}", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.brand)
                                }
                                Spacer(modifier = Modifier.width(Space.s))
                                TextButton(onClick = { selectedDayOfYear = if(selectedDayOfYear < 31) selectedDayOfYear + 1 else 1 }) {
                                    Text("Day $selectedDayOfYear", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.brand)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Space.xl))

            // Auto Task Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radii.l)
                    .background(if (isAutoTask) c.brandSoft else c.surface2)
                    .clickable { isAutoTask = !isAutoTask }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(Space.l)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isAutoTask) c.brand else c.ink50
                    )
                    Spacer(modifier = Modifier.width(Space.m))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Task", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                        Text("Let Nestify handle this automatically", style = NestifyTheme.type.meta, color = c.ink50)
                    }
                    Switch(
                        checked = isAutoTask,
                        onCheckedChange = { isAutoTask = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = c.brand)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Space.xxxl))

            NButton(
                label = if (editingItem == null) "Create Schedule" else "Update Schedule",
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
                size = BtnSize.Lg,
                full = true
            )
            Spacer(modifier = Modifier.height(Space.xxxl))
        }
    }

    if (showFromTimePicker || showToTimePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showFromTimePicker = false; showToTimePicker = false }) {
            Surface(
                shape = Radii.xl,
                color = c.surface,
                modifier = Modifier.padding(Space.l)
            ) {
                Column(modifier = Modifier.padding(Space.xl), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Time", style = NestifyTheme.type.h3Serif, color = c.ink)
                    Spacer(modifier = Modifier.height(Space.xl))
                    TimePicker(
                        state = if (showFromTimePicker) fromTimeState else toTimeState,
                        colors = TimePickerDefaults.colors(
                            selectorColor = c.brand,
                            periodSelectorSelectedContainerColor = c.brandSoft,
                            timeSelectorSelectedContainerColor = c.brandSoft,
                            timeSelectorSelectedContentColor = c.brandDeep
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showFromTimePicker = false; showToTimePicker = false }) {
                            Text("Done", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.brand)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeBlock(label: String, hour: Int, minute: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    Box(
        modifier = modifier
            .clip(Radii.l)
            .background(c.surface2)
            .clickable { onClick() }
            .padding(Space.l)
    ) {
        Column {
            Text(label, style = NestifyTheme.type.meta, color = c.ink50)
            Text(
                text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                style = NestifyTheme.type.h2Serif,
                color = c.ink
            )
        }
    }
}
