package com.driftwatch.analytics.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Created by Wilber Amaya-Maurisio
@Database(
    entities = [EnvironmentalReadingEntity::class, SymptomLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driftWatchDao(): DriftWatchDao
}