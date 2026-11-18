package com.example.glassweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GlassColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color(0xFF0B1E3F),
    surface = Color.Transparent,
    onSurface = Color.White,
    background = Color.Transparent,
    onBackground = Color.White,
)

/** Додаток завжди використовує «скляний» вигляд на кольоровому тлі, незалежно від системної теми. */
@Composable
fun GlassWeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GlassColorScheme,
        typography = Typography(),
        content = content,
    )
}
