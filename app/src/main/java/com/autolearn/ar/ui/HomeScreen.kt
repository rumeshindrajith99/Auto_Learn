package com.autolearn.ar.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.autolearn.ar.data.CompetencyStatus
import com.autolearn.ar.data.DailyGoal
import com.autolearn.ar.data.DailyGoalAction
import com.autolearn.ar.data.DailyGoalStore
import com.autolearn.ar.data.GoalStreak
import com.autolearn.ar.data.InProgressSession
import com.autolearn.ar.data.PracticalJob
import com.autolearn.ar.data.PreferencesManager
import com.autolearn.ar.data.ResumeNotifier
import com.autolearn.ar.data.practicalJobsList
import com.autolearn.ar.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

@Composable
fun HomeScreen(
    onStartJob: (PracticalJob) -> Unit = {},
    onScanComponent: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    pausedSession: InProgressSession? = null,
    onResumePaused: () -> Unit = {},
    onDismissPaused: () -> Unit = {},
    isSpokenInstructionsEnabled: Boolean = true,
    onToggleSpokenInstructions: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var spokenEnabled by remember {
        mutableStateOf(PreferencesManager.isSpokenEnabled(context))
    }
    var dailyGoals by remember { mutableStateOf(DailyGoalStore.goals(context)) }
    var goalStreak by remember { mutableStateOf(DailyGoalStore.streak(context)) }
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }
    LaunchedEffect(pausedSession) {
        dailyGoals = withContext(Dispatchers.IO) {
            DailyGoalStore.refreshFromHistory(context)
        }
        goalStreak = DailyGoalStore.streak(context)
        if (pausedSession == null) return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !ResumeNotifier.canNotify(context)) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    val competent = practicalJobsList.count { it.status == CompetencyStatus.COMPETENT }
    val nextJob = practicalJobsList.firstOrNull { it.status != CompetencyStatus.COMPETENT }
        ?: practicalJobsList.firstOrNull()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = greeting,
            style = type.bodyMedium,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Engine servicing",
            style = type.headlineSmall,
            color = colors.onBackground
        )
        Text(
            text = "Practical coursework · Unit 04",
            style = type.bodySmall,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pausedSession != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.primary, shapes.medium),
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Practical paused",
                        style = type.labelMedium,
                        color = colors.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pausedSession.title,
                        style = type.titleMedium,
                        color = colors.onPrimaryContainer
                    )
                    Text(
                        text = "${pausedSession.stepLabel} saved on this phone. Resume to continue.",
                        style = type.bodySmall,
                        color = colors.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onResumePaused,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(text = "Resume practical", style = type.labelLarge)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Discard",
                        style = type.labelMedium,
                        color = colors.onPrimaryContainer,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable(onClick = onDismissPaused)
                            .padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        DailyGoalsCard(
            goals = dailyGoals,
            streak = goalStreak,
            onOpen = { goal ->
                when (goal.action) {
                    DailyGoalAction.SCAN -> onScanComponent()
                    DailyGoalAction.THEORY -> onOpenLibrary()
                    DailyGoalAction.PRACTICAL -> {
                        if (pausedSession != null) {
                            onResumePaused()
                        } else {
                            nextJob?.let(onStartJob)
                        }
                    }
                    DailyGoalAction.FOCUS_JOB -> {
                        val job = practicalJobsList.firstOrNull { it.id == goal.jobId }
                        if (job != null) onStartJob(job)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.outline, shapes.medium),
            shape = shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            var moduleExpanded by remember { mutableStateOf(false) }
            val arrowRotation by animateFloatAsState(
                targetValue = if (moduleExpanded) 180f else 0f,
                label = "moduleArrow"
            )
            val totalSteps = practicalJobsList.sumOf { it.steps.size }
            val doneSteps = practicalJobsList.sumOf { completedStepCount(it, pausedSession) }
            val overallProgress = if (totalSteps == 0) 0f else doneSteps / totalSteps.toFloat()

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.medium)
                        .clickable { moduleExpanded = !moduleExpanded }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Module progress",
                            style = type.titleSmall,
                            color = colors.onSurface
                        )
                        Text(
                            text = if (moduleExpanded) {
                                "Separate progress for each practical"
                            } else {
                                "$doneSteps of $totalSteps steps · tap to expand"
                            },
                            style = type.labelMedium,
                            color = colors.primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(shapes.small)
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (moduleExpanded) "Collapse" else "Expand",
                            tint = colors.onSurface,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(arrowRotation)
                        )
                    }
                }

                AnimatedVisibility(visible = !moduleExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { overallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(shapes.small),
                            color = colors.primary,
                            trackColor = colors.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$competent marked competent",
                            style = type.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                AnimatedVisibility(visible = moduleExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        practicalJobsList.forEachIndexed { index, job ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            JobStepProgressBlock(
                                job = job,
                                session = pausedSession,
                                startExpanded = jobMatchesSession(job, pausedSession),
                                onResume = if (jobMatchesSession(job, pausedSession)) {
                                    onResumePaused
                                } else {
                                    null
                                }
                            )
                        }
                    }
                }
            }
        }

        if (pausedSession == null && nextJob != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline, shapes.medium),
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Continue practical",
                        style = type.labelMedium,
                        color = colors.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nextJob.title,
                        style = type.titleMedium,
                        color = colors.onPrimaryContainer
                    )
                    Text(
                        text = nextJob.subtitle,
                        style = type.bodySmall,
                        color = colors.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onStartJob(nextJob) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(
                            text = if (nextJob.status == CompetencyStatus.NOT_ASSESSED) {
                                "Start this sheet"
                            } else {
                                "Resume this sheet"
                            },
                            style = type.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShortcutTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CameraAlt,
                title = "Scan part",
                subtitle = "Identify live",
                onClick = onScanComponent
            )
            ShortcutTile(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = "Theory",
                subtitle = "Reading list",
                onClick = onOpenLibrary
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Assigned practicals",
            style = type.titleMedium,
            color = colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            practicalJobsList.forEachIndexed { index, job ->
                AssignedPracticalRow(
                    index = index + 1,
                    job = job,
                    onClick = { onStartJob(job) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Spoken steps",
                        style = type.titleSmall,
                        color = colors.onSurface
                    )
                    Text(
                        text = "Read each directive aloud.",
                        style = type.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
                Switch(
                    checked = spokenEnabled,
                    onCheckedChange = {
                        spokenEnabled = it
                        PreferencesManager.setSpokenEnabled(context, it)
                        onToggleSpokenInstructions(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.onPrimary,
                        checkedTrackColor = colors.primary,
                        uncheckedThumbColor = colors.onSurfaceVariant,
                        uncheckedTrackColor = colors.surfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun DailyGoalsCard(
    goals: List<DailyGoal>,
    streak: GoalStreak,
    onOpen: (DailyGoal) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val done = goals.count { it.done }
    val total = goals.size.coerceAtLeast(1)
    val fraction = done / total.toFloat()

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
                        text = "Today's learning goals",
                        style = type.titleSmall,
                        color = colors.onSurface
                    )
                    Text(
                        text = DailyGoalStore.todayLabel(),
                        style = type.labelMedium,
                        color = colors.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${streak.current}-day streak",
                        style = type.labelMedium,
                        color = app.success
                    )
                    Text(
                        text = "$done of $total today",
                        style = type.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(shapes.small),
                color = if (done == total) app.success else colors.primary,
                trackColor = colors.surfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            goals.forEach { goal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.medium)
                        .clickable { onOpen(goal) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (goal.done) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Outlined.Circle
                        },
                        contentDescription = if (goal.done) "Completed" else "Not completed",
                        tint = if (goal.done) app.success else colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            style = type.titleSmall,
                            color = colors.onSurface
                        )
                        Text(
                            text = goal.detail,
                            style = type.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutTile(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    Card(
        modifier = modifier
            .border(1.dp, colors.outline, shapes.medium)
            .clickable(onClick = onClick),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colors.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = type.titleSmall, color = colors.onSurface)
            Text(text = subtitle, style = type.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun AssignedPracticalRow(
    index: Int,
    job: PracticalJob,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography

    val (statusText, statusColor) = when (job.status) {
        CompetencyStatus.COMPETENT -> "Competent" to app.success
        CompetencyStatus.NEEDS_PRACTICE -> "Retry" to colors.secondary
        CompetencyStatus.NOT_ASSESSED -> "Not started" to colors.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, shapes.medium)
            .clickable(onClick = onClick),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(shapes.small)
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString().padStart(2, '0'),
                    style = type.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = job.title, style = type.titleSmall, color = colors.onSurface)
                Text(text = job.subtitle, style = type.bodySmall, color = colors.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = statusText, style = type.labelSmall, color = statusColor)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun JobStepProgressBlock(
    job: PracticalJob,
    session: InProgressSession?,
    startExpanded: Boolean,
    onResume: (() -> Unit)?
) {
    val colors = MaterialTheme.colorScheme
    val app = LocalAppColors.current
    val shapes = MaterialTheme.shapes
    val type = MaterialTheme.typography
    val total = job.steps.size.coerceAtLeast(1)
    val done = completedStepCount(job, session)
    val fraction = done / total.toFloat()
    val isPausedHere = onResume != null
    var stepsExpanded by remember { mutableStateOf(startExpanded) }
    val jobArrow by animateFloatAsState(
        targetValue = if (stepsExpanded) 180f else 0f,
        label = "jobArrow"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.medium)
            .background(colors.surfaceVariant.copy(alpha = 0.45f))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { stepsExpanded = !stepsExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    style = type.titleSmall,
                    color = colors.onSurface
                )
                Text(
                    text = if (stepsExpanded) "Hide steps" else "$done / $total steps",
                    style = type.labelSmall,
                    color = if (isPausedHere) colors.primary else colors.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (stepsExpanded) "Hide steps" else "Show steps",
                tint = colors.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(jobArrow)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(shapes.small),
            color = if (done == total) app.success else colors.primary,
            trackColor = colors.outline.copy(alpha = 0.35f)
        )
        if (isPausedHere && !stepsExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Resume this practical",
                style = type.labelMedium,
                color = colors.primary,
                modifier = Modifier.clickable { onResume?.invoke() }
            )
        }
        AnimatedVisibility(visible = stepsExpanded) {
            Column {
                if (isPausedHere) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Resume this practical",
                        style = type.labelMedium,
                        color = colors.primary,
                        modifier = Modifier.clickable { onResume?.invoke() }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                job.steps.forEachIndexed { index, step ->
                    val state = stepState(job, session, index)
                    val (dotColor, labelColor, label) = when (state) {
                        StepProgressState.DONE -> Triple(app.success, colors.onSurface, "Done")
                        StepProgressState.CURRENT -> Triple(colors.primary, colors.primary, "Now")
                        StepProgressState.PENDING -> Triple(
                            colors.outline,
                            colors.onSurfaceVariant,
                            "Todo"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${index + 1}. ${step.shortTitle}",
                            style = type.bodySmall,
                            color = labelColor,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = label,
                            style = type.labelSmall,
                            color = labelColor
                        )
                    }
                }
            }
        }
    }
}

private enum class StepProgressState {
    DONE,
    CURRENT,
    PENDING
}

private fun jobMatchesSession(job: PracticalJob, session: InProgressSession?): Boolean {
    if (session == null) return false
    val taskId = session.taskId.lowercase()
    val componentId = session.componentId.lowercase()
    val title = session.title.lowercase()
    val jobId = job.id.lowercase()
    return jobId == taskId ||
        taskId.contains(jobId) ||
        jobId.contains(componentId) ||
        job.title.equals(session.title, ignoreCase = true) ||
        title.contains(job.title.lowercase())
}

private fun completedStepCount(job: PracticalJob, session: InProgressSession?): Int {
    val total = job.steps.size
    if (session != null && jobMatchesSession(job, session)) {
        val verified = session.verifiedSteps.count { it }
        return maxOf(verified, session.currentStepIndex).coerceIn(0, total)
    }
    return when (job.status) {
        CompetencyStatus.COMPETENT -> total
        CompetencyStatus.NEEDS_PRACTICE, CompetencyStatus.NOT_ASSESSED -> 0
    }
}

private fun stepState(
    job: PracticalJob,
    session: InProgressSession?,
    index: Int
): StepProgressState {
    if (session != null && jobMatchesSession(job, session)) {
        if (session.verifiedSteps.getOrElse(index) { false } || index < session.currentStepIndex) {
            return StepProgressState.DONE
        }
        if (index == session.currentStepIndex) return StepProgressState.CURRENT
        return StepProgressState.PENDING
    }
    return if (job.status == CompetencyStatus.COMPETENT) {
        StepProgressState.DONE
    } else {
        StepProgressState.PENDING
    }
}
