package com.example.workoutapp.tracker

import android.app.Application
import androidx.lifecycle.*
import com.example.workoutapp.database.TrackerDao
import com.example.workoutapp.database.Walking
import com.example.workoutapp.database.WalkingDao
import kotlinx.coroutines.launch

class WalkingViewModel(application: Application, val database: WalkingDao): AndroidViewModel(application) {
    private var walk: MutableLiveData<Walking?> = MutableLiveData()

    private fun initializeWalk(){
        viewModelScope.launch {
            walk.value = getWalkFromDatabase()
        }
    }

    private suspend fun getWalkFromDatabase(): Walking? {
        var walking = database.getRecentWalking()
        if(walking?.timeEnd != walking?.timeStart){
            walking = null
        }
        return walking
    }
}

class WalkingViewModelFactory(private val application: Application, private val dataSource: TrackerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrackerViewModel::class.java)){
            return TrackerViewModel(application, dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}