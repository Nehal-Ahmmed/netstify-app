package com.nhbhuiyan.nestify.presentation.ui.components.brainston

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

/**
 * BrainSton foundation composables (ported from system.jsx primitives).
 * Everything reads from [NestifyTheme] tokens — no hard-coded colors/sizes.
 */

/** Mono uppercase eyebrow label. */
@Composable
fun Kicker(text: String, modifier: Modifier = Modifier, color: Color = NestifyTheme.colors.ink50) {
    Text(text.uppercase(), modifier = modifier, style = NestifyTheme.type.kicker, color = color)
}

/** Section header: optional mono kicker + serif title + optional trailing action. */
@Composable
fun SectionHead(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            if (kicker != null) {
                Kicker(kicker)
                Spacer(Modifier.height(4.dp))
            }
            Text(title, style = NestifyTheme.type.h3Serif, color = NestifyTheme.colors.ink)
        }
        if (actionText != null) {
            Text(
                actionText,
                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                color = NestifyTheme.colors.brand,
                modifier = if (onAction != null) Modifier.clickable(onClick = onAction) else Modifier,
            )
        }
    }
}

/** Base surface card: white surface, hairline border, large radius. */
@Composable
fun NestifyCard(
    modifier: Modifier = Modifier,
    padding: androidx.compose.ui.unit.Dp = Space.l,
    background: Color = NestifyTheme.colors.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base = modifier
        .clip(Radii.l)
        .background(background)
        .border(1.dp, NestifyTheme.colors.hair2, Radii.l)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(padding)
    Box(base) { content() }
}

/** Numbered/iconed soft square (used in module lists, settings rows). */
@Composable
fun IconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    background: Color = NestifyTheme.colors.brandSoft,
    tint: Color = NestifyTheme.colors.brand,
    corner: androidx.compose.ui.unit.Dp = 12.dp,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

/** A row of stat values separated by vertical hairline dividers. */
@Composable
fun StatRow(
    stats: List<Pair<String, String>>, // value to label
    modifier: Modifier = Modifier,
    valueColor: Color = NestifyTheme.colors.ink,
    labelColor: Color = NestifyTheme.colors.ink50,
    dividerColor: Color = NestifyTheme.colors.hair,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        stats.forEachIndexed { i, (value, label) ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, style = NestifyTheme.type.h3Serif, color = valueColor)
                Spacer(Modifier.height(2.dp))
                Kicker(label, color = labelColor)
            }
            if (i != stats.lastIndex) {
                Box(
                    Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(dividerColor)
                )
            }
        }
    }
}

/** Numbered soft square — used for ordered topic / module lists (BrainSton module rows). */
@Composable
fun NumberTile(
    number: Int,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    background: Color = NestifyTheme.colors.brandSoft,
    tint: Color = NestifyTheme.colors.brand,
    corner: androidx.compose.ui.unit.Dp = 12.dp,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            number.toString(),
            style = NestifyTheme.type.h3Serif,
            color = tint,
        )
    }
}

/** Thin progress bar (pill track + fill). */
@Composable
fun ProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 6.dp,
    color: Color = NestifyTheme.colors.brand,
    track: Color = NestifyTheme.colors.ink10,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(99.dp))
            .background(track)
    ) {
        Box(
            Modifier
                .fillMaxWidth(value.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(99.dp))
                .background(color)
        )
    }
}

/** Truncating single-line text helper used across list rows. */
@Composable
fun OneLine(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(text, modifier = modifier, style = style, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
}
