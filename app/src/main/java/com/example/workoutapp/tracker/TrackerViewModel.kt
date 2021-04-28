package com.example.workoutapp.tracker

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workoutapp.database.TrackerDao

class TrackerViewModel(application: Application, dataSource: TrackerDao): AndroidViewModel(application) {
    val database = dataSource
    val tracks = database.getRecentCycling()
    var _isCycling: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCycling: LiveData<Boolean>
        get() = _isCycling

    fun updateIsCycling(value: Boolean){
        _isCycling.value = value
    }

    fun getIsCycling(): Boolean{
        return isCycling.value == true
    }
}