package com.example.workoutapp.history

data class TrainingLog(
    val id: Int,
    val type: String,
    val timeStart: Long,
    val timeEnd: Long
) {
}