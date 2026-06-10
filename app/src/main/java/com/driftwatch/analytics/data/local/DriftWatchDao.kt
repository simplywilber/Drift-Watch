package com.driftwatch.analytics.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Created by Wilber Amaya-Maurisio
@Dao
interface DriftWatchDao {

    @Query("SELECT * FROM environmental_readings ORDER BY timestamp DESC")
    fun observeReadings(): Flow<List<EnvironmentalReadingEntity>>

    @Query("SELECT * FROM symptom_logs ORDER BY timestamp DESC")
    fun observeSymptoms(): Flow<List<SymptomLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: EnvironmentalReadingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: SymptomLogEntity)
}