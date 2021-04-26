package com.example.workoutapp.service

import android.content.Context
import android.location.Location
import androidx.core.content.edit
import com.example.workoutapp.R
import com.example.workoutapp.database.CyclingAndTrack
import kotlin.math.*


/**
 * Returns the `location` object as a human readable string.
 */
fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

fun calculateTotalDistance(cycling: CyclingAndTrack): Double{
    if(cycling.tracks.size <= 1){
        return 0.toDouble()
    }
    var sum = 0.toDouble()
    for(i in 1..cycling.tracks.size){
        sum += calculateDistanceInKm(cycling.tracks[i].latitude, cycling.tracks[i].longitude, cycling.tracks[i-1].latitude, cycling.tracks[i-1].longitude)
    }

    return sum
}

fun calculateDistanceInKm(latitudeA: Double, longitudeA: Double, latitudeB: Double, longitudeB: Double ): Double{
    val longitudeARadian = longitudeA * Math.PI / 180
    val latitudeARadian = latitudeA * Math.PI / 180

    val longitudeBRadian = longitudeB * Math.PI / 180
    val latitudeBRadian = latitudeB * Math.PI / 180

    val dLongitude = longitudeBRadian - longitudeARadian
    val dLatitude = latitudeBRadian - latitudeARadian

    val a = sin(dLatitude/2).pow(2) + cos(latitudeARadian) * cos(latitudeBRadian) * sin(dLongitude/2).pow(2)
    val c = 2 * asin(sqrt(a))
    val r = 6371
    return c * r
}

/**
 * Provides access to SharedPreferences for location to Activities and Services.
 */
internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun getLocationTrackingPref(context: Context): Boolean =
            context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    .getBoolean(KEY_FOREGROUND_ENABLED, false)

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
            context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE).edit {
                putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
            }
}