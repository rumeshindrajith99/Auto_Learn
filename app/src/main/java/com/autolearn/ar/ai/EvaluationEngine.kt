package com.autolearn.ar.ai

import com.autolearn.ar.model.Evaluation
import com.autolearn.ar.utils.calculateScore

class EvaluationEngine {
    fun evaluate(
        taskName: String,
        steps: List<String>,
        completed: List<Boolean>,
        safetyWarnings: List<String>
    ): Evaluation {
        val completedCount = completed.count { it }
        val score = calculateScore(completedCount, steps.size)
        val mistakes = steps.filterIndexed { index, _ -> !completed[index] }
        val suggestions = if (score < 70) {
            listOf(
                "Review the task instructions.",
                "Follow the correct step sequence.",
                "Repeat the practical activity."
            )
        } else {
            listOf(
                "Good performance.",
                "Continue practicing the task."
            )
        }
        return Evaluation(taskName, score, mistakes, safetyWarnings, suggestions)
    }
}
