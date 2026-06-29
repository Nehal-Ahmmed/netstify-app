package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
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
private fun fileTypeIcon(fileType: String): ImageVector = when (fileType.lowercase()) {
    "pdf" -> Icons.Outlined.PictureAsPdf
    "jpg", "jpeg", "png", "gif", "webp", "image" -> Icons.Outlined.Image
    "mp4", "mkv", "mov", "avi", "video" -> Icons.Outlined.VideoFile
    "doc", "docx", "txt", "document" -> Icons.Outlined.Description
    else -> Icons.AutoMirrored.Outlined.InsertDriveFile
}

@Composable
fun FileDetailScreen(
    file: File,
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val c = NestifyTheme.colors

    Column(
        modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(title = "File Details", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screen)
                .padding(top = Space.l, bottom = Space.xxxl),
            verticalArrangement = Arrangement.spacedBy(Space.l)
        ) {
            // 📂 File Card
            NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.xl) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.m),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(Radii.l)
                            .background(c.brandSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = fileTypeIcon(file.fileType),
                            contentDescription = file.fileType,
                            modifier = Modifier.size(36.dp),
                            tint = c.brand
                        )
                    }

                    Text(
                        text = file.fileName,
                        style = NestifyTheme.type.h2Serif,
                        color = c.ink,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "${file.fileType.uppercase().ifBlank { "FILE" }} • ${file.mimeType}",
                        style = NestifyTheme.type.body,
                        color = c.ink70,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Size: ${file.fileSize / 1024} KB",
                        style = NestifyTheme.type.label,
                        color = c.ink50,
                    )
                }
            }

            // 🔗 Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.m)
            ) {
                NButton(
                    label = "Copy name",
                    onClick = { clipboardManager.setText(AnnotatedString(file.fileName)) },
                    variant = BtnVariant.Secondary,
                    leadingIcon = Icons.Outlined.ContentCopy,
                    full = true,
                    modifier = Modifier.weight(1f),
                )
                NButton(
                    label = "Open",
                    onClick = { uriHandler.openUri(file.uri) },
                    leadingIcon = Icons.Outlined.OpenInNew,
                    full = true,
                    modifier = Modifier.weight(1f),
                )
            }

            // 📅 Timestamps
            NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                    MetaRow(label = "Created", value = formatInstant(file.createdAt))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair2))
                    MetaRow(label = "Updated", value = formatInstant(file.updatedAt))
                }
            }
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val c = NestifyTheme.colors
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = NestifyTheme.type.label, color = c.ink50)
        Spacer(Modifier.size(Space.m))
        Text(value, style = NestifyTheme.type.label, color = c.ink)
    }
}
