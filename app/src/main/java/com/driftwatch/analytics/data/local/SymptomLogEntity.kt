package com.driftwatch.analytics.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Created by Wilber Amaya-Maurisio
@Entity(tableName = "symptom_logs")
data class SymptomLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val symptomType: String,
    val severityLevel: Int,
    val notes: String
)