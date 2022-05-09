package com.example.powerconsumptionapp.model

import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.ButtonsInfo
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList
import kotlinx.coroutines.launch

class BatteryViewModel: ViewModel() {
    var ringtonesList: HashMap<String, Uri> = HashMap()

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

    fun listRingtones(activity: FragmentActivity) {
        val manager = RingtoneManager(activity)
        manager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor: Cursor = manager.cursor

        while (cursor.moveToNext()) {
            val title: String = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val ringtoneURI: Uri = manager.getRingtoneUri(cursor.position)
            // Do something with the title and the URI of ringtone
            ringtonesList[title] = ringtoneURI
        }
    }
}