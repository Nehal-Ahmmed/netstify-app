package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.LinkFolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLinkDialog(
    folders: List<LinkFolder>,
    onDismiss: () -> Unit,
    onCreateLink: (String, String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var isUrlValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save New Link") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        isUrlValid = it.isNotEmpty() && (it.startsWith("http") || it.contains("."))
                    },
                    label = { Text("URL *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Link, null) }
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(16.dp))
                Text("Select Folder", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FolderMiniPill(
                            name = "None",
                            isSelected = selectedFolderId == null,
                            onClick = { selectedFolderId = null }
                        )
                    }
                    items(folders) { folder ->
                        FolderMiniPill(
                            name = folder.name,
                            isSelected = selectedFolderId == folder.id,
                            onClick = { selectedFolderId = folder.id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreateLink(title, description, url, selectedFolderId) },
                enabled = url.isNotBlank() && isUrlValid
            ) {
                Text("Save Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FolderMiniPill(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(
                name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}