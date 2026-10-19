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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.theoryContentMap
import com.autolearn.ar.model.EngineComponent
import com.autolearn.ar.ui.theme.LocalAppColors

@Composable
fun ComponentScreen(
    component: EngineComponent,
    confidence: Float = 0.92f,
    onContinue: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val confidencePercent = (confidence.coerceIn(0f, 1f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(shapes.small)
                .background(app.success.copy(alpha = 0.14f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Component matched",
                style = type.labelMedium,
                color = app.success
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Component identified",
            style = type.headlineSmall,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Check the details before starting the practical.",
            style = type.bodyMedium,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.outline, shapes.medium),
            shape = shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val compImageResId = theoryContentMap[component.id]?.imageResId
                    if (compImageResId != null) {
                        Image(
                            painter = painterResource(id = compImageResId),
                            contentDescription = component.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(shapes.small)
                                .border(1.dp, colors.outline, shapes.small)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(shapes.small)
                                .background(colors.surfaceVariant)
                                .border(1.dp, colors.outline, shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Component",
                                tint = colors.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Engine part",
                            style = type.labelMedium,
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = component.name,
                            style = type.titleLarge,
                            color = colors.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colors.outline)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Model confidence",
                        style = type.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "$confidencePercent%",
                        style = type.titleLarge,
                        color = app.success
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { confidence.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(shapes.small),
                    color = app.success,
                    trackColor = colors.surfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                ComponentInfoSection(
                    icon = Icons.Default.Info,
                    title = "Function",
                    description = component.function
                )
                Spacer(modifier = Modifier.height(14.dp))
                ComponentInfoSection(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "Specifications",
                    description = component.specifications
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start practical", style = type.labelLarge)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "On-device vision · EngiScan",
            modifier = Modifier.fillMaxWidth(),
            style = type.bodySmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ComponentInfoSection(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(shapes.small)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = type.labelMedium,
                color = colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = type.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
    }
}
