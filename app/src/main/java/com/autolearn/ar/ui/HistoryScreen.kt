package com.autolearn.ar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.ScoreRecord
import com.autolearn.ar.data.ScoreRepository
import com.autolearn.ar.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HistoryScreen() {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val context = LocalContext.current

    var records by remember { mutableStateOf<List<ScoreRecord>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        records = withContext(Dispatchers.IO) {
            ScoreRepository.getHistory(context)
        }
        loaded = true
    }

    val best = records.maxByOrNull { it.score }?.score
    val average = if (records.isEmpty()) null else records.map { it.score }.average().toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Results",
            style = type.headlineSmall,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Saved practical scores from this device.",
            style = type.bodyMedium,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryChip(
                modifier = Modifier.weight(1f),
                label = "Attempts",
                value = if (loaded) records.size.toString() else "—"
            )
            SummaryChip(
                modifier = Modifier.weight(1f),
                label = "Best",
                value = best?.toString() ?: "—"
            )
            SummaryChip(
                modifier = Modifier.weight(1f),
                label = "Average",
                value = average?.toString() ?: "—"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (loaded && records.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline, shapes.medium),
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No scores yet",
                        style = type.titleMedium,
                        color = colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Finish a practical sheet and the score will be stored on this phone.",
                        style = type.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(records, key = { "${it.dateText}-${it.taskName}-${it.score}-${it.durationText}" }) { record ->
                    val scoreColor = when {
                        record.score >= 90 -> app.success
                        record.score >= 70 -> colors.secondary
                        else -> colors.error
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.outline, shapes.medium),
                        shape = shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.taskName,
                                    style = type.titleSmall,
                                    color = colors.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = record.dateText,
                                    style = type.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                                Text(
                                    text = "Duration ${record.durationText}",
                                    style = type.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${record.score}",
                                    style = type.headlineSmall,
                                    color = scoreColor
                                )
                                Text(
                                    text = "Grade ${record.grade}",
                                    style = type.labelSmall,
                                    color = scoreColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(
    modifier: Modifier,
    label: String,
    value: String
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    Card(
        modifier = modifier.border(1.dp, colors.outline, shapes.medium),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Text(text = label, style = type.labelSmall, color = colors.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = type.titleMedium, color = colors.onSurface)
        }
    }
}
