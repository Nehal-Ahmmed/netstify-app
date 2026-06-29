package com.nhbhuiyan.nestify.presentation.navigation.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.ui.theme.Elevation
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme

/**
 * A bottom-nav tab: an outlined (inactive) and filled (active) icon variant + label.
 * Index 2 in the list is treated as the uplifted center (Network) button.
 */
data class GlassNavItem(
    val label: String,
    val icon: ImageVector,        // outlined / inactive
    val iconActive: ImageVector,  // filled / active
)

/**
 * BrainSton-style floating glassmorphism navigation.
 *
 * A detached, frosted pill that floats over the content (16dp inset on all sides), holding four
 * flat tabs, with the center slot (index 2 = **Network**) lifted into a teal circular FAB.
 *
 * Glass here is a "pseudo-glass" treatment — a translucent surface wash + hairline border + soft
 * drop shadow. Compose has no native backdrop blur; real frost (via the Haze library) is a drop-in
 * upgrade that can replace the [glassBrush] background later without touching this layout.
 *
 * @param items exactly 5 entries; index 2 is rendered as the uplifted center button.
 * @param selectedItem currently selected tab index (0..4).
 */
@Composable
fun GlassBottomNav(
    items: List<GlassNavItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    // Translucent "frosted" wash — slightly opaque so content colour bleeds through underneath.
    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            c.surface.copy(alpha = 0.86f),
            c.surface.copy(alpha = 0.94f),
        )
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // ── The floating glass pill ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(66.dp)
                .shadow(
                    elevation = Elevation.sheet,
                    shape = RoundedCornerShape(30.dp),
                    spotColor = c.ink.copy(alpha = 0.30f),
                    ambientColor = c.ink.copy(alpha = 0.20f),
                    clip = false,
                )
                .clip(RoundedCornerShape(30.dp))
                .background(glassBrush)
                .border(1.dp, c.hair2, RoundedCornerShape(30.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    // Reserve space for the uplifted center FAB.
                    Spacer(Modifier.width(64.dp))
                } else {
                    GlassNavTab(
                        item = item,
                        selected = selectedItem == index,
                        onClick = { onItemClick(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // ── Uplifted center "Network" FAB ──────────────────────────────────────
        items.getOrNull(2)?.let { center ->
            NetworkFab(
                item = center,
                selected = selectedItem == 2,
                onClick = { onItemClick(2) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 52.dp),
            )
        }
    }
}

@Composable
private fun GlassNavTab(
    item: GlassNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    val tint by animateColorAsState(
        targetValue = if (selected) c.brandDeep else c.ink50,
        animationSpec = tween(220),
        label = "navTint",
    )
    val pillBg by animateColorAsState(
        targetValue = if (selected) c.brandSoft else Color.Transparent,
        animationSpec = tween(220),
        label = "navPill",
    )

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(pillBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Crossfade(targetState = selected, animationSpec = tween(200), label = "navIcon") { on ->
            Icon(
                imageVector = if (on) item.iconActive else item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
        }
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(200)),
            exit = fadeOut(tween(150)) + shrinkHorizontally(tween(150)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = item.label,
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    color = tint,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun NetworkFab(
    item: GlassNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = tween(220),
        label = "fabScale",
    )
    Box(
        modifier = modifier
            .size(60.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = Elevation.fab,
                shape = CircleShape,
                spotColor = Elevation.fabSpot,
                ambientColor = Elevation.fabSpot,
                clip = false,
            )
            .clip(CircleShape)
            .background(NestifyGradients.brandFab())
            .border(2.5.dp, Color.White, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = item.iconActive,
            contentDescription = item.label,
            tint = Color.White,
            modifier = Modifier.size(26.dp),
        )
    }
}
