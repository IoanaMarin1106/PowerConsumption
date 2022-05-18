package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.powerconsumptionapp.general.Constants

class BatteryMonitoringService : Service() {
    lateinit var processingThread: BatteryMonitoringThread

    companion object {
        const val TAG = "SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Monitoring service is started")
    }

    override fun onDestroy() {
        processingThread.stopThread()
        Log.i(TAG, "Service is destroyed")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processingThread = BatteryMonitoringThread(applicationContext)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}