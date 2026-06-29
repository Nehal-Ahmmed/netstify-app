package com.nhbhuiyan.nestify.presentation.ui.screens.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.domain.model.ReminderType
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory
import com.nhbhuiyan.nestify.domain.model.ScheduleItem
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ScrollableTabPill
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val c = NestifyTheme.colors
    val scheduleItemsByCategory by viewModel.scheduleItemsByCategory.collectAsState()

    val categories = remember {
        listOf(
            ScheduleCategory(id = 1, name = "Weekly", colorHex = "#3498DB"),
            ScheduleCategory(id = 2, name = "Monthly", colorHex = "#9B59B6"),
            ScheduleCategory(id = 3, name = "Yearly", colorHex = "#27AE60")
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { categories.size }
    )
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ScheduleItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    var filterType by remember { mutableStateOf("Date wise") } // "All" or "Date wise"

    LaunchedEffect(Unit) {
        viewModel.overlapWarning.collectLatest { warningMessage ->
            snackbarHostState.showSnackbar(message = warningMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(c.canvas)
        ) {
            NestifyAppBar(
                title = "My Routine",
                subtitle = "Manage your time effectively",
                trailing = {
                    IconButtonChrome(
                        icon = Icons.Default.Add,
                        onClick = {
                            editingItem = null
                            showBottomSheet = true
                        },
                        tint = c.brand,
                        contentDescription = "Add Schedule"
                    )
                }
            )

            // Category Selector (Tabs)
            ScrollableTabPill(
                tabs = categories.map { it.name },
                active = pagerState.currentPage,
                onChange = { index ->
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                modifier = Modifier.padding(horizontal = Space.screen, vertical = Space.m)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true,
                beyondViewportPageCount = 1 // Pre-load pages for speed
            ) { page ->
                val category = categories[page]
                val categoryItems = scheduleItemsByCategory[category.id] ?: emptyList()
                val accent = Color(android.graphics.Color.parseColor(category.colorHex))

                var selectedDayOfWeek by remember { mutableStateOf(1) }
                var selectedDateOfMonth by remember { mutableStateOf(1) }
                var selectedMonth by remember { mutableStateOf(1) }

                // Memoize filtered items to prevent heavy re-filtering during UI updates/swiping
                val filteredItems by remember(categoryItems, filterType, selectedDayOfWeek, selectedDateOfMonth, selectedMonth) {
                    derivedStateOf {
                        if (filterType == "All") {
                            categoryItems
                        } else {
                            when(category.id) {
                                1L -> categoryItems.filter { it.daysOfWeek.contains(selectedDayOfWeek) }
                                2L -> categoryItems.filter { it.date == selectedDateOfMonth.toString().padStart(2, '0') }
                                else -> categoryItems.filter { it.date == "${selectedDateOfMonth.toString().padStart(2, '0')}/${selectedMonth.toString().padStart(2, '0')}" }
                            }
                        }.sortedBy { it.fromTime }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Space.screen)
                ) {

                    // Filter Toggle (Visible for all categories)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Space.m),
                        horizontalArrangement = Arrangement.spacedBy(Space.s, Alignment.End)
                    ) {
                        listOf("All", "Date wise").forEach { type ->
                            val isSelected = filterType == type
                            Chip(
                                label = type,
                                tone = if (isSelected) ChipTone.Default else ChipTone.Ghost,
                                active = isSelected,
                                onClick = { filterType = type }
                            )
                        }
                    }

                    // Dynamic Sub-Selector
                    when (category.id) {
                        1L -> {
                            val scheduledDays = remember(categoryItems) { categoryItems.flatMap { it.daysOfWeek }.toSet() }
                            DayOfWeekSelector(selectedDayOfWeek, accent, scheduledDays) { selectedDayOfWeek = it }
                        }
                        2L -> {
                            val scheduledDates = remember(categoryItems) { categoryItems.mapNotNull { it.date?.toIntOrNull() }.toSet() }
                            DateGridSelector(selectedDateOfMonth, accent, scheduledDates) { selectedDateOfMonth = it }
                        }
                        3L -> {
                            val scheduledDatesForMonth = remember(categoryItems, selectedMonth) {
                                categoryItems.filter {
                                    it.date?.split("/")?.getOrNull(1)?.toIntOrNull() == selectedMonth
                                }.mapNotNull {
                                    it.date?.split("/")?.getOrNull(0)?.toIntOrNull()
                                }.toSet()
                            }
                            YearlySelector(selectedMonth, selectedDateOfMonth, accent, scheduledDatesForMonth, onMonthChange = { selectedMonth = it }, onDateChange = { selectedDateOfMonth = it })
                        }
                    }

                    Spacer(modifier = Modifier.height(Space.l))

                    if (filteredItems.isEmpty()) {
                        EmptyScheduleState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = GlassNavSpace),
                            verticalArrangement = Arrangement.spacedBy(Space.m)
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                ScheduleTimelineItem(
                                    item = item,
                                    categoryColor = accent,
                                    onDelete = { viewModel.deleteScheduleItem(item) },
                                    onEdit = {
                                        editingItem = item
                                        showBottomSheet = true
                                    },
                                    onAlarmToggle = { updatedItem ->
                                        viewModel.updateScheduleItem(updatedItem)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showBottomSheet) {
        ScheduleCreationSheet(
            sheetState = sheetState,
            categoryId = categories[pagerState.currentPage].id,
            editingItem = editingItem,
            onDismissRequest = {
                showBottomSheet = false
                editingItem = null
            },
            onSaveSchedule = { item ->
                if (editingItem != null) {
                    viewModel.updateScheduleItem(item)
                } else {
                    viewModel.addScheduleItem(item)
                }
            }
        )
    }
}


@Composable
fun DayOfWeekSelector(selectedDay: Int, accentColor: Color, scheduledDays: Set<Int>, onDaySelected: (Int) -> Unit) {
    val c = NestifyTheme.colors
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, day ->
            val dayNum = index + 1
            val isSelected = selectedDay == dayNum
            val isScheduled = scheduledDays.contains(dayNum)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(Radii.m)
                    .background(
                        if (isSelected) accentColor
                        else if (isScheduled) accentColor.copy(alpha = 0.15f)
                        else Color.Transparent
                    )
                    .clickable { onDaySelected(dayNum) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = day.take(1),
                    style = NestifyTheme.type.meta,
                    color = if (isSelected) Color.White else if (isScheduled) accentColor else c.ink50,
                )
                Text(
                    text = dayNum.toString(),
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isSelected) Color.White else c.ink,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateGridSelector(selectedDate: Int, accentColor: Color, scheduledDates: Set<Int>, onDateSelected: (Int) -> Unit) {
    val c = NestifyTheme.colors
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 7,
        horizontalArrangement = Arrangement.Center
    ) {
        for (day in 1..31) {
            val isSelected = selectedDate == day
            val isScheduled = scheduledDates.contains(day)
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(34.dp)
                    .clip(Radii.s)
                    .background(
                        if (isSelected) accentColor
                        else if (isScheduled) accentColor.copy(alpha = 0.15f)
                        else c.surface2
                    )
                    .clickable { onDateSelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                    color = if (isSelected) Color.White else if (isScheduled) accentColor else c.ink,
                )
            }
        }
    }
}

@Composable
fun YearlySelector(selectedMonth: Int, selectedDate: Int, accentColor: Color, scheduledDates: Set<Int>, onMonthChange: (Int) -> Unit, onDateChange: (Int) -> Unit) {
    val c = NestifyTheme.colors
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButtonChrome(
                icon = Icons.Default.KeyboardArrowLeft,
                onClick = { if (selectedMonth > 1) onMonthChange(selectedMonth - 1) },
                tint = c.ink70,
                contentDescription = "Prev"
            )
            Text(
                text = months[selectedMonth - 1].uppercase(),
                style = NestifyTheme.type.kicker,
                color = c.ink,
            )
            IconButtonChrome(
                icon = Icons.Default.KeyboardArrowRight,
                onClick = { if (selectedMonth < 12) onMonthChange(selectedMonth + 1) },
                tint = c.ink70,
                contentDescription = "Next"
            )
        }
        DateGridSelector(selectedDate, accentColor, scheduledDates, onDateChange)
    }
}

@Composable
fun ScheduleTimelineItem(
    item: ScheduleItem,
    categoryColor: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onAlarmToggle: (ScheduleItem) -> Unit
) {
    val c = NestifyTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = c.surface,
            title = { Text("Delete Schedule", style = NestifyTheme.type.h3Serif, color = c.ink) },
            text = { Text("Are you sure you want to delete this schedule?", style = NestifyTheme.type.body, color = c.ink70) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = c.coral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = c.ink70)
                }
            }
        )
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            containerColor = c.surface,
            title = { Text(item.title, style = NestifyTheme.type.h3Serif, color = c.ink) },
            text = {
                Column {
                    Text(item.description.ifBlank { "No description provided." }, style = NestifyTheme.type.body, color = c.ink70)
                    Spacer(modifier = Modifier.height(Space.s))
                    Text("Time: ${item.fromTime / 60}:${(item.fromTime % 60).toString().padStart(2, '0')} - ${item.toTime / 60}:${(item.toTime % 60).toString().padStart(2, '0')}", style = NestifyTheme.type.label, color = c.ink)
                    if (item.date != null) {
                        Text("Date: ${item.date}", style = NestifyTheme.type.label, color = c.ink70)
                    }
                    Text("Repeat: ${item.repeatStrategy.name.lowercase().replaceFirstChar { it.uppercase() }}", style = NestifyTheme.type.label, color = c.ink70)
                    Text("Reminder: ${item.reminderType.name.lowercase().replaceFirstChar { it.uppercase() }}", style = NestifyTheme.type.label, color = c.ink70)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Close", color = c.brand)
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Time Indicator
        Column(
            modifier = Modifier.width(56.dp).padding(top = Space.s),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${item.fromTime / 60}:${(item.fromTime % 60).toString().padStart(2, '0')}",
                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                color = c.ink
            )
            Text(
                text = if (item.fromTime < 720) "AM" else "PM",
                style = NestifyTheme.type.meta,
                color = c.ink50
            )
        }

        // Timeline Line
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = Space.m),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = Space.s)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
            Box(
                modifier = Modifier.weight(1f).width(2.dp).background(c.hair2)
            )
        }

        // Card Content
        NestifyCard(
            modifier = Modifier.weight(1f),
            padding = Space.l,
            onClick = { showDetailsDialog = true }
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = NestifyTheme.type.h3Serif,
                        color = c.ink,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val newReminderType = when(item.reminderType) {
                                    ReminderType.ALARM -> ReminderType.NONE
                                    ReminderType.NONE -> ReminderType.ALARM
                                    ReminderType.BOTH -> ReminderType.NOTIFICATION
                                    ReminderType.NOTIFICATION -> ReminderType.BOTH
                                }
                                onAlarmToggle(item.copy(reminderType = newReminderType))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (item.reminderType == ReminderType.ALARM || item.reminderType == ReminderType.BOTH) Icons.Default.Alarm else Icons.Default.AlarmOff,
                                contentDescription = "Toggle Alarm",
                                tint = if (item.reminderType == ReminderType.ALARM || item.reminderType == ReminderType.BOTH) c.warn else c.ink50,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val newReminderType = when(item.reminderType) {
                                    ReminderType.NOTIFICATION -> ReminderType.NONE
                                    ReminderType.NONE -> ReminderType.NOTIFICATION
                                    ReminderType.BOTH -> ReminderType.ALARM
                                    ReminderType.ALARM -> ReminderType.BOTH
                                }
                                onAlarmToggle(item.copy(reminderType = newReminderType))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (item.reminderType == ReminderType.NOTIFICATION || item.reminderType == ReminderType.BOTH) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = "Toggle Notification",
                                tint = if (item.reminderType == ReminderType.NOTIFICATION || item.reminderType == ReminderType.BOTH) c.brand else c.ink50,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = c.ink50, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = c.coral, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = NestifyTheme.type.body,
                        color = c.ink50,
                        maxLines = 2,
                        modifier = Modifier.padding(top = Space.xs)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = Space.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = c.ink50,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(Space.xs))
                    Text(
                        text = item.totalDuration,
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                        color = c.ink50,
                    )

                    if (item.isAutoTask) {
                        Spacer(modifier = Modifier.width(Space.m))
                        Chip(label = "Auto", tone = ChipTone.Soft, leadingIcon = Icons.Default.AutoAwesome)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyScheduleState() {
    EmptyState(
        icon = Icons.Default.EventNote,
        title = "No schedules for today",
        description = "Enjoy your free time!"
    )
}
