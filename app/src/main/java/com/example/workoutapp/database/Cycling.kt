package com.example.workoutapp.database

import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "cycling")
data class Cycling(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val timeStart: Long,
    val timeEnd: Long,
)

@Entity(tableName = "cycling_track", foreignKeys = arrayOf(ForeignKey(entity = Cycling::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("idCycling"),
    onDelete = ForeignKey.CASCADE
)))
data class CyclingTrack(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(index = true)
    val idCycling: Int,
)

data class CyclingAndTrack(
    @Embedded
    val cycling: Cycling,
    @Relation(
        parentColumn = "id",
        entityColumn = "idCycling"
    )
    val tracks: List<CyclingTrack>
)