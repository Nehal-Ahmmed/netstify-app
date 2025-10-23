package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Link
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ✅ Formatter for kotlinx.datetime.Instant
private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")

private fun formatInstant(instant: Instant): String {
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val javaLdt = LocalDateTime.of(
        ldt.year, ldt.monthNumber, ldt.dayOfMonth,
        ldt.hour, ldt.minute, ldt.second, ldt.nanosecond
    )
    return javaLdt.format(formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkDetailScreen(
    link: Link,
    onBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Link Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔗 Preview Card
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Preview Image (if available)
                    link.previewImageUrl?.let { url ->
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Preview Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Title
                    Text(
                        text = link.title ?: link.url,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Description
                    link.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Domain Chip
                    AssistChip(
                        onClick = { /* Maybe filter by domain later */ },
                        label = { Text(link.domain) }
                    )
                }
            }

            // 🔗 URL Row with actions
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = link.url,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uriHandler.openUri(link.url) }
                    )

                    Row {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(link.url))
                        }) {
                            Icon(painter = painterResource(R.drawable.baseline_content_copy_24), contentDescription = "Copy")
                        }
                        IconButton(onClick = { uriHandler.openUri(link.url) }) {
                            Icon(painter = painterResource(R.drawable.baseline_open_in_browser_24), contentDescription = "Open in Browser")
                        }
                    }
                }
            }

            // 📅 Timestamps
            Text(
                text = "Created: ${formatInstant(link.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Updated: ${formatInstant(link.updatedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
