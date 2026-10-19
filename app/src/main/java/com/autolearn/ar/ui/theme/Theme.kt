package com.autolearn.ar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(6.dp),
    extraLarge = RoundedCornerShape(6.dp)
)

private val LightColors = lightColorScheme(
    primary = BlueMid,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E8F8),
    onPrimaryContainer = NavyDeep,
    secondary = TealDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4F4F1),
    onSecondaryContainer = TealDeep,
    tertiary = Teal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD8F5DC),
    onTertiaryContainer = Slate,
    background = LightBackground,
    onBackground = Slate,
    surface = LightSurface,
    onSurface = Slate,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = SlateMid,
    outline = LightOutline,
    outlineVariant = Color(0xFFD0D7DE),
    inverseSurface = Slate,
    inverseOnSurface = LightBackground,
    inversePrimary = BlueBright,
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = BlueBright,
    onPrimary = NavyDeep,
    primaryContainer = NavyDeep,
    onPrimaryContainer = BlueBright,
    secondary = TealBright,
    onSecondary = DarkBackground,
    secondaryContainer = TealDeep,
    onSecondaryContainer = TealBright,
    tertiary = SuccessGreen,
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF2A4A3A),
    onTertiaryContainer = SuccessGreen,
    background = DarkBackground,
    onBackground = Color(0xFFF2F5F8),
    surface = DarkSurface,
    onSurface = Color(0xFFF2F5F8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFA8B4C4),
    outline = DarkOutline,
    outlineVariant = Color(0xFF3A4E64),
    inverseSurface = LightBackground,
    inverseOnSurface = Slate,
    inversePrimary = BlueMid,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun EngiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
