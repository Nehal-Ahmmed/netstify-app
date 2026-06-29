package com.nhbhuiyan.nestify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.R

// ─────────────────────────────────────────────────────────────────────────────
// BrainSton type families (the exact fonts the BrainSton HTML loads from Google Fonts),
// bundled as OFL TTFs in res/font/.
//   • Inter Tight     — variable (wght 100–900) → body, labels, buttons
//   • Instrument Serif — Regular + Italic only (NO bold weights) → headlines
//   • JetBrains Mono   — variable (wght 100–800) → kickers, metadata
// Variable fonts pin a weight via FontVariation.Settings (API 26+, app minSdk 28).
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalTextApi::class)
private fun interTight(weight: FontWeight) = Font(
    R.font.inter_tight_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

@OptIn(ExperimentalTextApi::class)
private fun jetBrainsMono(weight: FontWeight) = Font(
    R.font.jetbrains_mono_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val InterTight = FontFamily(
    interTight(FontWeight.Normal),
    interTight(FontWeight.Medium),
    interTight(FontWeight.SemiBold),
    interTight(FontWeight.Bold),
    interTight(FontWeight.ExtraBold),
)

/** Headlines. Only Regular + Italic exist — never request bold on this family. */
val InstrumentSerif = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

val JetBrainsMono = FontFamily(
    jetBrainsMono(FontWeight.Normal),
    jetBrainsMono(FontWeight.Medium),
)

// Convenience aliases
val NestifySans = InterTight
val NestifySerif = InstrumentSerif
val NestifyMono = JetBrainsMono

/**
 * Extended type roles that Material3's [Typography] has no slot for
 * (serif headlines, mono kickers/meta). Exposed via LocalNestifyType in Theme.kt.
 */
data class NestifyType(
    val displaySerif: TextStyle = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp
    ),
    val h1Serif: TextStyle = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 32.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp
    ),
    val h2Serif: TextStyle = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 24.sp, lineHeight = 28.sp, letterSpacing = (-0.3).sp
    ),
    val h3Serif: TextStyle = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 19.sp, lineHeight = 24.sp, letterSpacing = (-0.3).sp
    ),
    val kicker: TextStyle = TextStyle(
        fontFamily = JetBrainsMono, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 1.sp
    ),
    val meta: TextStyle = TextStyle(
        fontFamily = JetBrainsMono, fontWeight = FontWeight.Normal,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp
    ),
    val body: TextStyle = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp
    ),
    val label: TextStyle = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.1).sp
    ),
)

/**
 * Material3 typography — populated so un-migrated screens that read
 * MaterialTheme.typography.* still render. Display/Headline/Title use the serif
 * (Normal weight only); Body/Label use Inter Tight.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.4).sp
    ),
    displaySmall = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.4).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.3).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = (-0.1).sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = (-0.1).sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterTight, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 1.sp
    ),
)
