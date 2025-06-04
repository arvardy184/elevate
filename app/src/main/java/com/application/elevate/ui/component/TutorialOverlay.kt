package com.application.elevate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun TutorialOverlay(
    highlightRect: Rect,
    message: String,
    onNext: () -> Unit,
    padding: Float = 16f // Tambahan: padding keliling highlight
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f) // Wajib untuk BlendMode.Clear
            .clickable { onNext() }
    ) {
        // Background gelap dengan lubang
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    // Expand rect pakai padding
                    val expandedRect = Rect(
                        offset = Offset(
                            highlightRect.left - padding,
                            highlightRect.top - padding
                        ),
                        size = Size(
                            highlightRect.width + padding * 2,
                            highlightRect.height + padding * 2
                        )
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            color = Color.Black.copy(alpha = 0.7f),
                            size = size
                        )
                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = expandedRect.topLeft,
                            size = expandedRect.size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()), // Sudut rounded 16dp
                            blendMode = BlendMode.Clear
                        )
                    }
                }
        )

        // Text dan isi tutorial
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset {
                    IntOffset(
                        x = 0,
                        y = (highlightRect.bottom + padding + 16).toInt()
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Text(
            text = "Tap anywhere to continue",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}
