package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueFill
import com.example.ui.theme.WaterBlueRim

@Composable
fun WaterCupIcon(
    isFilled: Boolean,
    description: String,
    modifier: Modifier = Modifier,
) {
    val fillFraction by animateFloatAsState(
        targetValue = if (isFilled) 0.82f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "water_cup_fill",
    )

    Canvas(
        modifier = modifier
            .size(width = 52.dp, height = 62.dp)
            .semantics { contentDescription = description }
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.085f
        val topInset = w * 0.10f
        val bottomInset = w * 0.22f
        val topY = h * 0.12f
        val bottomY = h * 0.90f

        val cupPath = Path().apply {
            moveTo(topInset, topY)
            lineTo(w - topInset, topY)
            lineTo(w - bottomInset, bottomY)
            lineTo(bottomInset, bottomY)
            close()
        }

        // Subtle glass background
        drawPath(
            path = cupPath,
            color = if (isFilled) Color.White else Color.White.copy(alpha = 0.72f),
        )

        // Animated blue water fill clipped to the cup silhouette
        if (fillFraction > 0.01f) {
            clipPath(cupPath) {
                val usableHeight = bottomY - topY
                val waterTopY = bottomY - (usableHeight * fillFraction)

                drawRect(
                    color = WaterBlueFill,
                    topLeft = Offset(0f, waterTopY),
                    size = Size(w, bottomY - waterTopY + strokeWidth),
                )

                // Soft surface highlight line at top of water
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.45f),
                    topLeft = Offset(topInset, waterTopY + h * 0.04f),
                    size = Size(w - topInset * 2f, h * 0.06f),
                    cornerRadius = CornerRadius(h * 0.03f, h * 0.03f),
                )
            }
        }

        // Bold cup outline for high visibility
        drawPath(
            path = cupPath,
            color = if (isFilled) WaterBlue else WaterBlueRim,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}
