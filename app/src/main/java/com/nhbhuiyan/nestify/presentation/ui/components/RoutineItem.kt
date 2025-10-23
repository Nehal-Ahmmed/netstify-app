package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.ClassRoutine

@Composable
fun RoutineItem(routine: ClassRoutine, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = routine.content,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = routine.id.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = routine.imageDescription.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = routine.imageUri.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}