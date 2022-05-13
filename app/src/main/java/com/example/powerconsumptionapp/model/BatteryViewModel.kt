package com.example.powerconsumptionapp.model

import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.ButtonsInfo
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList
import com.google.android.material.switchmaterial.SwitchMaterial
import com.shawnlin.numberpicker.NumberPicker
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

    fun setAlarmRingtone(context: Context, ringtoneSpinner: Spinner) {
        val items = ringtonesList.keys.toTypedArray()
        val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        ringtoneSpinner.adapter = adapter
        ringtoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_ALARM,
                    Uri.parse(ringtonesList[items[position]].toString()));
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun playAlarm(context: Context) {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtoneSound: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        ringtoneSound.play()
    }
}