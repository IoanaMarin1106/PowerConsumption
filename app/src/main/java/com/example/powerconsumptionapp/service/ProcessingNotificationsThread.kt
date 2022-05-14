package com.example.powerconsumptionapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.general.Constants.CHANNEL_ID

class ProcessingNotificationsThread(
    private var applicationContext: Context,
    private var reminderBatteryLevel: Int
): Thread() {
    private var isRunning = true

    override fun run() {
        while (isRunning) {
            sendNotification()
            sleep(10000)
        }
        Log.i("STARTED_SERVICE","${Thread.currentThread()} has finished his job.")
    }

    private fun sendNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager!!.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }

    fun stopThread() {
        isRunning = false
    }
}