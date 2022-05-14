package com.example.powerconsumptionapp.batteryview

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ankushyerwar.floatingsnackbar.SnackBar
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatterySettingsBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.service.NotificationService
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BatterySettingsFragment : Fragment() {

    private lateinit var binding: FragmentBatterySettingsBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()
    companion object {
        var reminderBatteryLevel: Int = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        reminderBatteryLevel = binding.batteryLevelReminder.value

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
                ((this.toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                    .toFloat() * 100).toInt().toString() + "%").also { ringtoneVolumeTextView.text = it }
            }

            ringtoneVolumeSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val seekBarProgress =
                        (progress.toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                            .toFloat()) * 100
                    "${seekBarProgress.toInt()}%".also { binding.ringtoneVolumeTextView.text = it }
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        progress,
                        AudioManager.FLAG_PLAY_SOUND
                    )
                }
            })

            setReminderChargerAdapterOff(chargerAdapterSwitch, reminderLayout)

            batteryLevelReminder.setOnValueChangedListener { _, _, newVal ->
                reminderBatteryLevel = newVal
            }

            startNotificationService.setOnClickListener {
                val serviceIntent = Intent(requireActivity().applicationContext, NotificationService::class.java)
                serviceIntent.putExtra(Constants.REMINDER_BATTERY_LEVEL, reminderBatteryLevel)
                requireActivity().applicationContext.startService(serviceIntent)
                SnackBar.success(view, "Charging battery reminder set to: $reminderBatteryLevel%", SnackBar.LENGTH_LONG).show();
            }

            alarmButton.setOnClickListener {
                if (alarmButton.text.equals(getString(R.string.ok))) {
                    alarmButton.text = getString(R.string.close)
                    Toast.makeText(context, "ceva chestie care se triggeruieste aici", Toast.LENGTH_SHORT).show()

                } else if (alarmButton.text.equals(getString(R.string.close))) {
                    alarmContainer.visibility = View.GONE
                    setAlarmButton.visibility = View.VISIBLE
                    alarmButton.text = getString(R.string.ok)
                }
            }
        }
    }

    private fun setReminderChargerAdapterOff(switch: SwitchMaterial, reminder: LinearLayout) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            reminder.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }
}