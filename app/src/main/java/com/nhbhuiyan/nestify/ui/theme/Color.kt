package com.nhbhuiyan.nestify.ui.theme
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// BrainSton design tokens (ported from `new plans/BrainSton LMS new design/system.jsx`).
// These are the new source of truth for the Nestify UI remake. The legacy
// Nestify*/md_theme_* values below are kept ONLY so un-migrated screens still
// compile; remove them per-screen as each screen is re-skinned (final purge: Phase F).
// ─────────────────────────────────────────────────────────────────────────────

// Backgrounds
val BrandBg     = Color(0xFFF4F2EE) // warm near-white app canvas
val BsSurface   = Color(0xFFFFFFFF) // cards
val Surface2    = Color(0xFFFAF8F4) // subtle alt surface
val SurfaceDk   = Color(0xFF0F1A18) // deep teal-black (dark hero cards)

// Ink (text hierarchy)
val Ink   = Color(0xFF0F1A18)
val Ink70 = Color(0xFF3A4744)
val Ink50 = Color(0xFF6E7A77)
val Ink30 = Color(0xFFA6ADAB)
val Ink10 = Color(0xFFE6E4DF)

// Brand — deep-red primary (#8B0000), pure-red accent (#FF0000), teal secondary (#1A8080).
// Pure red (#FF0000) is reserved as a SMALL accent — badges, dots, destructive actions —
// never a large surface fill. Large fills use the deep-red [Brand] / [BrandDeep].
val Brand     = Color(0xFF8B0000) // primary — deep red / maroon
val BrandDeep = Color(0xFF6B0000) // pressed / darker primary
val BrandSoft = Color(0xFFF7E4E4) // soft maroon tint surface
val BrandTint = Color(0xFFFCF3F3) // lightest maroon wash

// Accent — pure brand red, used sparingly
val Accent     = Color(0xFFFF0000)
val AccentSoft = Color(0xFFFFE5E5)

// Secondary — teal
val Secondary     = Color(0xFF1A8080)
val SecondaryDeep = Color(0xFF0F5C5C)
val SecondarySoft = Color(0xFFE2F1F1)

// Accent — warm coral (sparingly: like/error/sale)
val Coral     = Color(0xFFD97A57)
val CoralSoft = Color(0xFFFAEBE3)

// Functional
val Warn     = Color(0xFFB8842B)
val WarnSoft = Color(0xFFFAF1DE)
val Ok       = Color(0xFF1B7A53)
val OkSoft   = Color(0xFFDDEEE6)

// Hairlines / dividers (alpha over ink)
val Hair  = Color(0xFF0F1A18).copy(alpha = 0.08f)
val Hair2 = Color(0xFF0F1A18).copy(alpha = 0.05f)

// Brand dark companions — luminous tints that stay legible on dark surfaces.
val BrandDark      = Color(0xFFFF8A80) // primary on dark (light red)
val BrandDeepDark  = Color(0xFFFFB4AB)
val BrandSoftDark  = Color(0xFF3A1414) // muted maroon container on dark
val BrandTintDark  = Color(0xFF2A0F0F)
val AccentDark     = Color(0xFFFF5252)
val AccentSoftDark = Color(0xFF3A1414)
val SecondaryDark      = Color(0xFF5FC4C4) // teal on dark
val SecondarySoftDark  = Color(0xFF11332F)

// Dark-scheme companions — neutral warm-charcoal canvas so the red brand reads cleanly.
val BsSurfaceDarkBg   = Color(0xFF0E0E10) // app canvas
val BsSurfaceDark     = Color(0xFF1A1A1D) // cards
val BsSurfaceDark2    = Color(0xFF24242A) // alt surface
val InkOnDark         = Color(0xFFECECEE)
val InkOnDark70       = Color(0xFFB7B7BC)
val InkOnDark50       = Color(0xFF85858C)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Schedule Category Colors
val ScheduleWeekly = Color(0xFF3498DB)
val ScheduleMonthly = Color(0xFF9B59B6)
val ScheduleYearly = Color(0xFF27AE60)
val ScheduleBackground = Color(0xFFF8FAFB)
val ScheduleCardSurface = Color(0xFFFFFFFF)
val ScheduleTextPrimary = Color(0xFF2C3E50)
val ScheduleTextSecondary = Color(0xFF7F8C8D)
val ScheduleDivider = Color(0xFFECF0F1)

// Nestify Brand Colors
val NestifyPeach = Color(0xFFE6D0BA)
val NestifyBlueGray = Color(0xFFA8BCC2)
val NestifySkyBlue = Color(0xFFC7DBE3)
val NestifyDeepBlue = Color(0xFFAEC4D1)
val NestifySlate = Color(0xFF333F48)
val NestifyGreen = Color(0xFF7BB87B)
val NestifyWhite = Color(0xFFFFFFFF)
val NestifySurface = Color(0xFFF7F9FB)


// Light Material scheme — repointed to the BrainSton teal/warm palette.
val md_theme_light_primary = Brand                       // deep red
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = BrandSoft
val md_theme_light_onPrimaryContainer = BrandDeep
val md_theme_light_secondary = Secondary                 // teal
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = SecondarySoft
val md_theme_light_onSecondaryContainer = SecondaryDeep
val md_theme_light_tertiary = Accent                     // pure-red accent (sparingly)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = AccentSoft
val md_theme_light_onTertiaryContainer = BrandDeep
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = BrandBg
val md_theme_light_onBackground = Ink
val md_theme_light_surface = BsSurface
val md_theme_light_onSurface = Ink
val md_theme_light_surfaceVariant = Surface2
val md_theme_light_onSurfaceVariant = Ink50
val md_theme_light_outline = Ink30
val md_theme_light_inverseOnSurface = Color(0xFFEAF0EE)
val md_theme_light_inverseSurface = SurfaceDk
val md_theme_light_inversePrimary = BrandDark
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Brand
val md_theme_light_outlineVariant = Ink10
val md_theme_light_scrim = Color(0xFF000000)


// Dark Material scheme — teal-black canvas with luminous teal brand.
val md_theme_dark_primary = BrandDark                    // luminous red
val md_theme_dark_onPrimary = Color(0xFF5A0000)
val md_theme_dark_primaryContainer = Color(0xFF7A0000)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDAD5)
val md_theme_dark_secondary = SecondaryDark              // teal
val md_theme_dark_onSecondary = Color(0xFF003735)
val md_theme_dark_secondaryContainer = Color(0xFF0F4F4D)
val md_theme_dark_onSecondaryContainer = Color(0xFFB6ECEA)
val md_theme_dark_tertiary = AccentDark                  // pure-red accent
val md_theme_dark_onTertiary = Color(0xFF5A0000)
val md_theme_dark_tertiaryContainer = Color(0xFF7A0000)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDAD5)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = BsSurfaceDarkBg
val md_theme_dark_onBackground = InkOnDark
val md_theme_dark_surface = BsSurfaceDark
val md_theme_dark_onSurface = InkOnDark
val md_theme_dark_surfaceVariant = BsSurfaceDark2
val md_theme_dark_onSurfaceVariant = InkOnDark70
val md_theme_dark_outline = InkOnDark50
val md_theme_dark_inverseOnSurface = Ink
val md_theme_dark_inverseSurface = InkOnDark
val md_theme_dark_inversePrimary = Brand
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = BrandDark
val md_theme_dark_outlineVariant = BsSurfaceDark2
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFF825500)