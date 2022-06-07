package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BatteryRemainingTimeService : Service() {

    private lateinit var processingThread: BatteryRemainingTimeThread

    companion object {
        const val TAG = "BATTERY_REMAINING_TIME_THREAD"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Battery remaining time service is started")
    }

    override fun onDestroy() {
        processingThread.stopThread()
        Log.i(TAG, "Battery remaining time service is destroyed")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processingThread = BatteryRemainingTimeThread(applicationContext)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}