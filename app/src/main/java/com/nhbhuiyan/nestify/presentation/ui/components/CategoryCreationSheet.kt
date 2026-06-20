package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.ScheduleCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCreationSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSaveCategory: (ScheduleCategory) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#FF7E7E") } // Default color

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Create New Category")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name (e.g. Study, Job Prep)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        val category = ScheduleCategory(
                            name = categoryName,
                            colorHex = colorHex
                        )
                        onSaveCategory(category)
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Category")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
