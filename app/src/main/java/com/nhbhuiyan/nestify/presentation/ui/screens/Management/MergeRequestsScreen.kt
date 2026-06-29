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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun MergeRequestsScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val c = NestifyTheme.colors
    val pendingRequests by viewModel.pendingMergeRequests.collectAsState()
    val status by viewModel.managementStatus.collectAsState()

    LaunchedEffect(status) {
        when (status) {
            is ResultStatus.Success -> {
                Toast.makeText(context, "Merge request resolved!", Toast.LENGTH_SHORT).show()
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
        NestifyAppBar(title = "Merge Requests", onBack = { navController.popBackStack() })

        val filteredList = pendingRequests.filter { it.status == "pending" }

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
                SectionHead(title = "Pending submissions", kicker = "Moderation")
                Spacer(Modifier.height(Space.s))
                Text(
                    "Review and merge syllabus topics, subject configurations, or PYQ materials proposed by students.",
                    style = NestifyTheme.type.body,
                    color = c.ink50,
                )
                Spacer(Modifier.height(Space.xs))
            }

            if (filteredList.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.MergeType,
                        title = "All caught up",
                        description = "No pending merge requests found.",
                    )
                }
            } else {
                items(filteredList) { mr ->
                    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Chip(label = mr.type.uppercase(), tone = ChipTone.Soft)
                                Text(
                                    "By ${mr.submitterName}",
                                    style = NestifyTheme.type.meta,
                                    color = c.ink50,
                                )
                            }

                            Spacer(Modifier.height(Space.m))

                            Text(
                                "Target: ${mr.target}",
                                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                                color = c.ink,
                            )

                            if (mr.data.isNotEmpty()) {
                                Spacer(Modifier.height(Space.xs))
                                mr.data.forEach { (key, value) ->
                                    Text(
                                        "$key: $value",
                                        style = NestifyTheme.type.body,
                                        color = c.ink70,
                                    )
                                }
                            }

                            Spacer(Modifier.height(Space.l))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Space.s),
                            ) {
                                NButton(
                                    label = "Reject",
                                    onClick = {
                                        viewModel.resolveMergeRequest(mr.id, "rejected", "Rejected by Representative")
                                    },
                                    modifier = Modifier.weight(1f),
                                    variant = BtnVariant.Danger,
                                    size = BtnSize.Sm,
                                    leadingIcon = Icons.Default.Close,
                                )
                                NButton(
                                    label = "Approve",
                                    onClick = {
                                        viewModel.resolveMergeRequest(mr.id, "accepted", "Approved and merged")
                                    },
                                    modifier = Modifier.weight(1f),
                                    variant = BtnVariant.Primary,
                                    size = BtnSize.Sm,
                                    leadingIcon = Icons.Default.Check,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
