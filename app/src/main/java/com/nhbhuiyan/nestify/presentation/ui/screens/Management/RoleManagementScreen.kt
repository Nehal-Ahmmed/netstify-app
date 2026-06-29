package com.nhbhuiyan.nestify.presentation.ui.screens.Management

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Avatar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun RoleManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val c = NestifyTheme.colors
    val rosterList by viewModel.roster.collectAsState()
    val status by viewModel.managementStatus.collectAsState()

    LaunchedEffect(status) {
        when (status) {
            is ResultStatus.Success -> {
                Toast.makeText(context, "Role updated successfully!", Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
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
        NestifyAppBar(title = "Role Management", onBack = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.l,
                bottom = Space.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            item {
                SectionHead(title = "Class roster", kicker = "Promotions")
                Spacer(Modifier.height(Space.s))
                Text(
                    "Promote classmates to Class Representatives (CR) to let them manage courses, marks, and announcements.",
                    style = NestifyTheme.type.body,
                    color = c.ink50,
                )
                Spacer(Modifier.height(Space.xs))
            }

            if (rosterList.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Group,
                        title = "No students yet",
                        description = "No students are currently registered in the roster.",
                    )
                }
            } else {
                items(rosterList) { student ->
                    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.m),
                        ) {
                            Avatar(name = student.displayName)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    student.displayName,
                                    style = NestifyTheme.type.label.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = c.ink,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "Roll: ${student.rollNumber}",
                                    style = NestifyTheme.type.meta,
                                    color = c.ink50,
                                )
                            }

                            NButton(
                                label = "Make CR",
                                onClick = { viewModel.setUserRole(student.uid, UserRole.CR) },
                                variant = BtnVariant.Primary,
                                size = BtnSize.Sm,
                                leadingIcon = Icons.Default.Star,
                            )
                        }
                    }
                }
            }
        }
    }
}
