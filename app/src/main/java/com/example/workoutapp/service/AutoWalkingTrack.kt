package com.example.workoutapp.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class AutoWalkingTrack : Service() {
    private var walkingServiceBound = false
    private var walkingService: WalkingService? = null

    private val walkingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WalkingService.LocalBinder
            walkingService = binder.service
            walkingServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            walkingService = null
            walkingServiceBound = false
        }
    }

    override fun onCreate() {
        val walkingServiceIntent = Intent(applicationContext, WalkingService::class.java)
        applicationContext.bindService(walkingServiceIntent, walkingServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val start : Int? = intent?.getIntExtra("start", -1)
        Log.d("start", start.toString())
        if (start == 1) {
            if(walkingService != null && !SharedPreferenceUtil.getWalkingTrackingPref(applicationContext)){
                walkingService!!.subscribeToWalkingUpdates()

            }
        } else {
            walkingService?.unsubscribeToWalkingUpdates()

        }
        return START_NOT_STICKY

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}