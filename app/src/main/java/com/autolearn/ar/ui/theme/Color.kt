package com.autolearn.ar.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Slate = Color(0xFF2D3F54)
val SlateMid = Color(0xFF52647A)
val SuccessGreen = Color(0xFF73CC80)
val NavyDeep = Color(0xFF004C94)
val BlueMid = Color(0xFF006ECF)
val BlueBright = Color(0xFF3C93FA)
val TealBright = Color(0xFF00D4C6)
val Teal = Color(0xFF0FA69D)
val TealDeep = Color(0xFF028183)

val LightBackground = Color(0xFFF3F5F7)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE4E9EE)
val LightOutline = Color(0xFFB4BEC8)

val DarkBackground = Color(0xFF1B2734)
val DarkSurface = Color(0xFF2D3F54)
val DarkSurfaceVariant = Color(0xFF3A4E64)
val DarkOutline = Color(0xFF52647A)

@Immutable
data class AppColors(
    val success: Color,
    val teal: Color,
    val tealDeep: Color,
    val slate: Color,
    val slateMid: Color,
    val warning: Color
)

val LightAppColors = AppColors(
    success = SuccessGreen,
    teal = Teal,
    tealDeep = TealDeep,
    slate = Slate,
    slateMid = SlateMid,
    warning = Color(0xFF0FA69D)
)

val DarkAppColors = AppColors(
    success = SuccessGreen,
    teal = TealBright,
    tealDeep = Teal,
    slate = Slate,
    slateMid = Color(0xFFA8B4C4),
    warning = TealBright
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
