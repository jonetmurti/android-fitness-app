package com.example.workoutapp.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkNewsContainer(val articles: List<NetworkNews>)

@JsonClass(generateAdapter = true)
data class NetworkNews(
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
)