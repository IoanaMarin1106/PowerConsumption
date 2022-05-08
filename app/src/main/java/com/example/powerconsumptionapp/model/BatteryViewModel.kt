package com.example.powerconsumptionapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.ButtonsInfo
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList
import kotlinx.coroutines.launch

class BatteryViewModel: ViewModel() {
    fun populateFragmentButtons() {
        viewModelScope.launch {
            if (buttonsList.isEmpty()) {
                val batteryViewButton = ButtonsInfo(Util.Button.BATTERY_VIEW.title, R.drawable.battery_icon_2_)
                val cpuInfoButton = ButtonsInfo(Util.Button.CPU_INFO.title, R.drawable.cpu_icon)
                val performanceManagerBttn = ButtonsInfo(Util.Button.PERFORMANCE_MANAGER.title, R.drawable.performance_manager)

                buttonsList.apply {
                    add(batteryViewButton)
                    add(cpuInfoButton)
                    add(performanceManagerBttn)
                }
            }
        }
    }
}