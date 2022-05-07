package com.example.powerconsumptionapp.model

import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.ButtonsInfo
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList

class BatteryViewModel: ViewModel() {
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