package com.autolearn.ar.utils

fun calculateScore(completedSteps: Int, totalSteps: Int): Int {
    if (totalSteps <= 0) return 0
    return ((completedSteps.toFloat() / totalSteps.toFloat()) * 100f).toInt().coerceIn(0, 100)
}
