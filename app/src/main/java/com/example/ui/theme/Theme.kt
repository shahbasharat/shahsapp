package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberPathColorScheme = darkColorScheme(
    primary = UbuntuOrange,
    secondary = MacOSBlue,
    tertiary = SuccessGreen,
    background = UbuntuAubergine,
    surface = MacOSDarkSurface,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

@Composable
fun CyberPathTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberPathColorScheme,
        typography = Typography,
        content = content
    )
}
