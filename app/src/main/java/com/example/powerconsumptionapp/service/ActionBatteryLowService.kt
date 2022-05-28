package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ActionBatteryLowService : Service() {

    private lateinit var processingThread: ActionBatteryThread

    companion object {
        const val TAG = "ACTION_BATTERY_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Alarm service is started")
    }

    override fun onDestroy() {
        processingThread.stopThread()
        Log.i(TAG, "Alarm service is destroyed")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processingThread = ActionBatteryThread(applicationContext)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}