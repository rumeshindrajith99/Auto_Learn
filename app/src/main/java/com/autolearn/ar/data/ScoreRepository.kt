package com.autolearn.ar.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScoreRepository {

    private fun dao(context: Context): ScoreDao {
        return AppDatabase.getInstance(context).scoreDao()
    }

    suspend fun saveScore(
        context: Context,
        taskName: String,
        componentId: String,
        score: Int,
        durationMillis: Long
    ) {
        migratePrefsIfNeeded(context)

        val secs = (durationMillis / 1000L).coerceAtLeast(1L)
        val formattedDuration = String.format(
            Locale.US,
            "%02d:%02d",
            secs / 60L,
            secs % 60L
        )
        val now = System.currentTimeMillis()
        val dateText = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
            .format(Date(now))
        val grade = gradeFor(score)

        val existing = dao(context).getAll()
        val latest = existing.firstOrNull()
        if (
            latest != null &&
            latest.taskName == taskName &&
            latest.score == score &&
            latest.durationText == formattedDuration &&
            now - latest.dateMillis < 8_000L
        ) {
            return
        }

        dao(context).insert(
            ScoreEntity(
                taskName = taskName,
                componentId = componentId,
                score = score,
                durationMillis = durationMillis,
                durationText = formattedDuration,
                dateMillis = now,
                dateText = dateText,
                grade = grade
            )
        )
    }

    suspend fun getHistory(context: Context): List<ScoreRecord> {
        migratePrefsIfNeeded(context)
        return dao(context).getAll().map { it.toRecord() }
    }

    private suspend fun migratePrefsIfNeeded(context: Context) {
        if (dao(context).count() > 0) return
        val legacy = ScoreHistoryManager.getHistory(context)
        if (legacy.isEmpty()) return
        val now = System.currentTimeMillis()
        legacy.reversed().forEachIndexed { index, record ->
            dao(context).insert(
                ScoreEntity(
                    taskName = record.taskName,
                    componentId = record.componentId,
                    score = record.score,
                    durationMillis = 0L,
                    durationText = record.durationText,
                    dateMillis = now - ((legacy.size - index) * 60_000L),
                    dateText = record.dateText,
                    grade = record.grade
                )
            )
        }
    }

    private fun ScoreEntity.toRecord(): ScoreRecord {
        return ScoreRecord(
            taskName = taskName,
            componentId = componentId,
            score = score,
            durationText = durationText,
            dateText = dateText,
            grade = grade,
            durationMillis = durationMillis
        )
    }

    fun gradeFor(score: Int): String {
        return when {
            score >= 90 -> "A+"
            score >= 80 -> "A"
            score >= 70 -> "B"
            score >= 60 -> "C"
            else -> "D"
        }
    }
}
