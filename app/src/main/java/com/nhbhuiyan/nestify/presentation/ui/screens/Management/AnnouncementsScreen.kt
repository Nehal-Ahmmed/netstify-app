package com.nhbhuiyan.nestify.presentation.ui.screens.Management

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun AnnouncementsScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val c = NestifyTheme.colors
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Low") }

    val status by viewModel.managementStatus.collectAsState()

    LaunchedEffect(status) {
        when (status) {
            is ResultStatus.Success -> {
                Toast.makeText(context, "Announcement posted successfully!", Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
                navController.popBackStack()
            }
            is ResultStatus.Error -> {
                Toast.makeText(context, (status as ResultStatus.Error).message, Toast.LENGTH_LONG).show()
                viewModel.clearStatus()
            }
            else -> {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(title = "Post Announcement", onBack = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Space.screen)
                .padding(top = Space.l),
            verticalArrangement = Arrangement.spacedBy(Space.l),
        ) {
            NestifyInput(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                placeholder = "Announcement headline",
                modifier = Modifier.fillMaxWidth(),
            )

            // Multi-line body editor (BrainSton tokened text area).
            Column {
                Text(
                    "Announcement Body",
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                    color = c.ink70,
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                        .clip(Radii.m)
                        .background(c.surface)
                        .border(1.5.dp, c.hair, Radii.m)
                        .padding(14.dp),
                ) {
                    if (body.isEmpty()) {
                        Text("Write the broadcast message…", style = NestifyTheme.type.body, color = c.ink50)
                    }
                    BasicTextField(
                        value = body,
                        onValueChange = { body = it },
                        textStyle = NestifyTheme.type.body.copy(color = c.ink),
                        cursorBrush = SolidColor(c.brand),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Priority selector — Coral=High, Warn=Medium, Ok=Low.
            Column {
                Text(
                    "Priority",
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                    color = c.ink70,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s)) {
                    listOf(
                        "Low" to ChipTone.Ok,
                        "Medium" to ChipTone.Warn,
                        "High" to ChipTone.Coral,
                    ).forEach { (pr, tone) ->
                        Chip(
                            label = pr,
                            tone = if (priority == pr) tone else ChipTone.Default,
                            onClick = { priority = pr },
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            if (status is ResultStatus.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = c.brand,
                )
            } else {
                NButton(
                    label = "Post Broadcast",
                    onClick = {
                        if (title.isBlank() || body.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@NButton
                        }
                        viewModel.postAnnouncement(title, body, priority) {}
                    },
                    size = BtnSize.Lg,
                    full = true,
                )
            }
            Spacer(Modifier.height(Space.l))
        }
    }
}
