package com.example.powerconsumptionapp.startfragment

import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.R
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

    fun populateFragmentButtons() {
        if (buttonsList.isEmpty()) {
            val batteryViewButton = ButtonsInfo(Util.Button.BATTERY_VIEW.title, R.drawable.battery_icon_2_)
            val cpuInfoButton = ButtonsInfo(Util.Button.CPU_INFO.title, R.drawable.cpu_icon)
            val performanceManagerBttn = ButtonsInfo(Util.Button.PERFORMANCE_MANAGER.title, R.drawable.performance_manager)
            val settingsBttn = ButtonsInfo(Util.Button.SETTINGS.title, R.drawable.ic_baseline_settings_24)

            buttonsList.apply {
                add(batteryViewButton)
                add(cpuInfoButton)
                add(performanceManagerBttn)
                add(settingsBttn)
            }
        }
    }
}