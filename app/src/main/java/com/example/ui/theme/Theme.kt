package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TransitDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = PitchBlack,
    primaryContainer = ElectricCyanContainer,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonYellow,
    onSecondary = PitchBlack,
    secondaryContainer = NeonYellowContainer,
    onSecondaryContainer = NeonYellow,
    tertiary = NeonGreen,
    onTertiary = PitchBlack,
    tertiaryContainer = NeonGreenContainer,
    onTertiaryContainer = NeonGreen,
    error = NeonRed,
    onError = PitchBlack,
    errorContainer = NeonRedContainer,
    onErrorContainer = NeonRed,
    background = PitchBlack,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = CardStroke
)

@Composable
fun TransitSaathiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TransitDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) = TransitSaathiTheme(content = content)
