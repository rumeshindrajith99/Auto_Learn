package com.autolearn.ar.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.JobIconType

@Composable
fun ComponentJobIcon(
    iconType: JobIconType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(40.dp)) {
            val width = size.width
            val height = size.height

            when (iconType) {

                // ==========================================
                // 1. AIR FILTER HOUSING ICON
                // ==========================================
                JobIconType.AIR_FILTER -> {
                    // Housing main body (Grey plastic)
                    drawRoundRect(
                        color = Color(0xFF6B7280),
                        topLeft = Offset(width * 0.1f, height * 0.25f),
                        size = Size(width * 0.8f, height * 0.6f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Top lid lip
                    drawRoundRect(
                        color = Color(0xFF4B5563),
                        topLeft = Offset(width * 0.05f, height * 0.2f),
                        size = Size(width * 0.9f, height * 0.15f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Orange Latch Clips (Left and Right)
                    drawRoundRect(
                        color = Color(0xFFD97706),
                        topLeft = Offset(width * 0.12f, height * 0.28f),
                        size = Size(width * 0.12f, height * 0.18f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    drawRoundRect(
                        color = Color(0xFFD97706),
                        topLeft = Offset(width * 0.76f, height * 0.28f),
                        size = Size(width * 0.12f, height * 0.18f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )

                    // Filter Pleats (Vertical lines - Warm Beige)
                    val pleatColor = Color(0xFFD1D5DB)
                    val startX = width * 0.22f
                    val pleatW = width * 0.06f
                    val gap = width * 0.09f
                    for (i in 0..6) {
                        drawRect(
                            color = pleatColor,
                            topLeft = Offset(startX + (i * gap), height * 0.45f),
                            size = Size(pleatW, height * 0.35f)
                        )
                    }
                }

                // ==========================================
                // 2. OIL FILTER CANISTER ICON
                // ==========================================
                JobIconType.OIL_FILTER -> {
                    // Gold top nut
                    drawRoundRect(
                        color = Color(0xFFD99E00),
                        topLeft = Offset(width * 0.38f, height * 0.1f),
                        size = Size(width * 0.24f, height * 0.15f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )

                    // Canister top shoulder
                    drawRoundRect(
                        color = Color(0xFF475569),
                        topLeft = Offset(width * 0.28f, height * 0.22f),
                        size = Size(width * 0.44f, height * 0.15f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )

                    // Canister body (metallic grey cylinder)
                    drawRoundRect(
                        color = Color(0xFF64748B),
                        topLeft = Offset(width * 0.28f, height * 0.35f),
                        size = Size(width * 0.44f, height * 0.55f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Canister horizontal ribs
                    drawRect(
                        color = Color(0xFF334155),
                        topLeft = Offset(width * 0.28f, height * 0.48f),
                        size = Size(width * 0.44f, height * 0.05f)
                    )
                    drawRect(
                        color = Color(0xFF334155),
                        topLeft = Offset(width * 0.28f, height * 0.65f),
                        size = Size(width * 0.44f, height * 0.05f)
                    )
                }

                // ==========================================
                // 3. SPARK PLUG ICON
                // ==========================================
                JobIconType.SPARK_PLUG -> {
                    // Top terminal nut
                    drawRect(
                        color = Color(0xFFCBD5E1),
                        topLeft = Offset(width * 0.44f, height * 0.05f),
                        size = Size(width * 0.12f, height * 0.12f)
                    )

                    // Ceramic insulator body (White ribbed)
                    drawRoundRect(
                        color = Color(0xFFF8FAFC),
                        topLeft = Offset(width * 0.38f, height * 0.17f),
                        size = Size(width * 0.24f, height * 0.38f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )

                    // Insulator ribs
                    drawRect(color = Color(0xFF94A3B8), topLeft = Offset(width * 0.35f, height * 0.24f), size = Size(width * 0.3f, height * 0.04f))
                    drawRect(color = Color(0xFF94A3B8), topLeft = Offset(width * 0.35f, height * 0.32f), size = Size(width * 0.3f, height * 0.04f))
                    drawRect(color = Color(0xFF94A3B8), topLeft = Offset(width * 0.35f, height * 0.40f), size = Size(width * 0.3f, height * 0.04f))

                    // Hex nut collar (Silver)
                    drawRect(
                        color = Color(0xFF94A3B8),
                        topLeft = Offset(width * 0.3f, height * 0.55f),
                        size = Size(width * 0.4f, height * 0.12f)
                    )

                    // Threaded metallic base (Dark Silver)
                    drawRect(
                        color = Color(0xFF64748B),
                        topLeft = Offset(width * 0.36f, height * 0.67f),
                        size = Size(width * 0.28f, height * 0.22f)
                    )

                    // Ground electrode hook
                    drawRect(
                        color = Color(0xFF475569),
                        topLeft = Offset(width * 0.46f, height * 0.89f),
                        size = Size(width * 0.08f, height * 0.08f)
                    )
                }

                // ==========================================
                // 4. BATTERY ICON
                // ==========================================
                JobIconType.BATTERY -> {
                    // Positive (+) Terminal Post (Red)
                    drawRect(
                        color = Color(0xFFEF4444),
                        topLeft = Offset(width * 0.2f, height * 0.15f),
                        size = Size(width * 0.12f, height * 0.15f)
                    )

                    // Negative (-) Terminal Post (Silver)
                    drawRect(
                        color = Color(0xFFCBD5E1),
                        topLeft = Offset(width * 0.68f, height * 0.15f),
                        size = Size(width * 0.12f, height * 0.15f)
                    )

                    // Main Battery Casing (Dark Grey)
                    drawRoundRect(
                        color = Color(0xFF334155),
                        topLeft = Offset(width * 0.1f, height * 0.3f),
                        size = Size(width * 0.8f, height * 0.58f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Battery casing handle line / accent
                    drawRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(width * 0.2f, height * 0.48f),
                        size = Size(width * 0.6f, height * 0.08f)
                    )

                    // Plus and Minus signs
                    drawRect(color = Color(0xFFEF4444), topLeft = Offset(width * 0.22f, height * 0.65f), size = Size(width * 0.08f, height * 0.08f))
                    drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(width * 0.7f, height * 0.68f), size = Size(width * 0.08f, height * 0.03f))
                }
            }
        }
    }
}
