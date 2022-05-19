package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.powerconsumptionapp.model.CPUViewModel

class CPUMonitoringService : Service() {
    lateinit var processingThread: CPUMonitoringThread

    companion object {
        const val TAG = "SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "CPU monitoring service is started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processingThread = CPUMonitoringThread()
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        processingThread.stopThread()
        Log.i(BatteryMonitoringService.TAG, "CPU monitoring service is destroyed")
        super.onDestroy()
    }

}