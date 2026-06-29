package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
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

@Composable
fun LinkDetailScreen(
    link: Link,
    isBookmarked : (Boolean) -> Unit,
    onBack: () -> Unit,
    navController: NavController,
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
        NestifyAppBar(
            title = "Link Details",
            subtitle = null,
            onBack = onBack,
            trailing = {
                IconButtonChrome(
                    if (link.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    onClick = { isBookmarked(!link.isBookmarked) },
                    tint = if (link.isBookmarked) c.brand else c.ink50,
                    contentDescription = "Bookmark",
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screen)
                .padding(top = Space.l, bottom = Space.xxxl),
            verticalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            // 🔗 Preview Card
            NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    // Preview Image (if available)
                    link.previewImageUrl?.let { url ->
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Preview Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(Radii.m),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    // Title
                    Text(
                        text = link.title ?: link.url,
                        style = NestifyTheme.type.h2Serif,
                        color = c.ink,
                    )

                    // Description
                    link.description?.let {
                        Text(it, style = NestifyTheme.type.body, color = c.ink70)
                    }

                    // Domain Chip
                    Chip(label = link.domain, tone = ChipTone.Soft)
                }
            }

            // 🔗 URL Row with actions
            NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.l) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = link.url,
                        style = NestifyTheme.type.body,
                        color = c.ink70,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uriHandler.openUri(link.url) },
                    )
                    PainterIconButton(
                        onClick = { clipboardManager.setText(AnnotatedString(link.url)) },
                        iconPainterRes = R.drawable.baseline_content_copy_24,
                        tint = c.ink50,
                        contentDescription = "Copy",
                    )
                    PainterIconButton(
                        onClick = { uriHandler.openUri(link.url) },
                        iconPainterRes = R.drawable.baseline_open_in_browser_24,
                        tint = c.ink50,
                        contentDescription = "Open in Browser",
                    )
                }
            }

            // 📅 Timestamps
            Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                Kicker("Created · ${formatInstant(link.createdAt)}")
                Kicker("Updated · ${formatInstant(link.updatedAt)}")
            }
        }
    }
}

/** Local icon button for vector-drawable painters (Chrome's IconButtonChrome takes ImageVector). */
@Composable
private fun PainterIconButton(
    onClick: () -> Unit,
    iconPainterRes: Int,
    tint: Color,
    contentDescription: String?,
) {
    Box(
        Modifier
            .size(38.dp)
            .clip(Radii.s)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(iconPainterRes),
            contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}
