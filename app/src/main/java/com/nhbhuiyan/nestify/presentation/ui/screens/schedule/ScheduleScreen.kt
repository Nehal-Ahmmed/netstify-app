package com.nhbhuiyan.nestify.presentation.ui.screens.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.nhbhuiyan.nestify.presentation.ui.components.NotebookSpreadComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
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

    Scaffold(
        containerColor = Color(0xFFF8FAFB),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showBottomSheet = true
                },
                containerColor = Color(0xFF2C3E50),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Routine",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2C3E50)
                        )
                        Text(
                            text = "Manage your time effectively",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Category Selector (Tabs)
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                edgePadding = 24.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        val color = Color(android.graphics.Color.parseColor(categories[pagerState.currentPage].colorHex))
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = color
                        )
                    }
                }
            ) {
                categories.forEachIndexed { index, category ->
                    val isSelected = pagerState.currentPage == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(
                                text = category.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF2C3E50) else Color.Gray
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true,
                beyondViewportPageCount = 1 // Pre-load pages for speed
            ) { page ->
                val category = categories[page]
                val categoryItems = scheduleItemsByCategory[category.id] ?: emptyList()

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

                NotebookSpreadComponent(
                    modifier = Modifier.fillMaxSize().padding(1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

                        // Filter Toggle (Visible for all categories)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            listOf("All", "Date wise").forEach { type ->
                                val isSelected = filterType == type
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color(0xFF2C3E50) else Color(0xFFECF0F1))
                                        .clickable { filterType = type }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = type,
                                        color = if (isSelected) Color.White else Color.Gray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Dynamic Sub-Selector
                        when (category.id) {
                            1L -> {
                                val scheduledDays = remember(categoryItems) { categoryItems.flatMap { it.daysOfWeek }.toSet() }
                                DayOfWeekSelector(selectedDayOfWeek, scheduledDays) { selectedDayOfWeek = it }
                            }
                            2L -> {
                                val scheduledDates = remember(categoryItems) { categoryItems.mapNotNull { it.date?.toIntOrNull() }.toSet() }
                                DateGridSelector(selectedDateOfMonth, Color(0xFF9B59B6), scheduledDates) { selectedDateOfMonth = it }
                            }
                            3L -> {
                                val scheduledDatesForMonth = remember(categoryItems, selectedMonth) {
                                    categoryItems.filter {
                                        it.date?.split("/")?.getOrNull(1)?.toIntOrNull() == selectedMonth
                                    }.mapNotNull {
                                        it.date?.split("/")?.getOrNull(0)?.toIntOrNull()
                                    }.toSet()
                                }
                                YearlySelector(selectedMonth, selectedDateOfMonth, scheduledDatesForMonth, onMonthChange = { selectedMonth = it }, onDateChange = { selectedDateOfMonth = it })
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (filteredItems.isEmpty()) {
                            EmptyScheduleState()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(filteredItems, key = { it.id }) { item ->
                                    ScheduleTimelineItem(
                                        item = item,
                                        categoryColor = Color(android.graphics.Color.parseColor(category.colorHex)),
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
}


@Composable
fun DayOfWeekSelector(selectedDay: Int, scheduledDays: Set<Int>, onDaySelected: (Int) -> Unit) {
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color(0xFF3498DB)
                        else if (isScheduled) Color(0xFF3498DB).copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable { onDaySelected(dayNum) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = day.take(1),
                    color = if (isSelected) Color.White else if (isScheduled) Color(0xFF3498DB) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dayNum.toString(),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateGridSelector(selectedDate: Int, accentColor: Color, scheduledDates: Set<Int>, onDateSelected: (Int) -> Unit) {
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) accentColor
                        else if (isScheduled) accentColor.copy(alpha = 0.2f)
                        else Color(0xFFF0F3F4)
                    )
                    .clickable { onDateSelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = if (isSelected) Color.White else if (isScheduled) accentColor else Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun YearlySelector(selectedMonth: Int, selectedDate: Int, scheduledDates: Set<Int>, onMonthChange: (Int) -> Unit, onDateChange: (Int) -> Unit) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { if (selectedMonth > 1) onMonthChange(selectedMonth - 1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev")
            }
            Text(
                text = months[selectedMonth - 1].uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            IconButton(onClick = { if (selectedMonth < 12) onMonthChange(selectedMonth + 1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
            }
        }
        DateGridSelector(selectedDate, Color(0xFF27AE60), scheduledDates, onDateChange)
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Schedule") },
            text = { Text("Are you sure you want to delete this schedule?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(item.title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(item.description.ifBlank { "No description provided." })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Time: ${item.fromTime / 60}:${(item.fromTime % 60).toString().padStart(2, '0')} - ${item.toTime / 60}:${(item.toTime % 60).toString().padStart(2, '0')}", fontWeight = FontWeight.Medium)
                    if (item.date != null) {
                        Text("Date: ${item.date}")
                    }
                    Text("Repeat: ${item.repeatStrategy.name.lowercase().replaceFirstChar { it.uppercase() }}")
                    Text("Reminder: ${item.reminderType.name.lowercase().replaceFirstChar { it.uppercase() }}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 8.dp)
            .clickable { showDetailsDialog = true }
    ) {
        // Time Indicator
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${item.fromTime / 60}:${(item.fromTime % 60).toString().padStart(2, '0')}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = if (item.fromTime < 720) "AM" else "PM",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        // Timeline Line
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(categoryColor)
            )
            VerticalDivider(
                modifier = Modifier.weight(1f).width(2.dp),
                color = Color(0xFFECF0F1)
            )
        }

        // Card Content
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50),
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
                                tint = if (item.reminderType == ReminderType.ALARM || item.reminderType == ReminderType.BOTH) Color(0xFFE67E22) else Color.Gray,
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
                                tint = if (item.reminderType == ReminderType.NOTIFICATION || item.reminderType == ReminderType.BOTH) Color(0xFF3498DB) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.totalDuration,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    if (item.isAutoTask) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto",
                            tint = Color(0xFF9B59B6),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto",
                            fontSize = 10.sp,
                            color = Color(0xFF9B59B6),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyScheduleState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            tint = Color(0xFFECF0F1),
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "No schedules for today",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        Text(
            text = "Enjoy your free time!",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}
