 package com.example.workoutapp.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.*
import java.util.*


 public class Alarm : BroadcastReceiver() {
    private var locationService: LocationService? = null
    private var locationServiceBound = false

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {


        val start : String = intent.getIntExtra("start", -1).toString()
        val trainingType : String? = intent.getStringExtra("exercise")
        var target : Int = intent.getIntExtra("target", 0)
        val startTime : Long = intent.getLongExtra("startTime", 0)
        val endTime : Long = intent.getLongExtra("endTime", 0)
        val id : Int = intent.getIntExtra("id", 0)
        val type : String = intent.getIntExtra("type", 0).toString()
        val auto : Boolean = intent.getBooleanExtra("auto", true)

        val targets : String
        if (trainingType == "Walking"){
            targets = target.toString() + " steps"
        } else {
            targets = target.toString() + " kms"
        }

        var builder = NotificationCompat.Builder(context, "channel")
            .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), intent, 0)
        val notificationManager :  NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (start == "1"){
            builder.setContentTitle("Starting your exercise...")
                .setContentText(trainingType + " exercise with target: " + targets)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


        } else {
            builder.setContentTitle("Ending your exercise...")
                .setContentText(trainingType + " exercise with your target: " + targets)
                .setAutoCancel(true)

        }
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        Log.d("training", trainingType.toString())
        Log.d("auto", auto.toString())
        if (auto){
            if (trainingType == "Walking"){
                Log.d("hey", "jey")
            } else {
                Log.d("boo", "boo")
//                val locationServiceIntent = Intent(context, LocationService::class.java)
//                val binder: IBinder = peekService(context, locationServiceIntent)
//                if (binder != null){
//                    locationService = (binder as LocationService.LocalBinder).service
//                    locationService!!.subscribeToLocationUpdates()
//
//                }
            }
        }
        if (type == "2"){
            if (trainingType != null) {
                repeat(context, startTime + (INTERVAL_DAY), endTime + (INTERVAL_DAY), trainingType, target, id, 2, auto)
            }
        } else if (type == "3"){
            if (trainingType != null) {
                repeat(context, startTime + (INTERVAL_DAY * 7), endTime + (INTERVAL_DAY * 7), trainingType, target, id, 3, auto)
            }
        }


    }

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
        }
    }

    fun repeat(context: Context, startTime: Long, endTime: Long, exerciseType: String, target: Int, id: Int, type: Int, auto: Boolean){
        val startIntent = Intent(context, Alarm::class.java)
        startIntent.putExtra("exercise", exerciseType)
        startIntent.putExtra("target", target)
        startIntent.putExtra("id", id)
        startIntent.putExtra("start", 1)
        startIntent.putExtra("startTime", startTime)
        startIntent.putExtra("endTime", endTime)
        startIntent.putExtra("type", type)
        startIntent.putExtra("auto", auto)

        val endIntent = Intent(context, Alarm::class.java)
        endIntent.putExtra("exercise", exerciseType)
        endIntent.putExtra("target", target)
        endIntent.putExtra("id", id)
        var newcal = Calendar.getInstance()
        newcal.timeInMillis = startTime
        Log.d("startTime", newcal.toString())
        newcal.timeInMillis = endTime
        Log.d("endTime", newcal.toString())

        var startReqId : Int = System.currentTimeMillis().toInt()
        val startPendingIntent = PendingIntent.getBroadcast(context, startReqId, startIntent, PendingIntent.FLAG_ONE_SHOT)

        var endReqId : Int = System.currentTimeMillis().toInt() + 10
        val endPendingIntent = PendingIntent.getBroadcast(context, endReqId, endIntent, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                startTime, startPendingIntent
        )

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                endTime, endPendingIntent
        )
    }

    fun getTrackingData(context: Context, startTime: Long) : Double? {
        val trackingDao: TrackerDao =
                TrainingDatabase.getDatabase(context.applicationContext).trackerDao

        val data : CyclingAndTrack? = trackingDao.getWalkingByTime(startTime).value

        val distance = data?.let { calculateTotalDistance(it) }

        return distance

    }

    fun getWalkingData(context: Context, startTime: Long): Long?{
        val walkerDao : WalkingDao =
                TrainingDatabase.getDatabase(context.applicationContext).walkingDao

        val data : Walking? = walkerDao.getWalkingByTime(startTime).value

        val steps = data?.totalStep

        return steps
    }
}