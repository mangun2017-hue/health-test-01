package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IvoryLightColorScheme = lightColorScheme(
    primary = TakenGreen,
    onPrimary = Color.White,
    primaryContainer = TakenGreenSurface,
    onPrimaryContainer = TakenGreenBorder,
    secondary = WaterBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterSurface,
    onSecondaryContainer = WaterBlue,
    background = IvoryBackground,
    onBackground = InkPrimary,
    surface = IvorySurface,
    onSurface = InkPrimary,
    surfaceVariant = IvorySurfaceVariant,
    onSurfaceVariant = InkSecondary,
    outline = IvoryBorder,
)

@Composable
fun SeniorHealthTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = IvoryLightColorScheme,
        typography = SeniorTypography,
        content = content,
    )
}
