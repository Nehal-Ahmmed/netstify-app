package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyScaffold
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteDetailedScreen(
    note: Note,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onBookmarkToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = NestifyTheme.colors
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")
    fun formatKtxInstant(i: kotlinx.datetime.Instant): String {
        val millis = i.toEpochMilliseconds() // kotlinx -> epoch ms
        val jInstant = java.time.Instant.ofEpochMilli(millis) // java.time.Instant
        val zoned = java.time.ZonedDateTime.ofInstant(jInstant, java.time.ZoneId.systemDefault())
        return zoned.format(formatter)
    }

    var expanded by remember { mutableStateOf(true) }
    // Animate rotation for expand/collapse icon
    val rotation by animateFloatAsState(if (expanded) 90f else 0f, label = "")

    val tones = listOf(ChipTone.Soft, ChipTone.Brand, ChipTone.Coral, ChipTone.Ok, ChipTone.Warn)

    NestifyScaffold(
        modifier = modifier,
        appBar = {
            NestifyAppBar(
                title = "Note Details",
                onBack = onBack,
                trailing = {
                    IconButtonChrome(
                        if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        onClick = { onBookmarkToggle(!note.isBookmarked) },
                        tint = if (note.isBookmarked) c.brand else c.ink,
                        contentDescription = "Bookmark",
                    )
                },
            )
        },
    ) {
        Spacer(Modifier.height(Space.l))

        // Title
        Text(
            text = note.title,
            style = NestifyTheme.type.h1Serif,
            color = c.ink,
        )

        // Tags
        if (note.tags.isNotEmpty()) {
            Spacer(Modifier.height(Space.m))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Space.s),
                verticalArrangement = Arrangement.spacedBy(Space.s),
            ) {
                note.tags.forEach { tag ->
                    val idx = (tag.hashCode().absoluteValue) % tones.size
                    Chip(label = tag, tone = tones[idx])
                }
            }
        }

        Spacer(Modifier.height(Space.l))

        // Expandable content card
        NestifyCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Kicker("Content")
                    IconButtonChrome(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        onClick = { expanded = !expanded },
                        tint = c.ink50,
                        modifier = Modifier.rotate(rotation),
                        contentDescription = "Expand/Collapse",
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Text(
                        text = note.content,
                        style = NestifyTheme.type.body,
                        color = c.ink70,
                        modifier = Modifier.padding(top = Space.s),
                    )
                }
            }
        }

        Spacer(Modifier.height(Space.l))

        // Metadata
        NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
            Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                MetaRow("Created", formatKtxInstant(note.createdAt))
                MetaRow("Updated", formatKtxInstant(note.updatedAt))
            }
        }

        Spacer(Modifier.height(Space.l))

        // Edit affordance (replaces floating FAB)
        NButton(
            label = "Edit note",
            onClick = { onEditClick(note.id) },
            leadingIcon = Icons.Default.Edit,
            full = true,
            size = BtnSize.Lg,
        )
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val c = NestifyTheme.colors
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Kicker(label)
        Text(value, style = NestifyTheme.type.meta, color = c.ink70)
    }
}
