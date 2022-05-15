package com.example.powerconsumptionapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.general.Constants

class ProcessingStartActivityThread(private var applicationContext: Context) : Thread() {

    private var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()

    companion object {
        const val TAG = "SERVICE"
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_POWER_CONNECTED == intent!!.action) {
                Log.i(TAG, "AC Connected by onReceive");
                // turn on app via using Intent
                val startActivityIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                applicationContext.startActivity(startActivityIntent)
            } else if (Intent.ACTION_POWER_DISCONNECTED == intent!!.action) {
                Log.i(TAG, "AC Disconnected by onReceive");
            }
        }
    }

    private fun setIntentFilter() {
        iFilter!!.addAction(Intent.ACTION_POWER_CONNECTED)
        iFilter!!.addAction(Intent.ACTION_POWER_DISCONNECTED)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    override fun run() {
        while (isRunning) {
            setIntentFilter()
            sleep(1000)
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    fun stopThread() {
        isRunning = false
    }
}