package com.autolearn.ar.data

import android.content.Context

object PreferencesManager {
    private const val PREF_NAME = "engiscan_user_prefs"
    private const val KEY_SPOKEN = "spoken_instructions"
    private const val KEY_CONFIDENCE = "confidence_threshold"
    private const val KEY_SENSITIVITY = "camera_sensitivity"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isSpokenEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SPOKEN, true)
    }

    fun setSpokenEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_SPOKEN, enabled).apply()
    }

    fun confidenceThreshold(context: Context): Float {
        return prefs(context).getFloat(KEY_CONFIDENCE, 0.60f).coerceIn(0.45f, 0.90f)
    }

    fun setConfidenceThreshold(context: Context, value: Float) {
        prefs(context).edit().putFloat(KEY_CONFIDENCE, value.coerceIn(0.45f, 0.90f)).apply()
    }

    fun cameraSensitivity(context: Context): Int {
        return prefs(context).getInt(KEY_SENSITIVITY, 1).coerceIn(0, 2)
    }

    fun setCameraSensitivity(context: Context, level: Int) {
        prefs(context).edit().putInt(KEY_SENSITIVITY, level.coerceIn(0, 2)).apply()
    }

    fun actionMinConfidence(context: Context): Float {
        return when (cameraSensitivity(context)) {
            0 -> 0.10f
            2 -> 0.22f
            else -> 0.15f
        }
    }

    fun sensitivityLabel(level: Int): String {
        return when (level) {
            0 -> "Low"
            2 -> "High"
            else -> "Medium"
        }
    }
}
