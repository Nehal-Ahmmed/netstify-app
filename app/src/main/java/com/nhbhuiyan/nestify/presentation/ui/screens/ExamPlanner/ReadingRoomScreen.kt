package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nhbhuiyan.nestify.data.local.entity.PYQEntity
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import com.nhbhuiyan.nestify.utils.ImageUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingRoomScreen(
    topicId: Long,
    viewModel: ExamPlannerViewModel,
    onBack: () -> Unit,
) {
    val c = NestifyTheme.colors
    val context = LocalContext.current
    val pyqsMap by viewModel.pyqs.collectAsState()
    val pyqs = pyqsMap[topicId] ?: emptyList()
    val syllabusTopicsMap by viewModel.syllabusTopics.collectAsState()
    val topicName = syllabusTopicsMap.values.flatten().find { it.id == topicId }?.title ?: "Unknown Topic"

    var showAddSheet by remember { mutableStateOf(false) }
    var editingPyq by remember { mutableStateOf<PYQEntity?>(null) }
    var showSolutions by remember { mutableStateOf(false) }

    var timeRemaining by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timeRemaining) {
        if (isTimerRunning && timeRemaining > 0) {
            kotlinx.coroutines.delay(1000L)
            timeRemaining -= 1
        } else if (timeRemaining == 0L) {
            isTimerRunning = false
        }
    }

    LaunchedEffect(topicId) { viewModel.loadPYQsForTopic(topicId) }

    Scaffold(
        containerColor = c.canvas,
        topBar = {
            NestifyAppBar(
                title = "Reading Room",
                subtitle = topicName,
                onBack = onBack,
                trailing = {
                    val h = timeRemaining / 3600
                    val m = (timeRemaining % 3600) / 60
                    val s = timeRemaining % 60
                    val timeStr = if (h > 0) String.format("%02d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
                    Chip(timeStr, tone = ChipTone.Soft, leadingIcon = Icons.Default.Timer, onClick = { showTimerDialog = true })
                },
            )
        },
        floatingActionButton = {
            Box(
                Modifier
                    .padding(bottom = 96.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(NestifyGradients.brandFab())
                    .clickable { showAddSheet = true },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Default.Add, contentDescription = "Add PYQ", tint = Color.White) }
        },
    ) { padding ->
        if (showTimerDialog) {
            var hours by remember { mutableStateOf("") }
            var mins by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showTimerDialog = false },
                title = { Text("Set study timer") },
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Hours") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = mins, onValueChange = { mins = it }, label = { Text("Minutes") }, modifier = Modifier.weight(1f))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val h = hours.toLongOrNull() ?: 0L
                        val mm = mins.toLongOrNull() ?: 0L
                        timeRemaining = h * 3600 + mm * 60
                        isTimerRunning = timeRemaining > 0
                        showTimerDialog = false
                    }) { Text("Start") }
                },
                dismissButton = { TextButton(onClick = { showTimerDialog = false }) { Text("Cancel") } },
            )
        }

        if (pyqs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Outlined.HelpOutline,
                    title = "No questions yet",
                    description = "Add previous-year questions to build this topic's question bank.",
                )
            }
        } else {
            var selectedQuestionForDialog by remember { mutableStateOf<PYQEntity?>(null) }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.m, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(Space.m),
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Space.s)) {
                        Chip("Questions", tone = if (!showSolutions) ChipTone.Brand else ChipTone.Default, active = !showSolutions, onClick = { showSolutions = false })
                        Chip("Solutions", tone = if (showSolutions) ChipTone.Brand else ChipTone.Default, active = showSolutions, onClick = { showSolutions = true })
                    }
                }
                item { SectionHead(title = topicName, kicker = "Question bank") }
                item {
                    val sortedPyqs = pyqs.sortedByDescending { it.repeatCount }
                    PYQTable(pyqs = sortedPyqs, onQuestionClick = { selectedQuestionForDialog = it })
                }
                items(pyqs) { pyq ->
                    PYQCard(
                        pyq = pyq,
                        expandedDefault = showSolutions,
                        onEdit = { editingPyq = pyq; showAddSheet = true },
                        onDelete = { viewModel.deletePYQ(pyq) },
                    )
                }
            }

            if (selectedQuestionForDialog != null) {
                QuestionDetailsDialog(pyq = selectedQuestionForDialog!!, onDismiss = { selectedQuestionForDialog = null })
            }
        }

        if (showAddSheet) {
            AddEditPYQSheet(
                topicId = topicId,
                initialPyq = editingPyq,
                onDismiss = { showAddSheet = false; editingPyq = null },
                onSave = { entity ->
                    if (editingPyq != null) viewModel.updatePYQ(entity.copy(id = editingPyq!!.id))
                    else viewModel.insertPYQ(entity)
                    showAddSheet = false; editingPyq = null
                },
            )
        }
    }
}

