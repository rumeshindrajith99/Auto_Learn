package com.autolearn.ar.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.autolearn.ar.ui.theme.LocalAppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val type = MaterialTheme.typography

    var playing by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        playing = true
        delay(SplashScene.EXIT_MS.toLong())
        onFinished()
    }

    val transition = updateTransition(targetState = playing, label = "splash")

    val markScale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.MARK_END_MS - SplashScene.MARK_START_MS,
                delayMillis = SplashScene.MARK_START_MS,
                easing = FastOutSlowInEasing
            )
        },
        label = "markScale"
    ) { if (it) 1f else 0.72f }

    val markAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.MARK_END_MS - SplashScene.MARK_START_MS,
                delayMillis = SplashScene.MARK_START_MS
            )
        },
        label = "markAlpha"
    ) { if (it) 1f else 0f }

    val scanProgress by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.SCAN_END_MS - SplashScene.SCAN_START_MS,
                delayMillis = SplashScene.SCAN_START_MS,
                easing = LinearEasing
            )
        },
        label = "scan"
    ) { if (it) 1f else 0f }

    val titleAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.TITLE_END_MS - SplashScene.TITLE_START_MS,
                delayMillis = SplashScene.TITLE_START_MS,
                easing = FastOutSlowInEasing
            )
        },
        label = "titleAlpha"
    ) { if (it) 1f else 0f }

    val titleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.TITLE_END_MS - SplashScene.TITLE_START_MS,
                delayMillis = SplashScene.TITLE_START_MS,
                easing = FastOutSlowInEasing
            )
        },
        label = "titleOffset"
    ) { if (it) 0f else 12f }

    val lineAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = SplashScene.LINE_END_MS - SplashScene.LINE_START_MS,
                delayMillis = SplashScene.LINE_START_MS
            )
        },
        label = "lineAlpha"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(markScale)
                    .alpha(markAlpha),
                contentAlignment = Alignment.Center
            ) {
                val markColor = colors.surfaceVariant
                val strokeColor = colors.primary
                val scanColor = app.teal
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val radius = 6.dp.toPx()

                    drawRoundRect(
                        color = markColor,
                        topLeft = Offset(w * 0.12f, h * 0.22f),
                        size = Size(w * 0.76f, h * 0.58f),
                        cornerRadius = CornerRadius(radius, radius)
                    )
                    drawRoundRect(
                        color = strokeColor,
                        topLeft = Offset(w * 0.22f, h * 0.10f),
                        size = Size(w * 0.56f, h * 0.18f),
                        cornerRadius = CornerRadius(radius / 2f, radius / 2f)
                    )
                    drawRoundRect(
                        color = app.success.copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.18f, h * 0.38f),
                        size = Size(w * 0.16f, h * 0.12f),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                    drawRoundRect(
                        color = app.success.copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.66f, h * 0.38f),
                        size = Size(w * 0.16f, h * 0.12f),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    val scanY = h * 0.18f + (h * 0.64f * scanProgress)
                    drawLine(
                        color = scanColor,
                        start = Offset(w * 0.10f, scanY),
                        end = Offset(w * 0.90f, scanY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = SplashScene.TITLE,
                style = type.headlineMedium,
                color = colors.onBackground,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleOffset.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = SplashScene.LINE,
                style = type.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.alpha(lineAlpha)
            )
        }
    }
}
