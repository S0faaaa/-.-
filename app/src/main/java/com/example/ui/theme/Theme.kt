package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HighDensityColorScheme = lightColorScheme(
    primary = M3Primary,
    primaryContainer = M3PrimaryContainer,
    onPrimaryContainer = M3OnPrimaryContainer,
    secondary = M3Secondary,
    secondaryContainer = M3SecondaryContainer,
    background = M3Background,
    surface = M3Surface,
    surfaceVariant = M3SurfaceVariant,
    outline = M3Outline,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = M3TextPrimary,
    onSurface = M3TextPrimary,
    onSurfaceVariant = M3TextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}

