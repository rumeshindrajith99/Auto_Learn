package com.autolearn.ar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(entity: ScoreEntity): Long

    @Query("SELECT * FROM score_results ORDER BY dateMillis DESC")
    suspend fun getAll(): List<ScoreEntity>

    @Query("SELECT COUNT(*) FROM score_results")
    suspend fun count(): Int
}
