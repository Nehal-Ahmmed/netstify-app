package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ScrollableTabPill
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

/**
 * Academics hub (BrainSton "courses catalog" archetype). A floating [ScrollableTabPill]
 * swaps which existing tab composable renders; all ViewModel state / actions are unchanged.
 */
@Composable
fun ExamPlannerScreen(
    navController: NavController,
    viewModel: ExamPlannerViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors

    val defaultLevel by viewModel.defaultLevel.collectAsState()
    val defaultTerm by viewModel.defaultTerm.collectAsState()

    var currentTab by remember { mutableIntStateOf(0) }
    var showEditDefaultDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Subjects", "Planner", "CT Marks", "Results", "CGPA", "Sync")

    if (showEditDefaultDialog) {
        var tempLevel by remember { mutableStateOf(defaultLevel.toString()) }
        var tempTerm by remember { mutableStateOf(defaultTerm.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDefaultDialog = false },
            title = { Text("Set Default Workspace") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempLevel,
                        onValueChange = { tempLevel = it },
                        label = { Text("Level") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempTerm,
                        onValueChange = { tempTerm = it },
                        label = { Text("Term") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val lvl = tempLevel.trim().toIntOrNull() ?: defaultLevel
                    val trm = tempTerm.trim().toIntOrNull() ?: defaultTerm
                    viewModel.setDefaultLevel(lvl)
                    viewModel.setDefaultTerm(trm)
                    showEditDefaultDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDefaultDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space.screen, vertical = Space.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Academics",
                style = NestifyTheme.type.h2Serif,
                color = c.ink
            )
            Chip(
                "L$defaultLevel T$defaultTerm",
                tone = ChipTone.Soft,
                onClick = { showEditDefaultDialog = true },
            )
        }

        Column(Modifier.padding(horizontal = Space.screen)) {
            Spacer(Modifier.height(Space.m))
            ScrollableTabPill(tabs = tabs, active = currentTab, onChange = { currentTab = it })
            Spacer(Modifier.height(Space.s))
        }

        Box(Modifier.weight(1f)) {
            when (currentTab) {
                0 -> SubjectsDetailsTab(viewModel, defaultLevel, defaultTerm)
                1 -> ExamPlanTab(navController, viewModel, defaultLevel, defaultTerm)
                2 -> ClassTestMarksTab(viewModel, defaultLevel, defaultTerm)
                3 -> ExamResultsTab(viewModel, defaultLevel, defaultTerm)
                4 -> CGPADashboardTab(viewModel)
                5 -> PackagingSyncTab(viewModel, defaultLevel, defaultTerm)
            }
        }
    }
}
