package com.nhbhuiyan.nestify.presentation.ui.components.brainston

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

/** Height the floating glass nav reserves at the bottom of scrollable content. */
val GlassNavSpace = 108.dp

/** A 38dp transparent square icon button (port of chrome.jsx `iconBtn`). */
@Composable
fun IconButtonChrome(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = NestifyTheme.colors.ink,
    contentDescription: String? = null,
) {
    Box(
        modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

/**
 * Top app bar (port of chrome.jsx `AppBar`).
 *
 * - [title] null → brandmark + subtitle "default" variant.
 * - [title] set → centered/left title with optional back button.
 * Status-bar-aware top padding via [WindowInsets.statusBars].
 */
@Composable
fun NestifyAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = "Your campus, organised",
    onBack: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    divider: Boolean = true,
) {
    val c = NestifyTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .background(c.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = Space.l, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (onBack != null) {
                // Tonal back chip — clear affordance, neutral in both themes.
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.surface2)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = c.ink,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            if (title == null) {
                BrandMark(size = 28.dp)
                Column(Modifier.weight(1f)) {
                    Text(
                        "Nestify",
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold),
                        color = c.ink,
                    )
                    if (subtitle != null) {
                        Text(subtitle, style = NestifyTheme.type.meta, color = c.ink50)
                    }
                }
            } else {
                Text(
                    title,
                    style = NestifyTheme.type.h3Serif,
                    color = c.ink,
                    modifier = Modifier.weight(1f),
                )
            }
            if (trailing != null) {
                Row(verticalAlignment = Alignment.CenterVertically, content = trailing)
            }
        }
        if (divider) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.hair))
        }
    }
}

/** Re-export of Compose's RowScope for the [NestifyAppBar] trailing slot. */
typealias RowScope = androidx.compose.foundation.layout.RowScope

/**
 * Standard screen wrapper (port of chrome.jsx scaffolds): canvas background, optional app bar,
 * and a vertically-scrolling content column padded to clear the floating glass nav.
 */
@Composable
fun NestifyScaffold(
    modifier: Modifier = Modifier,
    appBar: @Composable (() -> Unit)? = null,
    horizontalPadding: androidx.compose.ui.unit.Dp = Space.screen,
    scrollable: Boolean = true,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val c = NestifyTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        if (appBar != null) appBar()
        val body: @Composable () -> Unit = {
            Column(
                Modifier
                    .fillMaxSize()
                    .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(horizontal = horizontalPadding),
                content = { content(); Spacer(Modifier.height(GlassNavSpace)) },
            )
        }
        body()
    }
}

/** Bottom spacer that pushes content above the floating nav (port of chrome.jsx `NavSpacer`). */
@Composable
fun NavSpacer(height: androidx.compose.ui.unit.Dp = GlassNavSpace) {
    Spacer(Modifier.height(height))
}

/** Convenience overload exposing scaffold content padding for screens managing their own lists. */
val NestifyScaffoldContentPadding = PaddingValues(bottom = GlassNavSpace)
