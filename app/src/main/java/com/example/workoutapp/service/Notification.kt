package com.example.workoutapp.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.SchedulerDao
import com.example.workoutapp.tracker.TrackerFragment


public class Alarm : BroadcastReceiver() {


    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {


        Log.d("ALARM", "RECEIVEEE")
        val start : String = intent.getIntExtra("start", -1).toString()
        Log.d("start", start)
        val trainingType : String? = intent.getStringExtra("exercise")
        var target : String = intent.getIntExtra("target", 0).toString()
        if (trainingType == "Walking"){
            target = target + " steps"
        } else {
            target = target + " kms"
        }
        var builder = NotificationCompat.Builder(context, "channel")
            .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("menu", "tracking")
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), intent, 0)
        val notificationManager :  NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (start == "1"){
            builder.setContentTitle("Starting your exercise...")
                .setContentText( trainingType + " exercise with target: " + target)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


        } else {
            builder.setContentTitle("Ending your exercise...")
                .setContentText( trainingType + " exercise with your target: " + target)
                .setAutoCancel(true)

        }
        notificationManager.notify(System.currentTimeMillis().toInt(),builder.build())

    }


    fun cancelAlarm(context: Context) {
        val intent = Intent(context, Alarm::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}