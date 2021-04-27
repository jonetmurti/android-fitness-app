package com.example.workoutapp.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class NetworkNewsContainer(val articles: List<NetworkNews>)

@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkNews(
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
): Parcelable