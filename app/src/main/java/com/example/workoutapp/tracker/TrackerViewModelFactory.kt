package com.example.workoutapp.tracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workoutapp.database.TrackerDao

class TrackerViewModelFactory(private val application: Application, private val dataSource: TrackerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrackerViewModel::class.java)){
            return TrackerViewModel(application, dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}