package com.autolearn.ar.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.data.TaskTheory

// ==========================================
// COLOR PALETTE
// ==========================================
private val TdBg = Color(0xFF0B0F19)
private val TdCard = Color(0xFF161E2E)
private val TdBorder = Color(0xFF26334D)
private val TdCyan = Color(0xFF38BDF8)
private val TdGreen = Color(0xFF34D399)
private val TdAmber = Color(0xFFFBBF24)
private val TdRed = Color(0xFFF87171)
private val TdTextPrimary = Color(0xFFF8FAFC)
private val TdTextSecondary = Color(0xFF94A3B8)
private val TdPurple = Color(0xFFA78BFA)


@Composable
fun TheoryDetailScreen(
    theory: TaskTheory,
    onBack: () -> Unit,
    onStartPractical: () -> Unit,
    onOpenChatbot: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TdBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // ==========================================
            // TOP BAR
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TdTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "THEORY MODULE",
                        color = TdCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = theory.title,
                        color = TdTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }

                // Practical badge
                if (theory.hasPracticalPipeline) {
                    Box(
                        modifier = Modifier
                            .background(TdGreen.copy(alpha = 0.12f), CircleShape)
                            .border(0.5.dp, TdGreen.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SCORED",
                            color = TdGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // COMPONENT HEADER PHOTO
            // ==========================================
            if (theory.imageResId != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .border(1.dp, TdBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = theory.imageResId),
                        contentDescription = theory.title,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }


            // ==========================================
            // OVERVIEW CARD
            // ==========================================
            SectionCard(
                title = "Overview",
                icon = Icons.Default.Info,
                iconColor = TdCyan
            ) {
                Text(
                    text = theory.overview,
                    color = TdTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Why It Matters",
                    color = TdAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = theory.whyItMatters,
                    color = TdTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==========================================
            // LEARNING OBJECTIVES
            // ==========================================
            SectionCard(
                title = "Learning Objectives",
                icon = Icons.Default.CheckCircle,
                iconColor = TdGreen
            ) {
                theory.learningObjectives.forEachIndexed { index, objective ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(TdGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = TdGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = objective,
                            color = TdTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==========================================
            // SAFETY WARNINGS
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TdRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TdRed.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TdRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAFETY WARNINGS",
                            color = TdRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    theory.safetyWarnings.forEach { warning ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TdRed.copy(alpha = 0.7f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = warning,
                                color = TdTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==========================================
            // PROCEDURE STEPS
            // ==========================================
            SectionCard(
                title = "Step-by-Step Procedure",
                icon = Icons.Default.Build,
                iconColor = TdPurple
            ) {
                theory.procedureSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TdPurple.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = TdPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = step,
                            color = TdTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==========================================
            // PRO TIPS
            // ==========================================
            SectionCard(
                title = "Pro Tips",
                icon = Icons.Default.Info,
                iconColor = TdAmber
            ) {
                theory.proTips.forEach { tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip,
                            color = TdTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==========================================
            // SAMPLE Q&A PREVIEW
            // ==========================================
            SectionCard(
                title = "Common Questions",
                icon = Icons.Default.Email,
                iconColor = TdCyan
            ) {
                Text(
                    text = "These are questions the AI Chatbot can answer for this component. " +
                            "Tap \"Ask AI\" below to start a conversation.",
                    color = TdTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                theory.sampleQA.forEach { (question, _) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(TdCyan.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                            .border(0.5.dp, TdCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .clickable { onOpenChatbot() }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "\"$question\"",
                            color = TdCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ==========================================
            // ACTION BUTTONS
            // ==========================================

            // Start Practical button (only if pipeline exists)
            if (theory.hasPracticalPipeline) {
                Button(
                    onClick = onStartPractical,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TdGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START PRACTICAL SESSION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Ask AI button
            OutlinedButton(
                onClick = onOpenChatbot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    TdCyan.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = TdCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ASK AI CHATBOT",
                    color = TdCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            if (!theory.hasPracticalPipeline) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Practical scoring for this component is under development.",
                    color = TdAmber.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// ==========================================
// REUSABLE SECTION CARD
// ==========================================

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TdBorder, RoundedCornerShape(16.dp))
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TdCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title.uppercase(),
                    color = iconColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}
