package com.autolearn.ar.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.data.CompetencyStatus
import com.autolearn.ar.data.PracticalJob
import com.autolearn.ar.data.practicalJobsList

private val EngiBg = Color(0xFF0B0F17)
private val EngiCardBg = Color(0xFF151C28)
private val EngiBorder = Color(0xFF26334A)
private val EngiGold = Color(0xFFF59E0B)
private val EngiGoldBright = Color(0xFFFBBF24)
private val EngiTextMain = Color(0xFFF8FAFC)
private val EngiTextMuted = Color(0xFF94A3B8)
private val EngiCyan = Color(0xFF06B6D4)
private val EngiGreen = Color(0xFF10B981)

@Composable
fun HomeScreen(
    onStartJob: (PracticalJob) -> Unit = {},
    onScanComponent: () -> Unit = {},
    isSpokenInstructionsEnabled: Boolean = true,
    onToggleSpokenInstructions: (Boolean) -> Unit = {}
) {
    var spokenEnabled by remember { mutableStateOf(isSpokenInstructionsEnabled) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EngiBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // ==========================================
        // 1. BRAND HEADER & APP TITLE
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EngiGreen.copy(alpha = pulseGlow))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AUTOLEARN AR · AUTOMOTIVE PLATFORM",
                        color = EngiCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "EngiScan AI",
                    color = EngiTextMain,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            // Top Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EngiCardBg)
                    .border(1.dp, EngiBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "TFLite v2.1",
                    color = EngiGoldBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "AI-powered augmented reality practical sheets for automobile engine servicing and maintenance.",
            color = EngiTextMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 2. HERO GLASSMORPHIC AI SCAN CARD
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EngiGreen.copy(alpha = 0.8f),
                            EngiCyan.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B29))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EngiGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AI COMPONENT VISION",
                            color = EngiGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "LIVE CAMERA",
                        color = EngiCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Scan & Identify Engine Component",
                    color = EngiTextMain,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Point camera at any engine component to identify it and instantly trigger the AR practical workflow.",
                    color = EngiTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onScanComponent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EngiGreen,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OPEN AI CAMERA SCANNER",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Practical Assessment Tasks",
                color = EngiTextMain,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${practicalJobsList.size} Modules Available",
                color = EngiTextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 3. PRACTICAL JOBS CARDS
        // ==========================================
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            practicalJobsList.forEach { job ->
                PracticalJobCard(
                    job = job,
                    onStartClick = { onStartJob(job) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 4. PREFERENCES CARD (SPOKEN INSTRUCTIONS TOGGLE)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EngiBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EngiCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Voice Guidance (TTS)",
                        color = EngiTextMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Spoken step directives and verification feedback.",
                        color = EngiTextMuted,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = spokenEnabled,
                    onCheckedChange = {
                        spokenEnabled = it
                        onToggleSpokenInstructions(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F1520),
                        checkedTrackColor = EngiGoldBright,
                        uncheckedThumbColor = EngiTextMuted,
                        uncheckedTrackColor = Color(0xFF1E293B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "🔒 Privacy: All AI computer vision models run locally on-device.",
            color = EngiTextMuted.copy(alpha = 0.7f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PracticalJobCard(
    job: PracticalJob,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EngiBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EngiCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComponentJobIcon(iconType = job.iconType)

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        color = EngiTextMain,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = job.subtitle,
                        color = EngiTextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val (statusText, statusColor) = when (job.status) {
                        CompetencyStatus.COMPETENT -> "Competent · ${job.score ?: 100}/100" to EngiGreen
                        CompetencyStatus.NEEDS_PRACTICE -> "Needs Practice · ${job.score ?: 0}/100" to EngiGoldBright
                        CompetencyStatus.NOT_ASSESSED -> "Not Yet Assessed" to EngiTextMuted
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val buttonText = if (job.status == CompetencyStatus.NOT_ASSESSED) "START PRACTICAL TASK" else "RETAKE PRACTICAL TASK"

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EngiGold,
                    contentColor = Color(0xFF0F1520)
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}