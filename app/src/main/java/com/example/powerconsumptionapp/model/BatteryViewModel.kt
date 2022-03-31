package com.example.powerconsumptionapp.model

import android.os.BatteryManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.ButtonsInfo
import com.example.powerconsumptionapp.startfragment.StarterFragment
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList
import kotlin.math.roundToInt

class BatteryViewModel: ViewModel() {

    // Battery Characteristics
    private val _batteryLevel = MutableLiveData<Int>()
    val batteryLevel: LiveData<Int>
        get() = _batteryLevel

    private val _batteryPct = MutableLiveData<Int>()
    val batteryPct: LiveData<Int>
        get() = _batteryPct

    private val _procentage = MutableLiveData<Float>()
    val procentage: LiveData<Float>
        get() = _procentage

    private val _chargingStatus = MutableLiveData<Int>()
    val chargingStatus: LiveData<Int>
        get() = _chargingStatus


    fun showBatteryInfo() {
        // Get battery Level
        val batteryProcent: Int? = StarterFragment.batteryStatus?.let {
            _batteryLevel.value = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (_batteryLevel.value!! * 100 / scale.toFloat())?.roundToInt()
        }

        _batteryPct.value = batteryProcent!!
        _procentage.value = (batteryProcent.toFloat() / 100)
        _chargingStatus.value = StarterFragment.batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
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

    fun containerHandler(
        informationContainer: RelativeLayout,
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