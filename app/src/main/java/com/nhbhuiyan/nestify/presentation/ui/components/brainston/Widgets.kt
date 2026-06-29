package com.nhbhuiyan.nestify.presentation.ui.components.brainston

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.ui.theme.Accent
import com.nhbhuiyan.nestify.ui.theme.Brand
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

// ─────────────────────────────────────────────────────────────────────────────
// Avatar — initials with deterministic color
// ─────────────────────────────────────────────────────────────────────────────

private val avatarPalette = listOf(
    Color(0xFFFF0000), Color(0xFF1F6F8B), Color(0xFF8F5A3C),
    Color(0xFF5A4A8F), Color(0xFF3F7F4D), Color(0xFF7F4A3F),
)

@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 36.dp,
    ring: Boolean = false,
) {
    val clean = name.ifBlank { "?" }
    val initials = clean.trim().split(" ").mapNotNull { it.firstOrNull() }.take(2)
        .joinToString("").uppercase()
    val idx = (clean[0].code + (clean.getOrNull(1)?.code ?: 0)) % avatarPalette.size
    val bg = avatarPalette[idx]
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .then(if (ring) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials,
            color = Color.White,
            style = NestifyTheme.type.label.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = androidx.compose.ui.unit.TextUnit(size.value * 0.38f, androidx.compose.ui.unit.TextUnitType.Sp),
            ),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search bar (pill)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SearchBarPill(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    trailing: @Composable (() -> Unit)? = null,
) {
    val c = NestifyTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(Radii.pill)
            .background(c.surface)
            .border(1.dp, c.hair, Radii.pill)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Default.Search, null, tint = c.ink50, modifier = Modifier.size(20.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) Text(placeholder, style = NestifyTheme.type.body, color = c.ink50)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = NestifyTheme.type.body.copy(color = c.ink),
                cursorBrush = SolidColor(c.brand),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (trailing != null) trailing()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Labeled input
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NestifyInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val c = NestifyTheme.colors
    Column(modifier) {
        if (label != null) {
            Text(
                label,
                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                color = c.ink70,
            )
            Spacer(Modifier.height(6.dp))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(Radii.m)
                .background(c.surface)
                .border(1.5.dp, c.hair, Radii.m)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (leadingIcon != null) Icon(leadingIcon, null, tint = c.ink50, modifier = Modifier.size(18.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text(placeholder, style = NestifyTheme.type.body, color = c.ink50)
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = NestifyTheme.type.body.copy(color = c.ink),
                    cursorBrush = SolidColor(c.brand),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (trailing != null) trailing()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty / gated state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    primaryLabel: String? = null,
    onPrimary: (() -> Unit)? = null,
) {
    val c = NestifyTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Space.xl, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(c.brandSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = c.brand, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(Space.xl))
        Text(title, style = NestifyTheme.type.h2Serif, color = c.ink, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Space.s))
        Text(
            description,
            style = NestifyTheme.type.body,
            color = c.ink50,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp),
        )
        if (primaryLabel != null && onPrimary != null) {
            Spacer(Modifier.height(Space.xxl))
            NButton(primaryLabel, onPrimary, size = BtnSize.Lg, full = true)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Brand mark (Nestify lockup)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 28.dp,
    showLabel: Boolean = false,
) {
    val c = NestifyTheme.colors
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.3f))
                // Brand-constant deep-red → pure-red gradient (identical in light & dark) so the
                // lockup stays high-contrast against white "N" regardless of theme.
                .background(Brush.linearGradient(listOf(Brand, Accent))),
            contentAlignment = Alignment.Center,
        ) {
            Text("N", color = Color.White, style = NestifyTheme.type.h3Serif.copy(
                fontSize = androidx.compose.ui.unit.TextUnit(size.value * 0.5f, androidx.compose.ui.unit.TextUnitType.Sp)
            ))
        }
        if (showLabel) {
            Text("Nestify", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold, fontSize = androidx.compose.ui.unit.TextUnit(17f, androidx.compose.ui.unit.TextUnitType.Sp)), color = c.ink)
        }
    }
}
