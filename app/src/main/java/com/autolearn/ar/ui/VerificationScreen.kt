package com.autolearn.ar.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.autolearn.ar.ai.Spatial3DEngine
import com.autolearn.ar.data.InProgressSession
import com.autolearn.ar.data.InProgressStore
import com.autolearn.ar.data.PreferencesManager
import com.autolearn.ar.data.PracticalJob
import com.autolearn.ar.data.practicalJobsList
import com.autolearn.ar.model.Task
import kotlinx.coroutines.delay
import java.util.Locale

private val EngiBg = Color(0xFF1B2734)
private val EngiCardBg = Color(0xFF2D3F54)
private val EngiGold = Color(0xFF3C93FA)
private val EngiGoldBright = Color(0xFF00D4C6)
private val EngiTextMain = Color(0xFFF2F5F8)
private val EngiTextMuted = Color(0xFFA8B4C4)
private val EngiPurpleProgress = Color(0xFF004C94)

@Composable
fun VerificationScreen(
    task: Task,
    completedSteps: List<Boolean>,
    initialStepIndex: Int = 0,
    initialVerifiedSteps: List<Boolean> = emptyList(),
    onComplete: (
        List<Boolean>,
        Int,
        Long,
        Int,
        Set<Int>
    ) -> Unit,
    onLeave: () -> Unit = {}
) {
    val context = LocalContext.current
    val spokenEnabled = remember { PreferencesManager.isSpokenEnabled(context) }
    val actionMinConfidence = remember { PreferencesManager.actionMinConfidence(context) }

    // Find detailed practical job data matching task
    val job = remember(task.id, task.componentId) {
        practicalJobsList.firstOrNull { jobItem ->
            jobItem.id.equals(task.id, ignoreCase = true) ||
            jobItem.id.contains(task.componentId, ignoreCase = true) ||
            task.componentId.contains(jobItem.id.replace("_service", "").replace("_inspection", "").replace("_replacement", "").replace("_change", ""), ignoreCase = true) ||
            jobItem.componentLocationText.contains(task.name, ignoreCase = true) ||
            task.name.contains(jobItem.title, ignoreCase = true) ||
            jobItem.title.contains(task.name, ignoreCase = true)
        } ?: practicalJobsList.first()
    }

    var currentStepIndex by remember(task.id) {
        mutableIntStateOf(initialStepIndex.coerceIn(0, (job.steps.size - 1).coerceAtLeast(0)))
    }
    var currentActionPrediction by remember { mutableStateOf<TFLiteClassifier.Prediction?>(null) }
    val totalSteps = job.steps.size

    val stepVerifiedState = remember(task.id) {
        mutableStateListOf(
            *Array(totalSteps) { index -> initialVerifiedSteps.getOrElse(index) { false } }
        )
    }
    var sessionFinished by remember { mutableStateOf(false) }
    val pauseSnapshot = remember { PauseSnapshot() }
    pauseSnapshot.taskId = task.id
    pauseSnapshot.componentId = task.componentId
    pauseSnapshot.title = job.title
    pauseSnapshot.stepIndex = currentStepIndex
    pauseSnapshot.totalSteps = totalSteps
    pauseSnapshot.verified = stepVerifiedState.toList()
    pauseSnapshot.finished = sessionFinished

    val lifecycleOwner = LocalLifecycleOwner.current

    var wrongAttemptsCount by remember { mutableIntStateOf(0) }
    var lastPenaltyTimeMillis by remember { mutableLongStateOf(0L) }
    var lastVerifiedStepTimeMillis by remember { mutableLongStateOf(0L) }
    var isAutoVerifyingStep by remember { mutableStateOf(false) }

    // 5-Second Continuous Action Hold Verification State
    var actionHoldStartMillis by remember { mutableStateOf<Long?>(null) }
    var actionHoldProgress by remember { mutableFloatStateOf(0f) }
    // Dynamic Performance Scoring Metrics State
    var resetCount by remember { mutableIntStateOf(0) }
    var confidenceSum by remember { mutableFloatStateOf(0f) }
    var confidenceCount by remember { mutableIntStateOf(0) }
    var lastFrameTimeMillis by remember { mutableLongStateOf(0L) }
    var activePredictionLabel by remember { mutableStateOf("") }
    var activePredictionConf by remember { mutableFloatStateOf(0f) }

    val currentStep = job.steps.getOrElse(currentStepIndex) { job.steps.first() }
    val startTime = remember { System.currentTimeMillis() }

    // TTS Setup
    var isTtsReady by remember { mutableStateOf(false) }
    val tts = remember {
        TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                tts.stop()
                tts.shutdown()
            }.onFailure { error ->
                Log.e("VerificationScreen", "tts shutdown failed", error)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                persistPausedPractical(context, pauseSnapshot, notify = false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            persistPausedPractical(context, pauseSnapshot, notify = false)
        }
    }

    BackHandler {
        persistPausedPractical(context, pauseSnapshot, notify = false)
        onLeave()
    }

    val leaveMidTask = {
        persistPausedPractical(context, pauseSnapshot, notify = false)
        onLeave()
    }

    // Speak directive when step changes
    LaunchedEffect(currentStepIndex, isTtsReady) {
        if (isTtsReady && spokenEnabled) {
            tts.speak(
                "${currentStep.shortTitle}. ${currentStep.directiveInstruction}",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "STEP_$currentStepIndex"
            )
        }
    }

    // Dedicated QA 3-Second Action Verification & Auto-Advancement Loop
    LaunchedEffect(currentStepIndex) {
        actionHoldStartMillis = null
        actionHoldProgress = 0f

        while (true) {
            val now = System.currentTimeMillis()
            val hasRecentFrame = (now - lastFrameTimeMillis < 1500L) && (activePredictionConf >= actionMinConfidence)

            val predictedStep = mapActionToStepIndex(activePredictionLabel, task.componentId)
            val isAlreadyVerified = if (predictedStep in 0 until totalSteps) stepVerifiedState[predictedStep] else false

            // Accept action ONLY if it is for the active step AND not already verified
            val isActionMatchingCurrentStep = !isAlreadyVerified && (
                predictedStep == currentStepIndex ||
                isExpectedActionForStep(task.componentId, currentStepIndex, activePredictionLabel)
            )

            if (hasRecentFrame && isActionMatchingCurrentStep) {
                if (actionHoldStartMillis == null) {
                    actionHoldStartMillis = now
                }

                val elapsed = now - (actionHoldStartMillis ?: now)
                val progress = (elapsed / 3000f).coerceIn(0f, 1f)
                actionHoldProgress = progress

                if (elapsed >= 3000L) {
                    // Action for Step currentStepIndex successfully held for 3 FULL SECONDS!
                    stepVerifiedState[currentStepIndex] = true
                    isAutoVerifyingStep = true

                    if (isTtsReady && spokenEnabled) {
                        tts.speak("Step ${currentStepIndex + 1} verified", TextToSpeech.QUEUE_FLUSH, null, "VERIFIED_$currentStepIndex")
                    }

                    delay(800L)
                    isAutoVerifyingStep = false
                    actionHoldStartMillis = null
                    actionHoldProgress = 0f

                    if (currentStepIndex >= totalSteps - 1 || stepVerifiedState.all { it }) {
                        // Mark all 4 steps verified
                        for (i in 0 until totalSteps) {
                            stepVerifiedState[i] = true
                        }
                        sessionFinished = true
                        pauseSnapshot.finished = true
                        InProgressStore.finish(context)
                        val duration = System.currentTimeMillis() - startTime
                        val totalSecs = (duration / 1000L).coerceAtLeast(1L)
                        val verifiedCount = stepVerifiedState.count { it }

                        // 4-Parameter Dynamic Scoring Calculation Formula
                        val completionScore = (verifiedCount.toFloat() / totalSteps.toFloat()) * 40f
                        val timePenalty = ((totalSecs - 30L).coerceAtLeast(0L) * 0.4f)
                        val timeScore = (25f - timePenalty).coerceIn(5f, 25f)
                        val avgConf = if (confidenceCount > 0) (confidenceSum / confidenceCount.toFloat()) else 0.85f
                        val stabilityScore = (avgConf * 20f).coerceIn(10f, 20f)
                        val precisionScore = (15f - (resetCount * 1.5f)).coerceIn(0f, 15f)

                        val calculatedScore = (completionScore + timeScore + stabilityScore + precisionScore)
                            .toInt()
                            .coerceIn(20, 100)

                        onComplete(
                            stepVerifiedState.toList(),
                            resetCount,
                            duration,
                            calculatedScore,
                            emptySet()
                        )
                        break
                    } else {
                        // Automatically advance to the next step!
                        currentStepIndex++
                        pauseSnapshot.stepIndex = currentStepIndex
                        pauseSnapshot.verified = stepVerifiedState.toList()
                        persistPausedPractical(context, pauseSnapshot, notify = false)
                        break
                    }
                }
            } else {
                // Action lost or wrong action — reset timer & track reset penalty
                if (actionHoldStartMillis != null) {
                    resetCount++
                }
                actionHoldStartMillis = null
                actionHoldProgress = 0f
            }

            delay(50L)
        }
    }

    // AR Arrow pulsing animation
    val infiniteTransition = rememberInfiniteTransition()
    val animPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EngiBg)
            .statusBarsPadding()
    ) {

        // ==========================================
        // 1. TOP HEADER & PROGRESS BAR
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EngiBg)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${job.title}  ·  Task ${currentStepIndex + 1} of $totalSteps",
                    color = EngiTextMain,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "✕ LEAVE",
                    color = EngiTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { leaveMidTask() }
                        .padding(vertical = 2.dp, horizontal = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Thin top progress bar across header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(EngiPurpleProgress)
            ) {
                val progressFraction = (stepVerifiedState.count { it }.toFloat() / totalSteps.toFloat()).coerceIn(0.1f, 1.0f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .background(if (isAutoVerifyingStep) Color(0xFF73CC80) else EngiGold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Directive instruction line
            Text(
                text = currentStep.directiveInstruction,
                color = EngiTextMain,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }

        // ==========================================
        // 2. FULL SCREEN LIVE CAMERA + AR OVERLAY
        // ==========================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .background(Color(0xFF111823))
        ) {

            // Live Camera Preview
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onPrediction = { prediction ->
                    currentActionPrediction = prediction
                    lastFrameTimeMillis = System.currentTimeMillis()
                    activePredictionLabel = prediction.label
                    activePredictionConf = prediction.confidence
                    if (prediction.confidence >= actionMinConfidence) {
                        confidenceSum += prediction.confidence
                        confidenceCount++
                    }
                }
            )

            // Futuristic Interactive AR Overlay Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val targetPos = Spatial3DEngine.project3DTo2D(
                    point = currentStep.target3D,
                    screenWidth = w,
                    screenHeight = h
                )

                val ringColor = when {
                    isAutoVerifyingStep -> Color(0xFF73CC80)
                    actionHoldStartMillis != null -> Color(0xFF00D4C6)
                    else -> Color(0xFF3C93FA)
                }

                // 1. Futuristic Bounding Box Corner Brackets around component target
                val boxSize = 48.dp.toPx()
                val cornerLen = 14.dp.toPx()
                val left = targetPos.x - boxSize
                val top = targetPos.y - boxSize
                val right = targetPos.x + boxSize
                val bottom = targetPos.y + boxSize

                // Top-Left Corner Bracket
                drawLine(ringColor.copy(alpha = 0.7f), Offset(left, top), Offset(left + cornerLen, top), 2.5.dp.toPx())
                drawLine(ringColor.copy(alpha = 0.7f), Offset(left, top), Offset(left, top + cornerLen), 2.5.dp.toPx())

                // Top-Right Corner Bracket
                drawLine(ringColor.copy(alpha = 0.7f), Offset(right, top), Offset(right - cornerLen, top), 2.5.dp.toPx())
                drawLine(ringColor.copy(alpha = 0.7f), Offset(right, top), Offset(right, top + cornerLen), 2.5.dp.toPx())

                // Bottom-Left Corner Bracket
                drawLine(ringColor.copy(alpha = 0.7f), Offset(left, bottom), Offset(left + cornerLen, bottom), 2.5.dp.toPx())
                drawLine(ringColor.copy(alpha = 0.7f), Offset(left, bottom), Offset(left, bottom - cornerLen), 2.5.dp.toPx())

                // Bottom-Right Corner Bracket
                drawLine(ringColor.copy(alpha = 0.7f), Offset(right, bottom), Offset(right - cornerLen, bottom), 2.5.dp.toPx())
                drawLine(ringColor.copy(alpha = 0.7f), Offset(right, bottom), Offset(right, bottom - cornerLen), 2.5.dp.toPx())

                // 2. Pulsing Dashed Outer AR Ring
                drawCircle(
                    color = ringColor.copy(alpha = 0.85f),
                    radius = 28.dp.toPx() + (animPulse * 5.dp.toPx()),
                    center = targetPos,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), animPulse * 20f)
                    )
                )

                // 3. 5-Second Circular Progress Arc Around Target Ring
                if (actionHoldStartMillis != null && actionHoldProgress > 0f) {
                    val sweepAngle = actionHoldProgress * 360f
                    drawArc(
                        color = Color(0xFF00D4C6),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(targetPos.x - 34.dp.toPx(), targetPos.y - 34.dp.toPx()),
                        size = Size(68.dp.toPx(), 68.dp.toPx()),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 4. Center Target Dot
                drawCircle(
                    color = ringColor,
                    radius = 8.dp.toPx(),
                    center = targetPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = targetPos
                )

                // 5. AR Golden Pointer Arrow pointing to target
                val arrowStart = Offset(targetPos.x + 50.dp.toPx(), targetPos.y - 75.dp.toPx() + (animPulse * 8.dp.toPx()))
                val arrowEnd = Offset(targetPos.x + 14.dp.toPx(), targetPos.y - 18.dp.toPx() + (animPulse * 4.dp.toPx()))

                val arrowPath = Path().apply {
                    moveTo(arrowStart.x, arrowStart.y)
                    lineTo(arrowEnd.x, arrowEnd.y)
                }

                drawPath(
                    path = arrowPath,
                    color = if (isAutoVerifyingStep) Color(0xFF73CC80) else EngiGoldBright,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                val tipPath = Path().apply {
                    moveTo(arrowEnd.x, arrowEnd.y)
                    lineTo(arrowEnd.x + 14.dp.toPx(), arrowEnd.y - 4.dp.toPx())
                    lineTo(arrowEnd.x + 4.dp.toPx(), arrowEnd.y - 14.dp.toPx())
                    close()
                }
                drawPath(path = tipPath, color = if (isAutoVerifyingStep) Color(0xFF73CC80) else EngiGoldBright)

                // 6. Expanding Shockwave Burst on Verification Success
                if (isAutoVerifyingStep) {
                    drawCircle(
                        color = Color(0xFF73CC80).copy(alpha = 0.4f),
                        radius = 45.dp.toPx() + (animPulse * 15.dp.toPx()),
                        center = targetPos,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // Top AI Action Verification Status Overlay Badge + 5-Second Hold Progress
            currentActionPrediction?.let { pred ->
                val targetActionStep = mapActionToStepIndex(pred.label, task.componentId)
                val isActionVerified = isExpectedActionForStep(task.componentId, currentStepIndex, pred.label) || targetActionStep == currentStepIndex
                val formattedLabel = pred.label.replace("_", " ").uppercase(Locale.getDefault())
                val confPercent = (pred.confidence * 100).toInt()

                val elapsedSec = String.format(Locale.US, "%.1f", actionHoldProgress * 3.0f)
                val badgeColor = when {
                    isAutoVerifyingStep -> Color(0xFF73CC80)
                    actionHoldStartMillis != null -> Color(0xFF00D4C6)
                    isActionVerified -> EngiGold
                    else -> Color(0xFF94A3B8)
                }

                val badgeText = when {
                    isAutoVerifyingStep -> "STEP ${currentStepIndex + 1} VERIFIED ✓: $formattedLabel ($confPercent%)"
                    actionHoldStartMillis != null -> "HOLDING ACTION: ${elapsedSec}s / 3.0s ($confPercent%)"
                    isActionVerified -> "DETECTED: $formattedLabel ($confPercent%) — HOLD FOR 3s"
                    else -> "ACTION: $formattedLabel ($confPercent%)"
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xEE0F1520))
                            .border(
                                width = 1.dp,
                                color = badgeColor,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(badgeColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = badgeText,
                                color = EngiTextMain,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 5-Second Action Hold Linear Progress Bar
                    if (actionHoldStartMillis != null && actionHoldProgress > 0f && !isAutoVerifyingStep) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(220.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xAA1F2937))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(actionHoldProgress)
                                    .background(Color(0xFF00D4C6))
                            )
                        }
                    }
                }
            }

            // Floating Mini Task Checklist Overlay (Bottom-left corner)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xDD1B2734))
                    .border(1.dp, Color(0xFF52647A), RoundedCornerShape(6.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                job.steps.forEachIndexed { idx, stepItem ->
                    val isCurrent = idx == currentStepIndex
                    val isVerified = stepVerifiedState.getOrElse(idx) { false }
                    val stepColor = when {
                        isVerified -> Color(0xFF73CC80)
                        isCurrent -> EngiGold
                        else -> EngiTextMuted
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isVerified) "✓  " else "•  ",
                            color = stepColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${idx + 1}. ${stepItem.shortTitle}",
                            color = stepColor,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent || isVerified) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

private class PauseSnapshot {
    var taskId: String = ""
    var componentId: String = ""
    var title: String = ""
    var stepIndex: Int = 0
    var totalSteps: Int = 1
    var verified: List<Boolean> = emptyList()
    var finished: Boolean = false

    fun toSession(): InProgressSession {
        val total = totalSteps.coerceAtLeast(1)
        return InProgressSession(
            taskId = taskId,
            componentId = componentId,
            title = title,
            currentStepIndex = stepIndex.coerceIn(0, total - 1),
            totalSteps = total,
            verifiedSteps = verified
        )
    }
}

private fun persistPausedPractical(
    context: android.content.Context,
    snapshot: PauseSnapshot,
    notify: Boolean = false
) {
    if (snapshot.finished || snapshot.taskId.isBlank()) return
    val session = snapshot.toSession()
    if (notify) {
        InProgressStore.pauseAndNotify(context, session)
    } else {
        InProgressStore.save(context, session)
    }
}

// ==========================================
// HELPER FUNCTIONS FOR ACTION MATCHING
// ==========================================

private fun mapActionToStepIndex(predictedLabel: String, componentId: String = ""): Int {
    val cleanLabel = predictedLabel.trim().lowercase()
    val cleanComp = componentId.trim().lowercase()

    // 1. Air Filter (action_model.tflite)
    if (cleanComp.contains("air_filter") || cleanComp.contains("filter")) {
        return when {
            cleanLabel == "air_filter_closed" || cleanLabel.contains("closed") || cleanLabel.contains("housing") || cleanLabel.contains("locate") -> 0
            cleanLabel == "cover_open" || cleanLabel.contains("cover") || cleanLabel.contains("open") || cleanLabel.contains("clip") || cleanLabel.contains("unlatch") -> 1
            cleanLabel == "inspection" || cleanLabel.contains("inspect") || cleanLabel.contains("pleat") || cleanLabel.contains("element") -> 2
            cleanLabel == "reinstall" || cleanLabel.contains("reinstall") || cleanLabel.contains("seat") || cleanLabel.contains("close") || cleanLabel.contains("latch") -> 3
            else -> -1
        }
    }

    // 2. Battery Terminal Change (battery_action_model.tflite)
    if (cleanComp.contains("battery") || cleanComp.contains("terminal")) {
        return when {
            cleanLabel == "battery_connected" || cleanLabel.contains("connected") || cleanLabel.contains("attached") || cleanLabel.contains("locate") -> 0
            cleanLabel == "negative_unclamped" || cleanLabel.contains("negative") || cleanLabel.contains("unclamp") || cleanLabel.contains("black") -> 1
            cleanLabel == "positive_unclamped" || cleanLabel.contains("positive") || cleanLabel.contains("unbolt") || cleanLabel.contains("red") -> 2
            cleanLabel == "battery_removed" || cleanLabel.contains("removed") || cleanLabel.contains("lifted") || cleanLabel.contains("casing") -> 3
            else -> -1
        }
    }

    // 3. Spark Plug Replacement (spark_plug_action_model.tflite)
    if (cleanComp.contains("spark") || cleanComp.contains("plug")) {
        return when {
            cleanLabel == "ignition_boot_on" || cleanLabel.contains("ignition") || cleanLabel.contains("boot_on") || cleanLabel.contains("coil") -> 0
            cleanLabel == "boot_removed" || cleanLabel.contains("boot_removed") || cleanLabel.contains("disconnected") || cleanLabel.contains("pull") -> 1
            cleanLabel == "plug_loosened" || cleanLabel.contains("loosened") || cleanLabel.contains("socket") || cleanLabel.contains("wrench") -> 2
            cleanLabel == "new_plug_seated" || cleanLabel.contains("new_plug") || cleanLabel.contains("seated") || cleanLabel.contains("threaded") -> 3
            else -> -1
        }
    }

    // 4. Engine Oil Dipstick Inspection (dipstick_action_model.tflite)
    if (cleanComp.contains("dipstick") || cleanComp.contains("oil")) {
        return when {
            cleanLabel == "dipstick_inserted" || cleanLabel.contains("inserted") || cleanLabel.contains("handle") || cleanLabel.contains("seated") -> 0
            cleanLabel == "dipstick_pulled" || cleanLabel.contains("pulled") || cleanLabel.contains("withdrawn") -> 1
            cleanLabel == "wiped_clean" || cleanLabel.contains("wiped") || cleanLabel.contains("clean") || cleanLabel.contains("rag") -> 2
            cleanLabel == "level_inspected" || cleanLabel.contains("level") || cleanLabel.contains("inspected") || cleanLabel.contains("min") || cleanLabel.contains("max") -> 3
            else -> -1
        }
    }

    // Direct Class Name Fallback Matching
    return when (cleanLabel) {
        "air_filter_closed", "battery_connected", "ignition_boot_on", "dipstick_inserted" -> 0
        "cover_open", "negative_unclamped", "boot_removed", "dipstick_pulled" -> 1
        "inspection", "positive_unclamped", "plug_loosened", "wiped_clean" -> 2
        "reinstall", "battery_removed", "new_plug_seated", "level_inspected" -> 3
        else -> -1
    }
}

private fun isExpectedActionForStep(
    componentId: String,
    stepIndex: Int,
    predictedLabel: String
): Boolean {
    val mappedStep = mapActionToStepIndex(predictedLabel, componentId)
    return mappedStep == stepIndex
}

private fun isKnownActionForTask(
    predictedLabel: String
): Boolean {
    val clean = predictedLabel.trim().lowercase()
    return clean == "air_filter_closed" || clean == "cover_open" || clean == "inspection" || clean == "reinstall" ||
           clean == "battery_terminal" || clean == "spark_plug" || clean == "engine_oil_dipstick"
}