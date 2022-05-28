package com.example.powerconsumptionapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.powerconsumptionapp.startfragment.StarterFragment

class ActionBatteryThread(private var applicationContext: Context) : Thread() {

    var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()

    companion object {
        const val TAG = "ACTION_BATTERY_SERVICE"
        const val SLEEP_TIME: Long = 4000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        while (isRunning) {
            Log.i(TAG,"${currentThread()} is running.")
            getBatteryStatus()
            sleep(SLEEP_TIME)
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action.equals(Intent.ACTION_BATTERY_LOW)) {
                Log.i(TAG,"Battery has 15% battery.")
            }
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        iFilter!!.addAction(Intent.ACTION_BATTERY_LOW)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    fun stopThread() {
        isRunning = false
    }
}