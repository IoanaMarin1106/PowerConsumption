package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class StartActivityService : Service() {

    private lateinit var processingThread: ProcessingStartActivityThread

    companion object {
        const val TAG = "SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service is started")
    }

    override fun onDestroy() {
        processingThread.stopThread()
        Log.i(TAG, "Service is destroyed")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processingThread = ProcessingStartActivityThread(applicationContext)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}