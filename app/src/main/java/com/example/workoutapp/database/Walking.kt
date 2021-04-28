package com.example.workoutapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walking")
data class Walking(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val date: Long,
        val timeStart: Long,
        val timeEnd: Long,
        val totalStep: Long,
)