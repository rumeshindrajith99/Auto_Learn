package com.autolearn.ar.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.model.Task
import com.autolearn.ar.ui.theme.LocalAppColors

@Composable
fun EvaluationScreen(
    task: Task,
    completedSteps: List<Boolean>,
    wrongAttempts: Int,
    timeTakenMillis: Long,
    finalScore: Int,
    manuallySkippedSteps: Set<Int>,
    safetyWarnings: List<String>,
    onTryAgain: () -> Unit,
    onReviewTheory: () -> Unit,
    onFinish: () -> Unit
) {

    val totalSteps = completedSteps.size.coerceAtLeast(1)
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val EvalBg = colors.background
    val EvalCard = colors.surface
    val EvalBorder = colors.outline
    val EvalTextMain = colors.onSurface
    val EvalTextMuted = colors.onSurfaceVariant
    val EvalGreen = app.success
    val EvalAmber = colors.secondary
    val EvalRed = colors.error
    val verificationSteps = completedSteps.indices.map { "Step ${it + 1}" }
    val correctSteps = completedSteps.count { it }
    val manualStepCount = manuallySkippedSteps.size
    val automaticallyVerifiedSteps = (correctSteps - manualStepCount).coerceAtLeast(0)

    val totalSeconds = timeTakenMillis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val statusColor = when {
        finalScore >= 90 -> EvalGreen
        finalScore >= 50 -> EvalAmber
        else -> EvalRed
    }

    val wrongActionPenalty = wrongAttempts * 5

    val context = androidx.compose.ui.platform.LocalContext.current
    var historyList by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(emptyList<com.autolearn.ar.data.ScoreRecord>())
    }

    androidx.compose.runtime.LaunchedEffect(finalScore, timeTakenMillis, task.id) {
        historyList = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            com.autolearn.ar.data.ScoreRepository.saveScore(
                context = context,
                taskName = task.name,
                componentId = task.componentId,
                score = finalScore,
                durationMillis = timeTakenMillis
            )
            com.autolearn.ar.data.ScoreRepository.getHistory(context)
        }
        com.autolearn.ar.data.DailyGoalStore.refreshCompletion(
            context,
            historyList.filter {
                it.dateText.startsWith(com.autolearn.ar.data.DailyGoalStore.todayScoreLabel())
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EvalBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // Header
            Text(
                text = "Evaluation Summary",
                color = EvalTextMain,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = task.name,
                color = EvalTextMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Score Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvalBorder, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = EvalCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Final Score",
                            color = EvalTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$finalScore / 100",
                            color = statusColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Duration",
                            color = EvalTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = formattedTime,
                            color = EvalTextMain,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Breakdown Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvalBorder, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = EvalCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Practical Steps",
                        color = EvalTextMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    verificationSteps.forEachIndexed { index, stepName ->
                        val isAuto = completedSteps.getOrElse(index) { false } && index !in manuallySkippedSteps
                        val isManual = index in manuallySkippedSteps

                        val (tag, tagColor) = when {
                            isAuto -> "Verified" to EvalGreen
                            isManual -> "Skipped" to EvalAmber
                            else -> "Incomplete" to EvalRed
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}. $stepName",
                                color = EvalTextMain,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = tag,
                                color = tagColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (index < verificationSteps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(EvalBorder)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculation Breakdown
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvalBorder, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = EvalCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Score Audit",
                        color = EvalTextMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val ptsPerStep = if (totalSteps > 0) (100 / totalSteps) else 0
                    val autoPoints = automaticallyVerifiedSteps * ptsPerStep

                    RowItem("Verified Steps ($ptsPerStep pts each)", "+$autoPoints pts")
                    RowItem("Skipped Steps", "0 pts")
                    RowItem("Wrong Action Penalty", if (wrongAttempts > 0) "-$wrongActionPenalty pts" else "0 pts")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(1.dp)
                            .background(EvalBorder)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", color = EvalTextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("$finalScore / 100", color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Areas to Improve & AI Feedback Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvalBorder, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = EvalCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Areas to Improve & AI Feedback",
                        color = EvalTextMain,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val feedbackItems = mutableListOf<String>()

                    if (wrongAttempts > 0) {
                        feedbackItems.add("• Sequence Precision: $wrongAttempts out-of-order action attempt(s) detected (-$wrongActionPenalty pts). Ensure air box lid clips are released before lifting element.")
                    } else {
                        feedbackItems.add("• Step Sequence: 100% correct operational sequence! Zero out-of-order action penalties recorded.")
                    }

                    if (totalSeconds < 60L) {
                        feedbackItems.add("• Practical Speed: Excellent execution time ($formattedTime). Efficient component inspection workflow.")
                    } else {
                        feedbackItems.add("• Practical Speed: Completed in $formattedTime. Practice clip unlatching to speed up practical service procedure.")
                    }

                    feedbackItems.add("• Component Inspection: Verify filter paper pleats against sunlight for invisible micro-tears before reinstalling.")

                    feedbackItems.forEach { item ->
                        Text(
                            text = item,
                            color = EvalTextMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvalBorder, RoundedCornerShape(6.dp)),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = EvalCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Score History & Past Attempts",
                                color = EvalTextMain,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${historyList.size} Saved",
                                color = EvalTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        historyList.take(5).forEachIndexed { idx, record ->
                            val scoreColor = when {
                                record.score >= 90 -> EvalGreen
                                record.score >= 70 -> EvalAmber
                                else -> EvalRed
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.taskName,
                                        color = EvalTextMain,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${record.dateText} · ${record.durationText}",
                                        color = EvalTextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(scoreColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${record.score}% (${record.grade})",
                                            color = scoreColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (idx < historyList.take(5).size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(EvalBorder)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = app.success,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(
                    text = "Retry Task",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Review Theory button
            Button(
                onClick = onReviewTheory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(
                    text = "Review Theory",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EvalTextMain)
            ) {
                Text(
                    text = "Finish Session",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun RowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}