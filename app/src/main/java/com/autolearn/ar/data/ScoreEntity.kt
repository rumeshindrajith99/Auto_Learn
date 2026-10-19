package com.autolearn.ar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_results")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskName: String,
    val componentId: String,
    val score: Int,
    val durationMillis: Long,
    val durationText: String,
    val dateMillis: Long,
    val dateText: String,
    val grade: String
)
