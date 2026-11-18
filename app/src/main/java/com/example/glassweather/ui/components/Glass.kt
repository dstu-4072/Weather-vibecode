package com.example.glassweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

private val GlassShape = RoundedCornerShape(28.dp)

/** Заливка: напівпрозорий градієнт + «відблиск» у верхньому лівому куті. */
private fun Modifier.glassSurface(shape: Shape, strength: Float = 1f): Modifier = this
    .clip(shape)
    .background(
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.28f * strength),
                Color.White.copy(alpha = 0.10f * strength),
            ),
        ),
    )
    .drawBehind {
        drawRect(
            Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.22f * strength), Color.Transparent),
                center = Offset(size.width * 0.15f, 0f),
                radius = size.maxDimension * 0.7f,
            ),
        )
    }
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.35f),
            ),
        ),
        shape = shape,
    )

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = GlassShape,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .glassSurface(shape)
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
    removeDescription: String? = null,
) {
    Row(
        modifier = modifier
            .glassSurface(CircleShape, strength = if (selected) 1.6f else 0.8f)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
        if (onRemove != null) {
            Spacer(Modifier.width(10.dp))
            Text(
                text = "✕",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.clickable(onClick = onRemove),
            )
        }
    }
}

@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔍")
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.6f),
                        )
                    }
                    inner()
                }
                if (value.isNotEmpty()) {
                    Text(
                        text = "✕",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.clickable { onValueChange("") },
                    )
                }
            }
        },
    )
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Color.White.copy(alpha = 0.75f),
        modifier = modifier.padding(bottom = 8.dp),
    )
}
