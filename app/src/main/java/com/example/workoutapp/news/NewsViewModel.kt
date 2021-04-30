package com.example.workoutapp.news

import android.app.Application
import androidx.lifecycle.*
import com.example.workoutapp.network.NetworkNews
import java.lang.IllegalArgumentException

class NewsViewModel(application: Application): AndroidViewModel(application) {
    var _news: MutableLiveData<List<NetworkNews>> = MutableLiveData(listOf())
    val news: LiveData<List<NetworkNews>>
        get() =  _news

    fun insertNews(news: List<NetworkNews>){
        _news.value = news
    }
}

class NewsViewModelFactory(
        private val application: Application
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
            return NewsViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}