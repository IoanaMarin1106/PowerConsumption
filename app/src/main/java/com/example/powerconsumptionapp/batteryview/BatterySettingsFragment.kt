package com.example.powerconsumptionapp.batteryview

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatterySettingsBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.shawnlin.numberpicker.NumberPicker
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class BatterySettingsFragment() : Fragment() {

    private lateinit var binding: FragmentBatterySettingsBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()
    companion object {
        var reminderBatteryLevel: Int = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_battery_settings,
            container,
            false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            launch {
                // List available ringtones
                batteryViewModel.listRingtones(requireActivity())
            }
        }

        runBlocking {
            launch {
                //set ringtone alarm
                batteryViewModel.setAlarmRingtone(requireContext(), binding.ringtoneSpinner)
            }
        }

        // Set spinner list
        binding.apply {
            Thread {
                // settings for up & down limit for battery level percentage
                requireActivity().runOnUiThread {
                    setAlarmButton.setOnClickListener {
                        it.visibility = View.GONE
                        alarmContainer.visibility = View.VISIBLE
                    }
                }
            }.start()
        }

        val audioManager: AudioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        binding.apply {
            ringtoneVolumeSeekbar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.getStreamVolume(AudioManager.STREAM_ALARM).apply {
                ringtoneVolumeSeekbar.progress = this
                ringtoneVolumeTextView.text = "${
                    ((this.toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                        .toFloat()) * 100).toInt()
                }%"
            }

            ringtoneVolumeSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // TODO Auto-generated method stub
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // TODO Auto-generated method stub
                }

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val seekBarProgress =
                        (progress.toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                            .toFloat()) * 100
                    binding.ringtoneVolumeTextView.text = "${seekBarProgress.toInt()}%"
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        progress,
                        AudioManager.FLAG_PLAY_SOUND
                    )
                }
            })

            setReminderChargerAdapterOff(chargerAdapterSwitch, batteryLevelReminder)

            batteryLevelReminder.setOnValueChangedListener { _, _, newVal ->
                reminderBatteryLevel = newVal
            }
        }
    }

    private fun setReminderChargerAdapterOff(switch: SwitchMaterial, levelReminder: NumberPicker) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            levelReminder.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

}