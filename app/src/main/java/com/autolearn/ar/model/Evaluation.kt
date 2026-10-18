package com.autolearn.ar.model

data class Evaluation(
    val taskName: String,
    val score: Int,
    val mistakes: List<String>,
    val safetyWarnings: List<String>,
    val suggestions: List<String>
)
