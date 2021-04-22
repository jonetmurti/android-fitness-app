package com.example.workoutapp.network

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService{
    @GET("top-headlines")
    fun getNews(@Query("country") country: String,
                        @Query("category") category: String,
                        @Query("apiKey") apiKey: String): Single<NetworkNewsContainer>
}

object NetworkService {
    const val API_KEY = "a8ce928d19a349dcb5de77485e1c616a"
    private const val BASE_URL = "https://newsapi.org/v2/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

    val newsService = retrofit.create(NewsService::class.java)
}