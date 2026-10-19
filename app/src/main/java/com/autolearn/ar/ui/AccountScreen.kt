package com.autolearn.ar.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.AchievementBadge
import com.autolearn.ar.data.AchievementCatalog
import com.autolearn.ar.data.DailyGoalStore
import com.autolearn.ar.data.GoalStreak
import com.autolearn.ar.data.PreferencesManager
import com.autolearn.ar.data.ScoreRepository
import com.autolearn.ar.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AccountScreen() {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val context = LocalContext.current

    var badges by remember { mutableStateOf(emptyList<AchievementBadge>()) }
    var goalBadges by remember { mutableStateOf(emptyList<AchievementBadge>()) }
    var goalStreak by remember { mutableStateOf(GoalStreak(0, 0, 0)) }
    var spoken by remember { mutableStateOf(PreferencesManager.isSpokenEnabled(context)) }
    var confidence by remember { mutableFloatStateOf(PreferencesManager.confidenceThreshold(context)) }
    var sensitivity by remember { mutableIntStateOf(PreferencesManager.cameraSensitivity(context)) }

    LaunchedEffect(Unit) {
        val records = withContext(Dispatchers.IO) {
            DailyGoalStore.refreshFromHistory(context)
            ScoreRepository.getHistory(context)
        }
        badges = AchievementCatalog.evaluate(records)
        goalBadges = DailyGoalStore.badges(context)
        goalStreak = DailyGoalStore.streak(context)
    }

    val unlockedCount = badges.count { it.unlocked } + goalBadges.count { it.unlocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Account",
            style = type.headlineSmall,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer)
            ) {
                Text(
                    text = "A",
                    style = type.headlineSmall,
                    color = colors.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column {
                Text(
                    text = "Automotive trainee",
                    style = type.titleLarge,
                    color = colors.onBackground
                )
                Text(
                    text = "EngiScan practical student",
                    style = type.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$unlockedCount of ${badges.size + goalBadges.size} badges",
                    style = type.labelMedium,
                    color = app.success
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

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
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Goal streak",
                        style = type.titleSmall,
                        color = colors.onSurface
                    )
                    Text(
                        text = if (goalStreak.current == 1) {
                            "1 day in a row"
                        } else {
                            "${goalStreak.current} days in a row"
                        },
                        style = type.headlineSmall,
                        color = app.success
                    )
                    Text(
                        text = "Best ${goalStreak.best} · ${goalStreak.completedDays} full days",
                        style = type.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Learning goal badges",
            style = type.titleMedium,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unlock by finishing every daily goal. Streak counts consecutive full days.",
            style = type.bodySmall,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        goalBadges.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { badge ->
                    BadgeCard(
                        badge = badge,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Achievement badges",
            style = type.titleMedium,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unlock by completing practicals stored on this phone.",
            style = type.bodySmall,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        badges.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { badge ->
                    BadgeCard(
                        badge = badge,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Preferences",
            style = type.titleMedium,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.outline, shapes.medium),
            shape = shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spoken instructions",
                            style = type.titleSmall,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Read each practical step aloud.",
                            style = type.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = spoken,
                        onCheckedChange = {
                            spoken = it
                            PreferencesManager.setSpokenEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.onPrimary,
                            checkedTrackColor = colors.primary,
                            uncheckedThumbColor = colors.onSurfaceVariant,
                            uncheckedTrackColor = colors.surfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Recognition confidence",
                    style = type.titleSmall,
                    color = colors.onSurface
                )
                Text(
                    text = "Component scan must reach ${(confidence * 100).toInt()}% before it is accepted.",
                    style = type.bodySmall,
                    color = colors.onSurfaceVariant
                )
                Slider(
                    value = confidence,
                    onValueChange = { confidence = it },
                    onValueChangeFinished = {
                        PreferencesManager.setConfidenceThreshold(context, confidence)
                    },
                    valueRange = 0.45f..0.85f,
                    steps = 7,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Camera sensitivity",
                    style = type.titleSmall,
                    color = colors.onSurface
                )
                Text(
                    text = "How readily live actions are accepted during a practical.",
                    style = type.bodySmall,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0, 1, 2).forEach { level ->
                        val selected = sensitivity == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shapes.medium)
                                .background(
                                    if (selected) colors.primaryContainer else colors.surfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (selected) colors.primary else colors.outline,
                                    shapes.medium
                                )
                                .clickable {
                                    sensitivity = level
                                    PreferencesManager.setCameraSensitivity(context, level)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = PreferencesManager.sensitivityLabel(level),
                                style = type.labelLarge,
                                color = if (selected) colors.onPrimaryContainer else colors.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badge: AchievementBadge,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    Card(
        modifier = modifier
            .border(1.dp, colors.outline, shapes.medium)
            .alpha(if (badge.unlocked) 1f else 0.55f),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(shapes.small)
                    .background(
                        if (badge.unlocked) app.success.copy(alpha = 0.16f)
                        else colors.surfaceVariant
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (badge.unlocked) "Earned" else "Locked",
                    style = type.labelSmall,
                    color = if (badge.unlocked) app.success else colors.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.title,
                style = type.titleSmall,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge.description,
                style = type.bodySmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}
