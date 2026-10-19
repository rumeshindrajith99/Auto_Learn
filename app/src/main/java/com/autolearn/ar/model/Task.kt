package com.autolearn.ar.model

data class Task(
    val id: String,
    val componentId: String,
    val name: String,
    val steps: List<String>
)
