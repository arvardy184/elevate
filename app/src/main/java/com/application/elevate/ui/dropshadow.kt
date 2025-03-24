package com.application.elevate.ui

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

fun Modifier.dropShadow(
    color: Color = Color.Black.copy(alpha = 0.3f),
    blurRadius: Dp = 12.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 8.dp
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = color.toArgb()
            maskFilter = BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawRoundRect(
            0f + offsetX.toPx(),
            0f + offsetY.toPx(),
            size.width,
            size.height,
            20f, 20f,  // Adjust corner radius as needed
            paint
        )
    }
}