package com.example.glassweather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.glassweather.domain.WeatherCondition
import com.example.glassweather.ui.skyPalette

/**
 * Кольорове небо з повільно дрейфуючими м'якими плямами світла —
 * саме вони «просвічують» крізь скляні картки.
 */
@Composable
fun SkyBackground(
    condition: WeatherCondition?,
    isDay: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = skyPalette(condition, isDay)
    val top by animateColorAsState(palette.top, tween(1200), label = "top")
    val bottom by animateColorAsState(palette.bottom, tween(1200), label = "bottom")
    val orbA by animateColorAsState(palette.orbA, tween(1200), label = "orbA")
    val orbB by animateColorAsState(palette.orbB, tween(1200), label = "orbB")

    val drift by rememberInfiniteTransition(label = "drift").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16_000, easing = LinearEasing), RepeatMode.Reverse),
        label = "driftValue",
    )

    Box(modifier.background(Brush.verticalGradient(listOf(top, bottom)))) {
        Canvas(Modifier.fillMaxSize()) {
            glowOrb(orbA, Offset(size.width * (0.15f + 0.5f * drift), size.height * 0.18f), size.minDimension * 0.75f)
            glowOrb(orbB, Offset(size.width * (0.9f - 0.5f * drift), size.height * 0.55f), size.minDimension * 0.85f)
            glowOrb(orbA, Offset(size.width * (0.3f + 0.3f * drift), size.height * 0.95f), size.minDimension * 0.7f)
        }
    }
}

private fun DrawScope.glowOrb(color: Color, center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.65f), Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}
