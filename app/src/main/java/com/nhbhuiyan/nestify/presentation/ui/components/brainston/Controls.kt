package com.nhbhuiyan.nestify.presentation.ui.components.brainston

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii

// ─────────────────────────────────────────────────────────────────────────────
// Chip
// ─────────────────────────────────────────────────────────────────────────────

enum class ChipTone { Default, Brand, Soft, Coral, Warn, Ok, Ghost }

@Composable
fun Chip(
    label: String,
    modifier: Modifier = Modifier,
    tone: ChipTone = ChipTone.Default,
    active: Boolean = false,
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    val c = NestifyTheme.colors
    val (bg, fg, border) = when (tone) {
        ChipTone.Default -> Triple(if (active) c.ink else c.surface, if (active) Color.White else c.ink70, if (active) c.ink else c.hair)
        ChipTone.Brand -> Triple(c.brand, Color.White, c.brand)
        ChipTone.Soft -> Triple(c.brandSoft, c.brandDeep, Color.Transparent)
        ChipTone.Coral -> Triple(c.coralSoft, c.coral, Color.Transparent)
        ChipTone.Warn -> Triple(c.warnSoft, c.warn, Color.Transparent)
        ChipTone.Ok -> Triple(c.okSoft, c.ok, Color.Transparent)
        ChipTone.Ghost -> Triple(Color.Transparent, c.ink70, c.hair)
    }
    Row(
        modifier
            .clip(Radii.pill)
            .background(bg)
            .border(1.dp, border, Radii.pill)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (leadingIcon != null) Icon(leadingIcon, null, tint = fg, modifier = Modifier.size(14.dp))
        Text(label, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium), color = fg)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Button
// ─────────────────────────────────────────────────────────────────────────────

enum class BtnVariant { Primary, Secondary, Ghost, Soft, Danger, Dark }
enum class BtnSize { Sm, Md, Lg }

@Composable
fun NButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BtnVariant = BtnVariant.Primary,
    size: BtnSize = BtnSize.Md,
    full: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    gradient: Brush? = null,
) {
    val c = NestifyTheme.colors
    val (bg, fg, border) = when (variant) {
        BtnVariant.Primary -> Triple(c.brand, Color.White, Color.Transparent)
        BtnVariant.Secondary -> Triple(c.surface, c.ink, c.hair)
        BtnVariant.Ghost -> Triple(Color.Transparent, c.ink, Color.Transparent)
        BtnVariant.Soft -> Triple(c.brandSoft, c.brandDeep, Color.Transparent)
        BtnVariant.Danger -> Triple(c.coral, Color.White, Color.Transparent)
        BtnVariant.Dark -> Triple(c.ink, Color.White, Color.Transparent)
    }
    val height = when (size) { BtnSize.Sm -> 32.dp; BtnSize.Md -> 42.dp; BtnSize.Lg -> 52.dp }
    val hPad = when (size) { BtnSize.Sm -> 12.dp; BtnSize.Md -> 16.dp; BtnSize.Lg -> 20.dp }
    val fontSize = when (size) { BtnSize.Sm -> 13; BtnSize.Md -> 14; BtnSize.Lg -> 15 }

    Row(
        modifier
            .let { if (full) it.fillMaxWidth() else it }
            .height(height)
            .clip(Radii.pill)
            .let { if (gradient != null) it.background(gradient) else it.background(bg) }
            .border(1.dp, border, Radii.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = hPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        if (leadingIcon != null) Icon(leadingIcon, null, tint = fg, modifier = Modifier.size(18.dp))
        Text(
            label,
            style = NestifyTheme.type.button.copy(fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold),
            color = fg,
        )
        if (trailingIcon != null) Icon(trailingIcon, null, tint = fg, modifier = Modifier.size(18.dp))
    }
}

/** Convenience: primary CTA filled with the teal FAB gradient. */
@Composable
fun NButtonGradient(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: BtnSize = BtnSize.Md,
    full: Boolean = false,
    trailingIcon: ImageVector? = null,
) = NButton(
    label = label, onClick = onClick, modifier = modifier, size = size, full = full,
    trailingIcon = trailingIcon, gradient = NestifyGradients.brandFab(),
)

// ─────────────────────────────────────────────────────────────────────────────
// Segmented tab pill
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TabPill(
    tabs: List<String>,
    active: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clip(Radii.pill)
            .background(c.surface2)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEachIndexed { i, t ->
            val on = i == active
            Box(
                Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(Radii.pill)
                    .background(if (on) c.surface else Color.Transparent)
                    .clickable { onChange(i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    t,
                    style = NestifyTheme.type.label.copy(fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (on) c.ink else c.ink50,
                )
            }
        }
    }
}

/**
 * Horizontally-scrollable segmented pill — used when there are more sections than
 * comfortably fit a fixed-width [TabPill] (e.g. the Academics hub's six sections).
 * Each tab sizes to its label; the active tab gets a raised surface pill.
 */
@Composable
fun ScrollableTabPill(
    tabs: List<String>,
    active: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clip(Radii.pill)
            .background(c.surface2)
            .horizontalScroll(rememberScrollState())
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEachIndexed { i, t ->
            val on = i == active
            Box(
                Modifier
                    .height(36.dp)
                    .clip(Radii.pill)
                    .background(if (on) c.surface else Color.Transparent)
                    .clickable { onChange(i) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    t,
                    style = NestifyTheme.type.label.copy(fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (on) c.ink else c.ink50,
                )
            }
        }
    }
}
