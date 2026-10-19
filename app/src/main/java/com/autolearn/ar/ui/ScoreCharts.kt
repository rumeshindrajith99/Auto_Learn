package com.autolearn.ar.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartBar(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun ScoreTrendChart(
    scores: List<Int>,
    modifier: Modifier = Modifier,
    lineColor: Color,
    pointColor: Color,
    gridColor: Color
) {
    if (scores.isEmpty()) return
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
    ) {
        val leftPad = 8.dp.toPx()
        val rightPad = 8.dp.toPx()
        val topPad = 10.dp.toPx()
        val bottomPad = 10.dp.toPx()
        val chartW = (size.width - leftPad - rightPad).coerceAtLeast(1f)
        val chartH = (size.height - topPad - bottomPad).coerceAtLeast(1f)
        val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
            val y = topPad + chartH * (1f - fraction)
            drawLine(
                color = gridColor.copy(alpha = 0.45f),
                start = Offset(leftPad, y),
                end = Offset(leftPad + chartW, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dash
            )
        }

        fun pointFor(index: Int, score: Int): Offset {
            val x = if (scores.size == 1) {
                leftPad + chartW / 2f
            } else {
                leftPad + chartW * (index / (scores.size - 1).toFloat())
            }
            val y = topPad + chartH * (1f - (score.coerceIn(0, 100) / 100f))
            return Offset(x, y)
        }

        val line = Path()
        scores.forEachIndexed { index, score ->
            val point = pointFor(index, score)
            if (index == 0) line.moveTo(point.x, point.y) else line.lineTo(point.x, point.y)
        }
        drawPath(
            path = line,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        scores.forEachIndexed { index, score ->
            val point = pointFor(index, score)
            drawCircle(color = pointColor, radius = 4.dp.toPx(), center = point)
        }
    }
}

@Composable
fun ScoreBarChart(
    bars: List<ChartBar>,
    modifier: Modifier = Modifier,
    trackColor: Color
) {
    if (bars.isEmpty()) return
    val maxValue = bars.maxOf { it.value }.coerceAtLeast(1f)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                val fraction = (bar.value / maxValue).coerceIn(0.06f, 1f)
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    val barWidth = size.width * 0.62f
                    val left = (size.width - barWidth) / 2f
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(left, 0f),
                        size = Size(barWidth, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    val barHeight = size.height * fraction
                    drawRoundRect(
                        color = bar.color,
                        topLeft = Offset(left, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = bar.value.toInt().toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StepMixChart(
    verified: Int,
    skipped: Int,
    incomplete: Int,
    verifiedColor: Color,
    skippedColor: Color,
    incompleteColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    val total = (verified + skipped + incomplete).coerceAtLeast(1)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
    ) {
        val radius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = radius
        )
        var x = 0f
        val segments = listOf(
            verified to verifiedColor,
            skipped to skippedColor,
            incomplete to incompleteColor
        )
        segments.forEach { (count, color) ->
            if (count <= 0) return@forEach
            val width = size.width * (count / total.toFloat())
            drawRoundRect(
                color = color,
                topLeft = Offset(x, 0f),
                size = Size(width, size.height),
                cornerRadius = radius
            )
            x += width
        }
    }
}
