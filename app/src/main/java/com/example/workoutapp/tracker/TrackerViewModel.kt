package com.example.workoutapp.tracker

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workoutapp.database.TrackerDao

class TrackerViewModel(dataSource: TrackerDao): ViewModel() {
    val database = dataSource
    val tracks = database.getRecentCycling()

}