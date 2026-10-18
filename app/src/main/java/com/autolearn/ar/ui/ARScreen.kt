
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.autolearn.ar.model.Task
import java.util.Locale

@Composable
fun ARScreen(
    task: Task,
    onFinish: () -> Unit
) {

    var currentStep by remember {
        mutableIntStateOf(0)
    }

    var detectedAction by remember {
        mutableStateOf("Waiting...")
    }

    var confidence by remember {
        mutableFloatStateOf(0f)
    }

    var actionStatus by remember {
        mutableStateOf("Waiting for student action")
    }

    /*
     * IMPORTANT:
     * These labels must exactly match action_labels.txt
     * and the labels used during model training.
     */
    val expectedActions = listOf(
        "air_filter_closed",
        "air_filter_inspecting",
        "air_filter_opening",
        "air_filter_removing",
        "air_filter_reinstalling"
    )

    val recentPredictions = remember {
        mutableStateListOf<String>()
    }

    val requiredStablePredictions = 5

    val confidenceThreshold = 0.70f

    val totalSteps = task.steps.size

    val expectedAction =
        expectedActions.getOrElse(currentStep) {
            ""
        }

    val currentInstruction =
        task.steps.getOrElse(currentStep) {
            task.steps.firstOrNull().orEmpty()
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==========================================
        // LIVE CAMERA + ACTION RECOGNITION
        // ==========================================

        CameraPreview(
            modifier = Modifier.fillMaxSize(),

            onPrediction = { prediction ->

                detectedAction = prediction.label
                confidence = prediction.confidence

                /*
                 * Ignore low-confidence predictions.
                 */
                if (
                    prediction.confidence <
                    confidenceThreshold
                ) {

                    actionStatus =
                        "Action unclear - keep performing the step"

                    return@CameraPreview
                }

                /*
                 * Store only high-confidence predictions.
                 */
                recentPredictions.add(
                    prediction.label
                )

                /*
                 * Keep only the latest predictions.
                 */
                if (
                    recentPredictions.size >
                    requiredStablePredictions
                ) {

                    recentPredictions.removeAt(0)
                }

                /*
                 * Check whether all recent predictions
                 * are the same.
                 */
                val isStable =
                    recentPredictions.size ==
                            requiredStablePredictions &&
                            recentPredictions.all {
                                it == prediction.label
                            }

                if (isStable) {

                    if (
                        prediction.label ==
                        expectedAction
                    ) {

                        actionStatus =
                            "✓ CORRECT ACTION DETECTED"

                    } else {

                        actionStatus =
                            "✗ DIFFERENT ACTION DETECTED"
                    }

                } else {

                    actionStatus =
                        "Analyzing action..."
                }
            }
        )


        // ==========================================
        // TOP TITLE
        // ==========================================

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 35.dp)
                .background(
                    Color.Black.copy(alpha = 0.75f),
                    RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
        ) {

            Text(
                text = "LIVE PRACTICAL GUIDANCE",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }


        // ==========================================
        // LIVE ACTION DETECTION PANEL
        // ==========================================

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = 95.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.82f),
                    RoundedCornerShape(16.dp)
                )
                .padding(14.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "LIVE ACTION RECOGNITION",
                color = Color.LightGray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Detected: ${
                        formatActionLabel(
                            detectedAction
                        )
                    }",

                color = Color.Green,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Confidence: %.1f%%",
                    confidence * 100f
                ),

                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Expected: ${
                        formatActionLabel(
                            expectedAction
                        )
                    }",

                color = Color.Yellow
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = actionStatus,

                color =
                    when {

                        actionStatus.startsWith("✓") ->
                            Color.Green

                        actionStatus.startsWith("✗") ->
                            Color.Red

                        else ->
                            Color.White
                    },

                style =
                    MaterialTheme.typography.bodyLarge
            )
        }


        // ==========================================
        // TARGET BOX
        // ==========================================

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp)
                .border(
                    width = 3.dp,
                    color = Color.Green,
                    shape = RoundedCornerShape(20.dp)
                )
        )


        // ==========================================
        // BOTTOM GUIDANCE PANEL
        // ==========================================

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.88f)
                )
                .padding(20.dp)
        ) {

            Text(
                text = task.name,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "STEP ${currentStep + 1} / $totalSteps",

                color = Color.Green,
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = currentInstruction,
                color = Color.White,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ======================================
            // STEP NAVIGATION
            // ======================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {

                        if (
                            currentStep > 0
                        ) {

                            currentStep--

                            recentPredictions.clear()

                            actionStatus =
                                "Waiting for student action"
                        }
                    },

                    enabled =
                        currentStep > 0,

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text("Previous")
                }


                OutlinedButton(
                    onClick = {

                        if (
                            currentStep <
                            totalSteps - 1
                        ) {

                            currentStep++

                            recentPredictions.clear()

                            actionStatus =
                                "Waiting for student action"

                        } else {

                            onFinish()
                        }
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        if (
                            currentStep ==
                            totalSteps - 1
                        ) {
                            "Complete"
                        } else {
                            "Next Step"
                        }
                    )
                }
            }
        }
    }
}


/**
 * Convert model label into readable text.
 *
 * air_filter_opening
 * ->
 * Air Filter Opening
 */
private fun formatActionLabel(
    label: String
): String {

    if (
        label == "Waiting..."
    ) {
        return label
    }

    return label
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->

            word.replaceFirstChar { character ->
                character.uppercase()
            }
        }
}

