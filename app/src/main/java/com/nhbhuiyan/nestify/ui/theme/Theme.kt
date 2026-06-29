package com.nhbhuiyan.nestify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim
)

/**
 * Extended BrainSton color tokens that Material3's ColorScheme has no slot for
 * (ink hierarchy, brandSoft/tint, coral, hairlines, dark hero surface).
 * Access via `NestifyTheme.colors` inside any composable under [NestifyTheme].
 */
data class NestifyColorTokens(
    val brand: Color,
    val brandDeep: Color,
    val brandSoft: Color,
    val brandTint: Color,
    val accent: Color,
    val accentSoft: Color,
    val secondary: Color,
    val secondarySoft: Color,
    val coral: Color,
    val coralSoft: Color,
    val warn: Color,
    val warnSoft: Color,
    val ok: Color,
    val okSoft: Color,
    val ink: Color,
    val ink70: Color,
    val ink50: Color,
    val ink30: Color,
    val ink10: Color,
    val surface: Color,
    val surface2: Color,
    val surfaceDk: Color,
    val canvas: Color,
    val hair: Color,
    val hair2: Color,
)

private val LightTokens = NestifyColorTokens(
    brand = Brand, brandDeep = BrandDeep, brandSoft = BrandSoft, brandTint = BrandTint,
    accent = Accent, accentSoft = AccentSoft, secondary = Secondary, secondarySoft = SecondarySoft,
    coral = Coral, coralSoft = CoralSoft, warn = Warn, warnSoft = WarnSoft, ok = Ok, okSoft = OkSoft,
    ink = Ink, ink70 = Ink70, ink50 = Ink50, ink30 = Ink30, ink10 = Ink10,
    surface = BsSurface, surface2 = Surface2, surfaceDk = SurfaceDk, canvas = BrandBg,
    hair = Hair, hair2 = Hair2,
)

private val DarkTokens = NestifyColorTokens(
    brand = BrandDark, brandDeep = BrandDeepDark, brandSoft = BrandSoftDark, brandTint = BrandTintDark,
    accent = AccentDark, accentSoft = AccentSoftDark, secondary = SecondaryDark, secondarySoft = SecondarySoftDark,
    coral = Color(0xFFEDA98C), coralSoft = Color(0xFF3A2218), warn = Color(0xFFD9B873), warnSoft = Color(0xFF332A18),
    ok = Color(0xFF6FC79E), okSoft = Color(0xFF18302A),
    ink = InkOnDark, ink70 = InkOnDark70, ink50 = InkOnDark50, ink30 = Color(0xFF5C6863), ink10 = Color(0xFF263430),
    surface = BsSurfaceDark, surface2 = BsSurfaceDark2, surfaceDk = SurfaceDk, canvas = BsSurfaceDarkBg,
    hair = Color(0xFFFFFFFF).copy(alpha = 0.10f), hair2 = Color(0xFFFFFFFF).copy(alpha = 0.06f),
)

val LocalNestifyColors = staticCompositionLocalOf { LightTokens }
val LocalNestifyType = staticCompositionLocalOf { NestifyType() }

/** Ergonomic accessor: `NestifyTheme.colors.brandSoft`, `NestifyTheme.type.h1Serif`. */
object NestifyTheme {
    val colors: NestifyColorTokens
        @Composable get() = LocalNestifyColors.current
    val type: NestifyType
        @Composable get() = LocalNestifyType.current
}

@Composable
fun NestifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val tokens = if (darkTheme) DarkTokens else LightTokens

    CompositionLocalProvider(
        LocalNestifyColors provides tokens,
        LocalNestifyType provides NestifyType(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
