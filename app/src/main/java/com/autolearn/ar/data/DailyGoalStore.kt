package com.autolearn.ar.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DailyGoalAction {
    PRACTICAL,
    FOCUS_JOB,
    THEORY,
    SCAN
}

data class DailyGoal(
    val id: String,
    val title: String,
    val detail: String,
    val action: DailyGoalAction,
    val jobId: String? = null,
    val done: Boolean = false
)

data class GoalStreak(
    val current: Int,
    val best: Int,
    val completedDays: Int
)

object DailyGoalStore {
    private const val PREF_NAME = "engiscan_daily_goals"
    private const val KEY_DATE = "goal_date"
    private const val KEY_DONE = "goal_done_ids_v2"
    private const val KEY_COMPLETED_DAYS = "goal_completed_days"
    private const val KEY_BEST_STREAK = "goal_best_streak"
    private const val KEY_THEORY_COUNT = "goal_theory_count"
    private const val KEY_SCAN_COUNT = "goal_scan_count"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun dateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    fun todayKey(): String {
        return dateFormat().format(Date())
    }

    fun todayScoreLabel(): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    fun todayLabel(): String {
        return SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date())
    }

    fun catalog(): List<DailyGoal> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val focus = practicalJobsList[dayOfYear % practicalJobsList.size]
        return listOf(
            DailyGoal(
                id = "practical",
                title = "Complete a practical",
                detail = "Finish one sheet today. Starts do not count.",
                action = DailyGoalAction.PRACTICAL
            ),
            DailyGoal(
                id = "focus",
                title = "Today's sheet: ${focus.title}",
                detail = "Finish all steps on this sheet today.",
                action = DailyGoalAction.FOCUS_JOB,
                jobId = focus.id
            ),
            DailyGoal(
                id = "theory",
                title = "Read one theory page",
                detail = "Open a component theory page and read it.",
                action = DailyGoalAction.THEORY
            ),
            DailyGoal(
                id = "scan",
                title = "Scan a live part",
                detail = "Get a live identification, not just open the camera.",
                action = DailyGoalAction.SCAN
            )
        )
    }

    fun goals(context: Context, todayRecords: List<ScoreRecord> = emptyList()): List<DailyGoal> {
        return refreshCompletion(context, todayRecords)
    }

    fun refreshCompletion(
        context: Context,
        todayRecords: List<ScoreRecord>
    ): List<DailyGoal> {
        ensureToday(context)
        val storedDone = doneIds(context).toMutableSet()
        val mapped = catalog().map { goal ->
            val completed = when (goal.id) {
                "practical" -> todayRecords.isNotEmpty()
                "focus" -> todayRecords.any { recordMatchesJob(it, goal.jobId) }
                else -> goal.id in storedDone
            }
            goal.copy(done = completed)
        }
        mapped.filter { it.done }.forEach { storedDone.add(it.id) }
        saveDone(context, storedDone)
        if (mapped.isNotEmpty() && mapped.all { it.done }) {
            addCompletedDay(context, todayKey())
        }
        return mapped
    }

    suspend fun refreshFromHistory(context: Context): List<DailyGoal> {
        val history = ScoreRepository.getHistory(context)
        val todayRecords = history.filter { it.dateText.startsWith(todayScoreLabel()) }
        return refreshCompletion(context, todayRecords)
    }

    fun markScanCompleted(context: Context) {
        ensureToday(context)
        if ("scan" !in doneIds(context)) {
            bumpCount(context, KEY_SCAN_COUNT)
        }
        markDone(context, "scan")
    }

    fun markTheoryCompleted(context: Context) {
        ensureToday(context)
        if ("theory" !in doneIds(context)) {
            bumpCount(context, KEY_THEORY_COUNT)
        }
        markDone(context, "theory")
    }

    fun streak(context: Context): GoalStreak {
        val days = completedDays(context)
        val current = currentStreak(days)
        val storedBest = prefs(context).getInt(KEY_BEST_STREAK, 0)
        val best = maxOf(storedBest, longestStreak(days), current)
        if (best > storedBest) {
            prefs(context).edit().putInt(KEY_BEST_STREAK, best).apply()
        }
        return GoalStreak(
            current = current,
            best = best,
            completedDays = days.size
        )
    }

    fun badges(context: Context): List<AchievementBadge> {
        val streak = streak(context)
        val theoryCount = prefs(context).getInt(KEY_THEORY_COUNT, 0)
        val scanCount = prefs(context).getInt(KEY_SCAN_COUNT, 0)
        return listOf(
            AchievementBadge(
                id = "goal_first_day",
                title = "Full day",
                description = "Complete every learning goal in one day.",
                unlocked = streak.completedDays >= 1
            ),
            AchievementBadge(
                id = "goal_streak_3",
                title = "Three-day streak",
                description = "Hit all daily goals three days in a row.",
                unlocked = streak.best >= 3 || streak.current >= 3
            ),
            AchievementBadge(
                id = "goal_streak_7",
                title = "Week on task",
                description = "Keep a seven-day learning-goal streak.",
                unlocked = streak.best >= 7 || streak.current >= 7
            ),
            AchievementBadge(
                id = "goal_ten_days",
                title = "Dedicated",
                description = "Finish all daily goals on ten different days.",
                unlocked = streak.completedDays >= 10
            ),
            AchievementBadge(
                id = "goal_theory_3",
                title = "Theory reader",
                description = "Read a theory page on three days.",
                unlocked = theoryCount >= 3
            ),
            AchievementBadge(
                id = "goal_scan_3",
                title = "Live identifier",
                description = "Identify a live part on three days.",
                unlocked = scanCount >= 3
            )
        )
    }

    fun recordMatchesJob(record: ScoreRecord, jobId: String?): Boolean {
        if (jobId.isNullOrBlank()) return false
        val componentIds = componentIdsForJob(jobId)
        val componentId = record.componentId.lowercase()
        val taskName = record.taskName.lowercase()
        val job = practicalJobsList.firstOrNull { it.id == jobId }
        return componentId in componentIds ||
            job?.title?.lowercase()?.let { taskName.contains(it) } == true
    }

    private fun componentIdsForJob(jobId: String): Set<String> {
        return when (jobId) {
            "air_filter_service" -> setOf("air_filter")
            "oil_filter_inspection" -> setOf("engine_oil_dipstick", "oil_filter")
            "spark_plug_replacement" -> setOf("spark_plug")
            "battery_inspection" -> setOf("battery_terminal", "battery")
            else -> setOf(jobId.lowercase())
        }
    }

    private fun markDone(context: Context, goalId: String) {
        ensureToday(context)
        val updated = doneIds(context).toMutableSet()
        updated.add(goalId)
        saveDone(context, updated)
    }

    private fun doneIds(context: Context): Set<String> {
        ensureToday(context)
        return prefs(context)
            .getString(KEY_DONE, "")
            .orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun completedDays(context: Context): Set<String> {
        return prefs(context)
            .getString(KEY_COMPLETED_DAYS, "")
            .orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun addCompletedDay(context: Context, day: String) {
        val days = completedDays(context).toMutableSet()
        if (!days.add(day)) return
        val best = maxOf(
            prefs(context).getInt(KEY_BEST_STREAK, 0),
            currentStreak(days),
            longestStreak(days)
        )
        prefs(context).edit()
            .putString(KEY_COMPLETED_DAYS, days.sorted().joinToString(","))
            .putInt(KEY_BEST_STREAK, best)
            .apply()
    }

    private fun currentStreak(days: Set<String>): Int {
        if (days.isEmpty()) return 0
        val format = dateFormat()
        val cursor = Calendar.getInstance()
        if (format.format(cursor.time) !in days) {
            cursor.add(Calendar.DAY_OF_YEAR, -1)
        }
        var streak = 0
        while (format.format(cursor.time) in days) {
            streak++
            cursor.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    private fun longestStreak(days: Set<String>): Int {
        if (days.isEmpty()) return 0
        val format = dateFormat()
        val sorted = days.mapNotNull { key ->
            runCatching { format.parse(key) }.getOrNull()
        }.sorted()
        if (sorted.isEmpty()) return 0
        var best = 1
        var run = 1
        for (index in 1 until sorted.size) {
            val previous = Calendar.getInstance().apply { time = sorted[index - 1] }
            val current = Calendar.getInstance().apply { time = sorted[index] }
            previous.add(Calendar.DAY_OF_YEAR, 1)
            val consecutive = previous.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                previous.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
            if (consecutive) {
                run++
                best = maxOf(best, run)
            } else {
                run = 1
            }
        }
        return best
    }

    private fun bumpCount(context: Context, key: String) {
        val stored = prefs(context)
        stored.edit().putInt(key, stored.getInt(key, 0) + 1).apply()
    }

    private fun ensureToday(context: Context) {
        val stored = prefs(context)
        val today = todayKey()
        if (stored.getString(KEY_DATE, null) == today) return
        stored.edit()
            .putString(KEY_DATE, today)
            .putString(KEY_DONE, "")
            .apply()
    }

    private fun saveDone(context: Context, ids: Set<String>) {
        prefs(context).edit()
            .putString(KEY_DATE, todayKey())
            .putString(KEY_DONE, ids.joinToString(","))
            .apply()
    }
}
