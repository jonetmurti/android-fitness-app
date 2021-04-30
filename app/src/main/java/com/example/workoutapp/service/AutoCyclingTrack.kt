package com.example.workoutapp.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class AutoCyclingTrack : Service() {
    private var locationService: LocationService? = null
    private var locationServiceBound = false


    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("connected","connect")
            val binder = service as LocationService.LocalBinder
            Log.d("binder",binder.toString())
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
        }
    }

    override fun onCreate() {
        val locationServiceIntent = Intent(applicationContext, LocationService::class.java)
        applicationContext.bindService(locationServiceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val start : Int? = intent?.getIntExtra("start", -1)
        Log.d("start", start.toString())
        if (start == 1 && !SharedPreferenceUtil.getLocationTrackingPref(applicationContext)) {
            if(locationService != null){
                locationService!!.subscribeToLocationUpdates()

            }
        } else {
                locationService?.unsubscribeToLocationUpdates()

        }
        return START_NOT_STICKY

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null

    }
}
