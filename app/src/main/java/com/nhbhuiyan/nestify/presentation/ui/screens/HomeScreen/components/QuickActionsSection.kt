package com.nhbhuiyan.nestify.presentation.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.QuickAction

/**
 * Quick Actions Section - Productivity shortcuts
 */
@Composable
fun QuickActionsSection(
    quickActions: List<QuickAction>,
    onQuickActionClicked: (QuickAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Title
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Horizontal Quick Actions List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickActions) { action ->
                QuickActionItem(
                    quickAction = action,
                    onClick = { onQuickActionClicked(action) }
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    quickAction: QuickAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = quickAction.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Action Icon
            Text(
                text = quickAction.icon,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp
            )

            // Action Details
            Column {
                Text(
                    text = quickAction.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = quickAction.color
                )

                Text(
                    text = quickAction.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}