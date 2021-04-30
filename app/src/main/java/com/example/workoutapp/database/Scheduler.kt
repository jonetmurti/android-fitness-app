package com.example.workoutapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "scheduler")
data class Scheduler(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val type: Int,
        val timeStart: Long,
        val timeEnd: Long,
        val trainingType: String,
        val targetKm: Int?,
        val targetStep: Int?,
        val auto: Int
)
