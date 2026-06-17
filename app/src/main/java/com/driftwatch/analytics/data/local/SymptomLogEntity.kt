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
    val notes: String,
    // Added fields to store environmental conditions at the time of logging
    val associatedPressure: Double? = null,
    val associatedTemperature: Double? = null,
    val associatedDriftEvent: Boolean? = null,
    // Raw diagnostic data for editing (Added by Yevgeniy Mazur)
    val rawClassifications: String = "",
    val rawSubOptions: String = "",
    val rawBodyParts: String = "",
    val rawBowelTypes: String = "",
    val customSymptomText: String = "",
    val userNotes: String = ""
)
