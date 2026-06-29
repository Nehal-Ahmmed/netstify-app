package com.nhbhuiyan.nestify.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * Reusable gradient utility for Nestify brand aesthetics.
 * This approximates the mesh gradient seen in the brand logo image.
 */
object NestifyGradients {

    /**
     * A stunning mesh gradient approximation using multiple color stops.
     * Replicates the Peach -> BlueGray -> SkyBlue -> DeepBlue transition.
     */
    fun meshGradient(): Brush {
        return Brush.linearGradient(
            colors = listOf(
                NestifyPeach,
                NestifyBlueGray,
                NestifySkyBlue,
                NestifyDeepBlue
            ),
            tileMode = TileMode.Clamp
        )
    }

    /**
     * A softer version of the brand gradient for backgrounds.
     */
    fun softGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                NestifySurface,
                NestifySkyBlue.copy(alpha = 0.3f),
                NestifyPeach.copy(alpha = 0.2f)
            )
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BrainSton remake gradients
    // ─────────────────────────────────────────────────────────────────────────

    /** Teal gradient for the uplifted Network FAB / primary CTAs. */
    fun brandFab(): Brush = Brush.linearGradient(
        colors = listOf(Brand, BrandDeep)
    )

    /** Deep teal-black wash for dark hero cards (CGPA / progress headers). */
    fun darkHero(): Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF14201D), SurfaceDk)
    )

    /** Subtle brand-tint background wash. */
    fun brandWash(): Brush = Brush.verticalGradient(
        colors = listOf(BrandTint, BsSurface)
    )
}
