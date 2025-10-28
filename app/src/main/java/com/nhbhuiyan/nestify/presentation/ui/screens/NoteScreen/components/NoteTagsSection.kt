package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * Tags Section for adding tags to notes
 */
@Composable
fun NoteTagsSection(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTag by remember { mutableStateOf(TextFieldValue()) }
    var isAddingTag by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Section Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NewLabel,
                contentDescription = "Tags",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Tags List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Existing Tags
            items(tags) { tag ->
                TagChip(
                    tag = tag,
                    onRemove = {
                        onTagsChange(tags - tag)
                    }
                )
            }

            // Add Tag Input
            item {
                if (isAddingTag) {
                    AddTagTextField(
                        value = newTag,
                        onValueChange = { newTag = it },
                        onAdd = {
                            if (newTag.text.isNotBlank() && newTag.text !in tags) {
                                onTagsChange(tags + newTag.text)
                            }
                            newTag = TextFieldValue()
                            isAddingTag = false
                        },
                        onCancel = {
                            newTag = TextFieldValue()
                            isAddingTag = false
                        }
                    )
                } else {
                    AddTagButton(
                        onClick = { isAddingTag = true }
                    )
                }
            }
        }
    }
}

@Composable
fun TagChip(
    tag: String,
    onRemove: () -> Unit
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = tag,
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag",
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun AddTagButton(
    onClick: () -> Unit
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = "Add Tag",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add tag",
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
fun AddTagTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(32.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (hasFocus) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.hasFocus
                    if (!focusState.hasFocus && value.text.isBlank()) {
                        onCancel()
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = "New tag...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}