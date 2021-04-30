package com.example.workoutapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.*
import kotlinx.coroutines.runBlocking

class WalkingService: Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager

    private lateinit var walkingDao: WalkingDao

    private var currentSteps: Long = 0
    private var walking: Walking? = null
    private var stepSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()

        walkingDao = TrainingDatabase.getDatabase(applicationContext).walkingDao

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        var walkingData = Walking(
                date = System.currentTimeMillis(),
                timeStart = System.currentTimeMillis(),
                timeEnd = System.currentTimeMillis(),
                totalStep = 0
        )

        Log.d("Walking Service", walkingData.toString())
        runBlocking {
            walkingDao.insert(walkingData)
            walking = walkingDao.getRecentWalking()
        }

        val cancelWalkingTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_WALKING_TRACKING_FROM_NOTIFICATION, false)

        Log.d(TAG, cancelWalkingTrackingFromNotification.toString())
        if(cancelWalkingTrackingFromNotification){
            unsubscribeToWalkingUpdates()
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

        if(SharedPreferenceUtil.getWalkingTrackingPref(this)){
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification(currentSteps)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        return true
    }

    fun subscribeToWalkingUpdates(){
        Log.d(TAG, "subscribeToWalkingUpdates()")

        SharedPreferenceUtil.saveWalkingTrackingPref(this, true)

        startService(Intent(applicationContext, WalkingService::class.java))
        running = true
        previousTotalSteps = SharedPreferenceUtil.getTotalStepPref(this)

        try{
            stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (stepSensor != null) {
                // Rate suitable for the user interface
                sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            }
        }catch (unlikely: SecurityException){
            SharedPreferenceUtil.saveWalkingTrackingPref(this, false)
            Log.e(TAG, "Lost Walking permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToWalkingUpdates(){
        Log.d(TAG, "unsubscribeToWalkingUpdates()")
        try{

            Log.d("Walking Service", walking.toString())
            if(walkingDao == null){
                walkingDao = TrainingDatabase.getDatabase(applicationContext).walkingDao
            }
            walking?.let {
                val oldWalking = walking ?: return@let
                Log.d("Walking Service", "Masuk sini woy")
                oldWalking.timeEnd = System.currentTimeMillis()

                runBlocking {
                    walkingDao.update(oldWalking)
                }

            }
            stepSensor?.let {
                sensorManager?.unregisterListener(this, it)
            }

            SharedPreferenceUtil.saveTotalStepPref(this, totalSteps)
            stopSelf()

            SharedPreferenceUtil.saveWalkingTrackingPref(this, false)
        }catch(unlikely: SecurityException){
            SharedPreferenceUtil.saveWalkingTrackingPref(this, true)
            Log.e(TAG, "Lost Walking permissions. Couldn't remove updates. $unlikely")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView

        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            currentSteps = totalSteps.toLong() - previousTotalSteps.toLong()

            val intent = Intent(ACTION_FOREGROUND_ONLY_WALKING_BROADCAST)
            intent.putExtra(EXTRA_WALKING, currentSteps)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

            walking?.let {
                it.totalStep = currentSteps
                runBlocking {
                    walkingDao.update(it)
                }

            }


            if(serviceRunningInForeground){
                notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(currentSteps)
                )
            }


        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun generateNotification(step: Long): Notification {
        Log.d(TAG, "generateNotification()")

        val mainNotificationText = step.toString()
        val titleText = "Walking in Android"

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

        val cancelIntent = Intent(this, WalkingService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_WALKING_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0
        )

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID
        )

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
                R.drawable.ic_baseline_cancel_24, "Stop receiving steps updates",
                servicePendingIntent
            )
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: WalkingService
            get() = this@WalkingService
    }

    companion object {
        private const val TAG = "Walking Service"

        private const val PACKAGE_NAME = "com.example.workoutapp"

        internal const val ACTION_FOREGROUND_ONLY_WALKING_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_WALKING_BROADCAST"

        internal const val EXTRA_WALKING = "$PACKAGE_NAME.extra.WALKING"

        private const val EXTRA_CANCEL_WALKING_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_WALKING_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 123456790

        private const val NOTIFICATION_CHANNEL_ID = "Workout_channel_2"
    }
}