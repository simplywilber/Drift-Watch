package com.driftwatch.analytics.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Created by Wilber Amaya-Maurisio
@Entity(tableName = "environmental_readings")
data class EnvironmentalReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val barometricPressure: Double,
    val ambientTemperature: Double,
    val isDriftEvent: Boolean
)