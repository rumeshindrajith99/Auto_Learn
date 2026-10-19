package com.autolearn.ar.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScoreRecord(
    val taskName: String,
    val componentId: String,
    val score: Int,
    val durationText: String,
    val dateText: String,
    val grade: String
)

object ScoreHistoryManager {
    private const val PREF_NAME = "autolearn_score_history_prefs"
    private const val KEY_HISTORY = "score_history_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveScore(context: Context, taskName: String, componentId: String, score: Int, durationMillis: Long) {
        val prefs = getPrefs(context)
        val historyList = getHistory(context).toMutableList()

        val secs = (durationMillis / 1000L).coerceAtLeast(1L)
        val mins = secs / 60L
        val remSecs = secs % 60L
        val formattedDuration = String.format(Locale.US, "%02d:%02d", mins, remSecs)

        val dateFmt = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
        val formattedDate = dateFmt.format(Date())

        val grade = when {
            score >= 90 -> "A+"
            score >= 80 -> "A"
            score >= 70 -> "B"
            score >= 60 -> "C"
            else -> "D"
        }

        // Avoid duplicate identical score saved within 5 seconds
        if (historyList.isNotEmpty()) {
            val latest = historyList.first()
            if (latest.taskName == taskName && latest.score == score && latest.durationText == formattedDuration) {
                return
            }
        }

        val newRecord = ScoreRecord(
            taskName = taskName,
            componentId = componentId,
            score = score,
            durationText = formattedDuration,
            dateText = formattedDate,
            grade = grade
        )

        historyList.add(0, newRecord)

        val trimmed = historyList.take(20)

        val jsonArray = JSONArray()
        for (record in trimmed) {
            val obj = JSONObject().apply {
                put("taskName", record.taskName)
                put("componentId", record.componentId)
                put("score", record.score)
                put("durationText", record.durationText)
                put("dateText", record.dateText)
                put("grade", record.grade)
            }
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    fun getHistory(context: Context): List<ScoreRecord> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<ScoreRecord>()

        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ScoreRecord(
                        taskName = obj.optString("taskName", "Practical Job"),
                        componentId = obj.optString("componentId", ""),
                        score = obj.optInt("score", 0),
                        durationText = obj.optString("durationText", "00:00"),
                        dateText = obj.optString("dateText", ""),
                        grade = obj.optString("grade", "N/A")
                    )
                )
            }
        } catch (_: Exception) {}

        return list
    }
}