@Composable
fun PYQCard(pyq: PYQEntity, expandedDefault: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    val c = NestifyTheme.colors
    var expanded by remember(expandedDefault) { mutableStateOf(expandedDefault) }

    NestifyCard(Modifier.fillMaxWidth(), onClick = { expanded = !expanded }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                val repeatTone = when {
                    pyq.repeatCount > 3 -> ChipTone.Coral
                    pyq.repeatCount > 1 -> ChipTone.Warn
                    else -> ChipTone.Ok
                }
                IconTile(Icons.Outlined.HelpOutline, size = 36.dp)
                Column(Modifier.weight(1f)) {
                    if (pyq.yearsSeen.isNotBlank()) Kicker("Years ${pyq.yearsSeen}")
                    Chip("${pyq.repeatCount}× repeated", tone = repeatTone)
                }
                IconButtonChrome(Icons.Default.EditNote, onClick = onEdit, tint = c.ink50, contentDescription = "Edit")
                IconButtonChrome(Icons.Default.DeleteOutline, onClick = onDelete, tint = c.coral, contentDescription = "Delete")
            }

            Spacer(Modifier.height(Space.m))
            Text("Q", style = NestifyTheme.type.kicker, color = c.brand)
            if (!pyq.questionText.isNullOrBlank()) {
                Text(
                    pyq.questionText,
                    style = NestifyTheme.type.body,
                    color = c.ink,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            } else if (!pyq.questionImagePath.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Image, null, tint = c.ink50, modifier = Modifier.size(16.dp))
                    Text("Image question", style = NestifyTheme.type.body, color = c.ink50)
                }
            }
            if (expanded && !pyq.questionImagePath.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = File(pyq.questionImagePath),
                    contentDescription = "Question Image",
                    modifier = Modifier.fillMaxWidth().clip(Radii.m),
                    contentScale = ContentScale.FillWidth,
                )
            }

            if (expanded) {
                Spacer(Modifier.height(Space.l))
                Text("A", style = NestifyTheme.type.kicker, color = c.ok)
                if (!pyq.answerText.isNullOrBlank()) {
                    Text(pyq.answerText, style = NestifyTheme.type.body, color = c.ink70)
                }
                if (!pyq.answerImagePath.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = File(pyq.answerImagePath),
                        contentDescription = "Answer Image",
                        modifier = Modifier.fillMaxWidth().clip(Radii.m),
                        contentScale = ContentScale.FillWidth,
                    )
                }
                val hasNB = !pyq.nbFormulas.isNullOrBlank() || !pyq.nbTheories.isNullOrBlank() || !pyq.nbConstants.isNullOrBlank() || !pyq.nbExtras.isNullOrBlank()
                if (hasNB) {
                    Spacer(Modifier.height(Space.m))
                    Column(Modifier.fillMaxWidth().clip(Radii.m).background(c.surface2).padding(12.dp)) {
                        Text("N.B. / Key points", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                        Spacer(Modifier.height(6.dp))
                        if (!pyq.nbFormulas.isNullOrBlank()) Text("• Formulas: ${pyq.nbFormulas}", style = NestifyTheme.type.body, color = c.ink70)
                        if (!pyq.nbTheories.isNullOrBlank()) Text("• Theories: ${pyq.nbTheories}", style = NestifyTheme.type.body, color = c.ink70)
                        if (!pyq.nbConstants.isNullOrBlank()) Text("• Constants: ${pyq.nbConstants}", style = NestifyTheme.type.body, color = c.ink70)
                        if (!pyq.nbExtras.isNullOrBlank()) Text("• Extras: ${pyq.nbExtras}", style = NestifyTheme.type.body, color = c.ink70)
                    }
                }
            } else {
                Spacer(Modifier.height(Space.s))
                Kicker("Tap to expand answer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPYQSheet(
    topicId: Long,
    initialPyq: PYQEntity? = null,
    onDismiss: () -> Unit,
    onSave: (PYQEntity) -> Unit,
) {
    val context = LocalContext.current

    var questionText by remember { mutableStateOf(initialPyq?.questionText ?: "") }
    var answerText by remember { mutableStateOf(initialPyq?.answerText ?: "") }
    var nbFormulas by remember { mutableStateOf(initialPyq?.nbFormulas ?: "") }
    var nbTheories by remember { mutableStateOf(initialPyq?.nbTheories ?: "") }
    var nbConstants by remember { mutableStateOf(initialPyq?.nbConstants ?: "") }
    var nbExtras by remember { mutableStateOf(initialPyq?.nbExtras ?: "") }
    var repeatCount by remember { mutableStateOf(initialPyq?.repeatCount?.toString() ?: "1") }
    var yearsSeen by remember { mutableStateOf(initialPyq?.yearsSeen ?: "") }
    var marks by remember { mutableStateOf(initialPyq?.marks ?: "") }

    var questionImageUri by remember { mutableStateOf<Uri?>(null) }
    var answerImageUri by remember { mutableStateOf<Uri?>(null) }

    val qImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> questionImageUri = uri }
    val aImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> answerImageUri = uri }

    val c = NestifyTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text(if (initialPyq != null) "Edit question" else "Add new question", style = NestifyTheme.type.h2Serif, color = c.ink) }
            item {
                OutlinedTextField(value = questionText, onValueChange = { questionText = it }, label = { Text("Question text") }, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = { qImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text(if (questionImageUri == null) "Attach question image" else "Question image attached")
                }
            }
            item {
                OutlinedTextField(value = answerText, onValueChange = { answerText = it }, label = { Text("Answer text") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                TextButton(onClick = { aImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text(if (answerImageUri == null) "Attach answer image" else "Answer image attached")
                }
            }
            item {
                Text("Metadata", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = repeatCount, onValueChange = { if (it.all { ch -> ch.isDigit() }) repeatCount = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = yearsSeen, onValueChange = { yearsSeen = it }, label = { Text("Years") }, modifier = Modifier.weight(2f))
                    OutlinedTextField(value = marks, onValueChange = { marks = it }, label = { Text("Marks") }, modifier = Modifier.weight(1f))
                }
            }
            item {
                Text("N.B. (Key points)", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.brand)
                OutlinedTextField(value = nbFormulas, onValueChange = { nbFormulas = it }, label = { Text("Formulas") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(value = nbTheories, onValueChange = { nbTheories = it }, label = { Text("Theories") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(value = nbConstants, onValueChange = { nbConstants = it }, label = { Text("Constants") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(value = nbExtras, onValueChange = { nbExtras = it }, label = { Text("Extras") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
            item {
                Button(
                    onClick = {
                        val qPath = questionImageUri?.let { ImageUtils.copyImageToInternalStorage(context, it) }
                        val aPath = answerImageUri?.let { ImageUtils.copyImageToInternalStorage(context, it) }
                        onSave(
                            PYQEntity(
                                topicId = topicId,
                                questionText = questionText.takeIf { it.isNotBlank() },
                                questionImagePath = qPath,
                                answerText = answerText.takeIf { it.isNotBlank() },
                                answerImagePath = aPath,
                                nbFormulas = nbFormulas.takeIf { it.isNotBlank() },
                                nbTheories = nbTheories.takeIf { it.isNotBlank() },
                                nbConstants = nbConstants.takeIf { it.isNotBlank() },
                                nbExtras = nbExtras.takeIf { it.isNotBlank() },
                                repeatCount = repeatCount.toIntOrNull() ?: 1,
                                yearsSeen = yearsSeen,
                                marks = marks.takeIf { it.isNotBlank() },
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = c.brand),
                ) { Text("Save question", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun PYQTable(pyqs: List<PYQEntity>, onQuestionClick: (PYQEntity) -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(Modifier.fillMaxWidth(), padding = 0.dp) {
        Column {
            Row(
                Modifier.fillMaxWidth().background(c.surface2).padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("No.", modifier = Modifier.weight(0.12f), style = NestifyTheme.type.kicker, color = c.ink70)
                Text("Question", modifier = Modifier.weight(0.4f), style = NestifyTheme.type.kicker, color = c.ink70)
                Text("Marks", modifier = Modifier.weight(0.16f), style = NestifyTheme.type.kicker, color = c.ink70)
                Text("Reps", modifier = Modifier.weight(0.16f), style = NestifyTheme.type.kicker, color = c.ink70)
                Text("Years", modifier = Modifier.weight(0.2f), style = NestifyTheme.type.kicker, color = c.ink70)
            }
            HorizontalDivider(color = c.hair2)
            pyqs.forEachIndexed { index, pyq ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${index + 1}", modifier = Modifier.weight(0.12f), style = NestifyTheme.type.body, color = c.ink70)
                    Box(modifier = Modifier.weight(0.4f).clickable { onQuestionClick(pyq) }.padding(end = 8.dp)) {
                        when {
                            !pyq.questionText.isNullOrBlank() -> Text(pyq.questionText, style = NestifyTheme.type.body, color = c.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            !pyq.questionImagePath.isNullOrBlank() -> Text("Tap to view image", style = NestifyTheme.type.body, color = c.brand)
                            else -> Text("No question", style = NestifyTheme.type.body, color = c.ink50)
                        }
                    }
                    Text(pyq.marks ?: "-", modifier = Modifier.weight(0.16f), style = NestifyTheme.type.body, color = c.ink70)
                    val repeatColor = if (pyq.repeatCount > 3) c.coral else if (pyq.repeatCount > 1) c.warn else c.ok
                    Text("${pyq.repeatCount}×", modifier = Modifier.weight(0.16f), style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = repeatColor)
                    OneLine(pyq.yearsSeen, modifier = Modifier.weight(0.2f), style = NestifyTheme.type.meta, color = c.ink70)
                }
                if (index < pyqs.size - 1) HorizontalDivider(color = c.hair2)
            }
        }
    }
}

@Composable
fun QuestionDetailsDialog(pyq: PYQEntity, onDismiss: () -> Unit) {
    val c = NestifyTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        title = { Text("Question details", style = NestifyTheme.type.h3Serif, color = c.ink) },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (!pyq.questionText.isNullOrBlank()) Text(pyq.questionText, style = NestifyTheme.type.body, color = c.ink)
                if (!pyq.questionImagePath.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    AsyncImage(
                        model = File(pyq.questionImagePath),
                        contentDescription = "Question Image",
                        modifier = Modifier.fillMaxWidth().clip(Radii.m),
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = c.brand) } },
    )
}
