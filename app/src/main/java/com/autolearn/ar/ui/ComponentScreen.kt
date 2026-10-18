package com.autolearn.ar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.data.theoryContentMap
import com.autolearn.ar.model.EngineComponent

private val CompBg = Color(0xFF0B0F17)
private val CompCardBg = Color(0xFF151C28)
private val CompBorder = Color(0xFF26334A)
private val CompGreen = Color(0xFF10B981)
private val CompGold = Color(0xFFF59E0B)
private val CompCyan = Color(0xFF06B6D4)
private val CompTextMain = Color(0xFFF8FAFC)
private val CompTextMuted = Color(0xFF94A3B8)

@Composable
fun ComponentScreen(
    component: EngineComponent,
    confidence: Float = 0.92f,
    onContinue: () -> Unit
) {
    val confidencePercent = (confidence.coerceIn(0f, 1f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CompBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        // ==========================================
        // 1. AI DETECTION STATUS BADGE
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CompGreen.copy(alpha = 0.15f))
                    .border(1.dp, CompGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CompGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI RECOGNITION MATCHED",
                        color = CompGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Text(
                text = "TFLite CNN",
                color = CompCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 2. SCREEN TITLE
        // ==========================================
        Text(
            text = "Component Identified",
            color = CompTextMain,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Review specification sheet before initiating AR practical workflow.",
            color = CompTextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 3. MAIN COMPONENT GLASSMORPHIC CARD
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CompBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CompCardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Icon / Image + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val compImageResId = theoryContentMap[component.id]?.imageResId
                    if (compImageResId != null) {
                        Image(
                            painter = painterResource(id = compImageResId),
                            contentDescription = component.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.5.dp, CompGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, CompBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Component",
                                tint = CompGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AUTOMOTIVE COMPONENT",
                            color = CompCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = component.name,
                            color = CompTextMain,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = CompBorder)

                Spacer(modifier = Modifier.height(18.dp))

                // AI Confidence Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI MODEL CONFIDENCE",
                        color = CompTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "$confidencePercent%",
                        color = CompGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { confidence.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = CompGreen,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Function Section
                ComponentInfoSection(
                    icon = Icons.Default.Info,
                    title = "OPERATIONAL FUNCTION",
                    description = component.function
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Specifications Section
                ComponentInfoSection(
                    icon = Icons.Default.List,
                    title = "SYSTEM SPECIFICATIONS",
                    description = component.specifications
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // ==========================================
        // 4. CONFIRM & LAUNCH BUTTON
        // ==========================================
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CompGold,
                contentColor = Color(0xFF0F1520)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "CONFIRM & LAUNCH AR PRACTICAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Automotive AI Vision • AutoLearn AR",
            modifier = Modifier.fillMaxWidth(),
            color = CompTextMuted.copy(alpha = 0.7f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ComponentInfoSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, CompBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CompCyan,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = CompCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = CompTextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}