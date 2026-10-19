package com.autolearn.ar.data

import android.content.Context

data class InProgressSession(
    val taskId: String,
    val componentId: String,
    val title: String,
    val currentStepIndex: Int,
    val totalSteps: Int,
    val verifiedSteps: List<Boolean>
) {
    val stepLabel: String
        get() = "Step ${currentStepIndex + 1} of $totalSteps"
}

object InProgressStore {
    private const val PREF_NAME = "engiscan_in_progress"
    private const val KEY_TASK_ID = "task_id"
    private const val KEY_COMPONENT_ID = "component_id"
    private const val KEY_TITLE = "title"
    private const val KEY_STEP = "current_step"
    private const val KEY_TOTAL = "total_steps"
    private const val KEY_VERIFIED = "verified_steps"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun save(context: Context, session: InProgressSession) {
        prefs(context).edit()
            .putString(KEY_TASK_ID, session.taskId)
            .putString(KEY_COMPONENT_ID, session.componentId)
            .putString(KEY_TITLE, session.title)
            .putInt(KEY_STEP, session.currentStepIndex)
            .putInt(KEY_TOTAL, session.totalSteps)
            .putString(KEY_VERIFIED, encodeSteps(session.verifiedSteps))
            .apply()
    }

    fun load(context: Context): InProgressSession? {
        val stored = prefs(context)
        val taskId = stored.getString(KEY_TASK_ID, null).orEmpty()
        if (taskId.isBlank()) return null
        val total = stored.getInt(KEY_TOTAL, 0).coerceAtLeast(1)
        return InProgressSession(
            taskId = taskId,
            componentId = stored.getString(KEY_COMPONENT_ID, "").orEmpty(),
            title = stored.getString(KEY_TITLE, "Practical").orEmpty(),
            currentStepIndex = stored.getInt(KEY_STEP, 0).coerceIn(0, total - 1),
            totalSteps = total,
            verifiedSteps = decodeSteps(stored.getString(KEY_VERIFIED, ""), total)
        )
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun pauseAndNotify(context: Context, session: InProgressSession) {
        save(context, session)
        ResumeNotifier.show(context, session)
    }

    fun finish(context: Context) {
        clear(context)
        ResumeNotifier.cancel(context)
    }

    private fun encodeSteps(steps: List<Boolean>): String {
        return steps.joinToString(",") { if (it) "1" else "0" }
    }

    private fun decodeSteps(raw: String?, total: Int): List<Boolean> {
        val parsed = raw.orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
            .map { it == "1" }
        return List(total) { index -> parsed.getOrElse(index) { false } }
    }
}
