package com.example.powerconsumptionapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.BatteryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class BatteryMonitoringThread(
    private var applicationContext: Context
): Thread() {

    var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()
    private var batteryPercent: Int? = 0
    private var batteryTemperature: Int = 0

    init {
        getBatteryStatus()
    }

    companion object {
        const val TAG = "SERVICE"
        const val SLEEP_TIME: Long = 4000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        while (isRunning) {

            // Get battery percent
            getBatteryStatus()

            // Get current time
            val currentTime = LocalDateTime.now()
            BatteryViewModel.batteryPercentTimeMap[currentTime] = batteryPercent!!
            BatteryViewModel.batteryTemperatureTimeMap[currentTime] = batteryTemperature
            sleep(SLEEP_TIME)
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            batteryPercent = intent?.let {
                val batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }

            val batteryTemp = intent!!.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            batteryTemperature = (batteryTemp.div(10)).toFloat().roundToInt()
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