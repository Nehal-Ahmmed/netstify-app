package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Note
import kotlinx.datetime.TimeZone
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteDetailedScreen(
    note: Note,
    onBack: () -> Unit,
    onArchiveToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")
    fun formatKtxInstant(i: kotlinx.datetime.Instant): String {
        val millis = i.toEpochMilliseconds() // kotlinx -> epoch ms
        val jInstant = java.time.Instant.ofEpochMilli(millis) // java.time.Instant
        val zoned = java.time.ZonedDateTime.ofInstant(jInstant, java.time.ZoneId.systemDefault())
        return zoned.format(formatter)
    }

    var expanded by remember { mutableStateOf(true) }
    // Animate rotation for expand/collapse icon
    val rotation by animateFloatAsState(if (expanded) 270f else 90f, label = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Note Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onArchiveToggle(!note.isArchived) }
            ) {
                Icon(
                    painter = if (note.isArchived) painterResource(R.drawable.baseline_unarchive_24) else painterResource(R.drawable.baseline_archive_24)  ,
                    contentDescription = if (note.isArchived) "Unarchive" else "Archive"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Tags with colors
            if (note.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val palette = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                    note.tags.forEach { tag ->
                            val idx = (tag.hashCode().absoluteValue) % palette.size
                            AssistChip(
                                onClick = { /* TODO: filter by tag */ },
                                label = { Text(tag) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = palette[idx])
                            )
                    }
                }
            }

            // Expandable content card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Content",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Expand/Collapse",
                                modifier = Modifier.rotate(rotation)
                            )
                        }
                    }

                    AnimatedVisibility(visible = expanded) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata
            // Metadata (using safe formatter above)
            Text(
                text = "Created: ${formatKtxInstant(note.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Updated: ${formatKtxInstant(note.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
    }
}
