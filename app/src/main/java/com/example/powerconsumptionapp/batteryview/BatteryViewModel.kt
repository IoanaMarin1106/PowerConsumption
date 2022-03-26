package com.example.powerconsumptionapp.batteryview

import android.os.BatteryManager
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.startfragment.StarterFragment
import kotlin.math.roundToInt

class BatteryViewModel: ViewModel() {

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

    fun containerHandler(
        informationContainer: LinearLayout,
        saverContainer: LinearLayout,
        levelStatisticsContainer: LinearLayout,
        infoVisibility: Int,
        saverVisibility: Int,
        statsVisibility: Int

    ) {
        infoVisibility.also { informationContainer.visibility = it }
        saverVisibility.also { saverContainer.visibility = it }
        statsVisibility.also { levelStatisticsContainer.visibility = it }
    }
}