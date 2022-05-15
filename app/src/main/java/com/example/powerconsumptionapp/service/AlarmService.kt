package com.example.powerconsumptionapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.powerconsumptionapp.general.Constants

class AlarmService : Service() {
    private lateinit var processingThread: ProcessingAlarmThread

    companion object {
        const val TAG = "ALARM_SERVICE"
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
        val bottomLimit: Int = intent!!.getIntExtra(Constants.BOTTOM_LIMIT, 0)
        val upperLimit: Int = intent!!.getIntExtra(Constants.UPPER_LIMIT, 100)
        processingThread = ProcessingAlarmThread(applicationContext, bottomLimit, upperLimit)
        processingThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}