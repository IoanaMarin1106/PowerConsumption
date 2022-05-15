package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.powerconsumptionapp.general.Constants

class NotificationService : Service() {
    private lateinit var processingThread: ProcessingNotificationsThread

    companion object {
        const val TAG = "SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service is started")
    }

    override fun onDestroy() {
        super.onDestroy()
        processingThread.stopThread()
        Log.i(TAG, "Service is destroyed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderBatteryLevel: Int = intent!!.getIntExtra(Constants.REMINDER_BATTERY_LEVEL, -1)
        if (reminderBatteryLevel == -1) {
            Log.i(TAG, "Error: Reminder battery level: -1")
        }
        processingThread = ProcessingNotificationsThread(applicationContext, reminderBatteryLevel)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}