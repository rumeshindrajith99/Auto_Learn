package com.autolearn.ar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autolearn.ar.model.Task

@Composable
fun TaskScreen(
    task: Task,
    onStartTask: () -> Unit
) {

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryBlue = MaterialTheme.colorScheme.primary
    val successGreen = MaterialTheme.colorScheme.secondary
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ==========================================
        // TOP STATUS
        // ==========================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        successGreen.copy(alpha = 0.18f)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
            ) {

                Text(
                    text = "READY FOR PRACTICAL",
                    color = successGreen,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // ==========================================
        // TITLE
        // ==========================================

        Text(
            text = "Practical Task",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = task.name,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "${task.steps.size} steps to complete",
            color = secondaryText,
            style = MaterialTheme.typography.bodyMedium
        )


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        // ==========================================
        // INSTRUCTIONS CARD
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "TASK INSTRUCTIONS",
                    color = primaryBlue,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                task.steps.forEachIndexed { index, step ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {

                        // Step Number

                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                                .clip(CircleShape)
                                .background(
                                    primaryBlue.copy(alpha = 0.18f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "${index + 1}",
                                color = primaryBlue,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }


                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )


                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = step,
                                color = Color.White.copy(
                                    alpha = 0.92f
                                ),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }


                    if (index != task.steps.lastIndex) {

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // ==========================================
        // INFO CARD
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111827)
            )
        ) {

            Text(
                text =
                    "Live guidance will use the camera to help verify your actions in real time.",
                color = secondaryText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }


        Spacer(
            modifier = Modifier.weight(1f)
        )


        // ==========================================
        // START BUTTON
        // ==========================================

        Button(
            onClick = onStartTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryBlue,
                contentColor = Color.White
            )
        ) {

            Text(
                text = "START LIVE GUIDANCE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "Camera • AR Guidance • Action Detection",
            color = secondaryText,
            style = MaterialTheme.typography.labelSmall
        )
    }
}