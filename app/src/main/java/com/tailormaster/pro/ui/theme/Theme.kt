package com.tailormaster.pro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenAccent,
    background = OffWhite,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    surfaceVariant = GreenLight,
    onSurfaceVariant = TextMutedLight
)

private val DarkColors = darkColorScheme(
    primary = GreenAccent,
    onPrimary = DarkBg,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = GreenLight,
    secondary = GreenAccent,
    background = DarkBg,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextMutedDark
)

@Composable
fun TailorMasterProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
