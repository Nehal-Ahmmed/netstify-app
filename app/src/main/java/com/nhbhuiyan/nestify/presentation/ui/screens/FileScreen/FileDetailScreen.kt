package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.File
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

// ✅ Get icon based on file type
@Composable
private fun fileTypeIcon(fileType: String) = when (fileType.lowercase()) {
    "image" -> painterResource(R.drawable.ic_home)
    "video" -> painterResource(R.drawable.baseline_open_in_browser_24)
    "pdf" -> painterResource(R.drawable.ic_home)
    else -> painterResource(R.drawable.ic_home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailScreen(
    file: File,
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Details") },
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
            // 📂 File Card
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // File Icon
                    Icon(
                        painter = fileTypeIcon(file.fileType),
                        contentDescription = file.fileType,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    // File Name
                    Text(
                        text = file.fileName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // File Details
                    Text(
                        text = "Type: ${file.fileType.uppercase()} • ${file.mimeType}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Size: ${file.fileSize / 1024} KB",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 🔗 Actions Row
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(file.fileName))
                    }) {
                        Icon(painter = painterResource(R.drawable.baseline_content_copy_24), contentDescription = "Copy")
                    }

                    IconButton(onClick = {
                        uriHandler.openUri(file.uri) // may need SAF or Intent for real file access
                    }) {
                        Icon(painter = painterResource(R.drawable.baseline_open_in_browser_24), contentDescription = "Open File")
                    }
                }
            }

            // 📅 Timestamps
            Text(
                text = "Created: ${formatInstant(file.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Updated: ${formatInstant(file.updatedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
