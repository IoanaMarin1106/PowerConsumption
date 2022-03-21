package com.example.powerconsumptionapp.startfragment

import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class StartViewModel: ViewModel() {

    // Battery Characteristics
    var batteryLevel:Int  = 0
    var batteryPct:Int = 0
    var chargingStatus:Int = 0

    fun showBatteryInfo() {
        // Get battery Level
        val batteryProcent: Int? = StarterFragment.batteryStatus?.let {
            batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (batteryLevel * 100 / scale.toFloat())?.roundToInt()
        }

        batteryPct = batteryProcent!!
        chargingStatus = StarterFragment.batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    }
}