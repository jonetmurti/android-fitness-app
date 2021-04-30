package com.example.workoutapp.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.*
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.android.gms.location.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class LocationService : Service() {
    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var trackerDao: TrackerDao

    private var currentId: Int = 0
    private var currentIdTrack: Int = 0

    private var cycling: Cycling? = null
    private var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()

        trackerDao = TrainingDatabase.getDatabase(applicationContext).trackerDao

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
//                super.onLocationResult(p0)

                currentLocation = p0.lastLocation
                currentIdTrack += 1

                var cyclingTrack = CyclingTrack(
                        latitude = p0.lastLocation.latitude,
                        longitude = p0.lastLocation.longitude,
                        idCycling = currentId
                )

                runBlocking {
                    trackerDao.insert(cyclingTrack)
                }

                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                if(serviceRunningInForeground){
                    notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification(currentLocation)
                    )
                }

            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        currentId += 1
        var cyclingData = Cycling(
                date = System.currentTimeMillis(),
                timeStart = System.currentTimeMillis(),
                timeEnd = System.currentTimeMillis(),
        )

        runBlocking {
            trackerDao.insert(cyclingData)
            cycling = trackerDao.getRecentCyclingOnly()
        }
        val cancelLocationTrackingFromNotification =
                intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        Log.d(TAG, cancelLocationTrackingFromNotification.toString())
        if(cancelLocationTrackingFromNotification){
            unsubscribeToLocationUpdates()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        // TODO("Return the communication channel to the service.")

        Log.d(TAG, "onBind()")

        stopForeground(true)
        serviceRunningInForeground = false

        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind()")

        stopForeground(true)
        serviceRunningInForeground = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind()")

        if(SharedPreferenceUtil.getLocationTrackingPref(this)){
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification(currentLocation)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        return true
    }

    fun subscribeToLocationUpdates(){
        Log.d(TAG, "subscribeToLocationUpdates()")

        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        startService(Intent(applicationContext, LocationService::class.java))

        try{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }catch (unlikely: SecurityException){
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates(){
        Log.d(TAG, "unsubscribeToLocationUpdates()")
        try{
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "Location Callback Removed.")
                    cycling?.let {
                        val oldCycling = cycling ?: return@let
                        Log.d("Walking Service", "Masuk sini woy")
                        oldCycling.timeEnd = System.currentTimeMillis()

                        runBlocking {
                            trackerDao.update(oldCycling)
                        }

                    }
                    stopSelf()
                }else{
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }

            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        }catch(unlikely: SecurityException){
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    private fun generateNotification(location: Location?): Notification {
        Log.d(TAG, "generateNotification()")

        val mainNotificationText = location?.toText() ?: "No current location"
        val titleText = "Location in Android"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(mainNotificationText)
                .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, MainActivity::class.java)

        val cancelIntent = Intent(this, LocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
                this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
                this, 0, launchActivityIntent, 0
        )

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(
                        R.drawable.ic_launcher_foreground, "Launch activity",
                        activityPendingIntent
                )
                .addAction(
                        R.drawable.ic_baseline_cancel_24, "Stop receiving location updates",
                        servicePendingIntent
                )
                .build()
    }


    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    companion object {
        private const val TAG = "Location Service"

        private const val PACKAGE_NAME = "com.example.workoutapp"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
                "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
                "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 13579

        private const val NOTIFICATION_CHANNEL_ID = "Workout_channel_1"
    }
}