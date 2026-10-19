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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autolearn.ar.model.Task

@Composable
fun TaskRecognitionScreen(
    task: Task,
    onContinue: () -> Unit
) {

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val successColor = MaterialTheme.colorScheme.secondary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // ==========================================
            // TOP SECTION
            // ==========================================

            Text(
                text = "Task recognition",
                color = accentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Practical Task Identified",
                color = textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "The system has matched the detected component with the appropriate practical task.",
                color = textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // ==========================================
            // RECOGNITION STATUS
            // ==========================================

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        successColor.copy(alpha = 0.12f),
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = successColor.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            successColor,
                            CircleShape
                        )
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "Task identified",
                    color = successColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ==========================================
            // TASK CARD
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "RECOGNIZED TASK",
                        color = textSecondary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = task.name,
                        color = textPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Color.White.copy(alpha = 0.08f)
                            )
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Text(
                        text = "Recognition Method",
                        color = textSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "AI-Based Component Recognition",
                        color = textPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // ==========================================
            // REQUIRED STEPS
            // ==========================================

            Text(
                text = "REQUIRED STEPS",
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // ==========================================
            // STEP CARDS
            // ==========================================

            task.steps.forEachIndexed { index, step ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // Step Number

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    accentColor.copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = accentColor.copy(alpha = 0.6f),
                                    shape = CircleShape
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "${index + 1}",
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.size(14.dp)
                        )


                        // Step Text

                        Text(
                            text = step,
                            color = textPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }


            // Push button toward bottom

            Spacer(
                modifier = Modifier.weight(1f)
            )


            // ==========================================
            // CONTINUE BUTTON
            // ==========================================

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Continue",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}