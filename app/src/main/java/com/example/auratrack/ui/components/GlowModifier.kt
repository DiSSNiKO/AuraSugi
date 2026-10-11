package com.example.aurasugi.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlow(
    color: Color,
    borderRadius: Dp = 8.dp,
    blurRadius: Dp = 8.dp,
    spread: Dp = 1.dp,
    alpha: Float = 0.2f
): Modifier = this.drawBehind {
    val radiusPx = borderRadius.toPx()
    val spreadPx = spread.toPx()
    val blurPx = blurRadius.toPx()

    // Layer 1: Outer soft glow (Hardware-accelerated)
    val outerSpread = spreadPx + blurPx * 0.5f
    drawRoundRect(
        color = color.copy(alpha = alpha * 0.3f),
        topLeft = Offset(-outerSpread, -outerSpread),
        size = Size(size.width + outerSpread * 2, size.height + outerSpread * 2),
        cornerRadius = CornerRadius(radiusPx + outerSpread)
    )

    // Layer 2: Inner tight glow (Hardware-accelerated)
    val innerSpread = spreadPx + blurPx * 0.2f
    drawRoundRect(
        color = color.copy(alpha = alpha * 0.7f),
        topLeft = Offset(-innerSpread, -innerSpread),
        size = Size(size.width + innerSpread * 2, size.height + innerSpread * 2),
        cornerRadius = CornerRadius(radiusPx + innerSpread)
    )
}
