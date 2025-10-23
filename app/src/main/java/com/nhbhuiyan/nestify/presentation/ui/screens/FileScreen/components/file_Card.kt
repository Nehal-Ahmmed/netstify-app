package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.File

@Composable
fun fileItem(file: File,onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = file.fileName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = file.fileType,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = file.mimeType,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = file.fileSize.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = file.createdAt.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = file.updatedAt.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = file.isArchived.toString(),
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}