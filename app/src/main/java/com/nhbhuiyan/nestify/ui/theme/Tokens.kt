package com.nhbhuiyan.nestify.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * BrainSton spacing / radii / elevation tokens (ported from `system.jsx` → `T`).
 *
 * Single source of truth for the Nestify UI remake. Screens consume these instead of
 * hard-coded dp values. Default screen edge padding is [Space.screen] (20.dp).
 */
object Space {
    val xs = 6.dp
    val s = 8.dp
    val s10 = 10.dp
    val m = 12.dp
    val m14 = 14.dp
    val l = 16.dp
    val screen = 20.dp   // default horizontal screen padding
    val xl = 24.dp
    val xxl = 28.dp
    val xxxl = 32.dp
}

/** Corner radii (T.r in system.jsx). */
object Radii {
    val xs = RoundedCornerShape(6.dp)
    val s = RoundedCornerShape(10.dp)
    val m = RoundedCornerShape(14.dp)
    val l = RoundedCornerShape(18.dp)
    val xl = RoundedCornerShape(22.dp)
    val pill = RoundedCornerShape(999.dp)

    // raw dp for ad-hoc use
    val xsDp = 6.dp
    val sDp = 10.dp
    val mDp = 14.dp
    val lDp = 18.dp
    val xlDp = 22.dp
}

/**
 * Shadow tokens. Compose has no CSS-style multi-layer box-shadow, so these are the
 * tuned single-layer equivalents used with `Modifier.shadow(elevation, shape, ...)`.
 */
object Elevation {
    val card = 10.dp     // shadowCard — subtle card lift
    val sheet = 20.dp    // shadowSheet — bottom sheets / floating nav
    val fab = 14.dp      // shadowFab — uplifted Network button / FABs
    val none = 0.dp

    // Tint used for the teal-glow FAB shadow (spotColor)
    val fabSpot: Color = Brand.copy(alpha = 0.45f)
}
