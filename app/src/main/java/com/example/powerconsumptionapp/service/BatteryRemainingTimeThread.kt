package com.example.powerconsumptionapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.powerconsumptionapp.model.BatteryViewModel
import kotlin.math.roundToInt

class BatteryRemainingTimeThread(private var applicationContext: Context) : Thread() {
    var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()
    private var batteryPercentage: Int? = 0

    private var firstLevel = 0
    private var secondLevel = 0

    companion object {
        const val TAG = "BATTERY_REMAINING_TIME_SERVICE"
        const val SLEEP_TIME: Long = 10000
    }

    init {
        getBatteryStatus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        while (isRunning) {
            Log.i(TAG,"${currentThread()} is running.")

            // Get battery status
            getBatteryStatus()

            firstLevel = batteryPercentage!!
            Log.i(TAG, "------- $firstLevel ---------")

            sleep(SLEEP_TIME)

            secondLevel = batteryPercentage!!
            BatteryViewModel.batteryPercentageEstimation.add(secondLevel)

            Log.i(TAG, "------- $secondLevel ---------")
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            batteryPercentage = intent?.let {
                val batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    fun stopThread() {
        isRunning = false
    }
}