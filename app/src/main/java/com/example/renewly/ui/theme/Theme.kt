package com.example.renewly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightThemeColors = lightColorScheme(
    primary = Color(0xFF0A84FF), // Accent blue
    onPrimary = Color.White,
    secondary = Color(0xFF5E5CE6),
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5), // Off-white background
    onBackground = Color(0xFF1C1C1E),
    surface = Color(0xFFFFFFFF), // White cards
    onSurface = Color(0xFF1C1C1E),
    error = Color(0xFFE53935)
)

val DarkThemeColors = darkColorScheme(
    primary = Color(0xFF0A84FF), // Accent blue
    onPrimary = Color.White,
    secondary = Color(0xFF7D7AFF),
    onSecondary = Color.White,
    background = Color(0xFF000000), // OLED black background
    onBackground = Color(0xFFE5E5E7),
    surface = Color(0xFF1C1C1E), // Dark gray cards
    onSurface = Color(0xFFE5E5E7),
    error = Color(0xFFE53935)
)

@Composable
fun RenewlyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: ColorScheme = if (darkTheme) DarkThemeColors else LightThemeColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}