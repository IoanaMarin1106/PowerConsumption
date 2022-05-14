package com.example.powerconsumptionapp.batteryview

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentInformationBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.Util
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.service.NotificationService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


class InformationFragment : Fragment() {

    private lateinit var binding: FragmentInformationBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()
    private var iFilter: IntentFilter? = IntentFilter()

    // Battery level
    private lateinit var tvBatteryPercentage: TextView
    private lateinit var batteryLevelIndicator: ProgressBar
    private lateinit var chargingBattery: ImageView

    // Battery temperature
    private lateinit var temperatureProgressBar: ProgressBar
    private lateinit var batteryTemperatureTextView: TextView
    private lateinit var temperatureFeedbackImage: ImageView


    // Grid Elements - Battery properties
    private lateinit var statusValueTextView: TextView
    private lateinit var voltageValueTextView: TextView
    private lateinit var healthValueTextView: TextView
    private lateinit var temperatureValueTextView: TextView
    private lateinit var powerValueTextView: TextView
    private lateinit var levelValueTextView: TextView

    private lateinit var acChargerOption: Button
    private lateinit var wirelessChargerOption: Button
    private lateinit var usbChargerOption: Button

    private var areChargingOptionsVisible: Boolean = false


    companion object {
        @JvmStatic
        fun newInstance(): InformationFragment = InformationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_information,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign class properties to XML attributes
        assignClassProperties()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            informationFragment = this@InformationFragment
            viewModel = batteryViewModel
        }

        runBlocking {
            launch {
                // Get battery attributes with action_battery_changed
                getBatteryStatus()
            }
        }

        Thread {
            this.requireActivity().runOnUiThread(java.lang.Runnable {
                binding.apply {
                    tempProgressBarElements.setOnClickListener {
                        // If current format is Celsius -> convert to Fahrenheit
                        if (batteryTemperatureTextView.hint.equals(Constants.CELSIUS_DEGREES)) {
                            convertCelsiusToFahrenheit()
                        } else if (batteryTemperatureTextView.hint.equals(Constants.FAHRENHEIT_DEGREES)) {
                            convertFahrenheitToCelsius()
                        }
                    }

                    chargeMenuButton.setOnClickListener {
                        if (!areChargingOptionsVisible) {
                            areChargingOptionsVisible = true
                            chargeOptionsHorizotalScrollView.visibility = View.VISIBLE
                        } else {
                            areChargingOptionsVisible = false
                            chargeOptionsHorizotalScrollView.visibility = View.GONE
                        }
                    }
                }
            })
        }.start()
    }

    private fun assignClassProperties() {
        tvBatteryPercentage = binding.tvBatteryPercentage
        batteryLevelIndicator = binding.batteryLevelIndicator
        chargingBattery = binding.chargingBattery

        temperatureProgressBar = binding.temperatureProgressBar
        temperatureFeedbackImage = binding.temperatureFeedbackImage
        batteryTemperatureTextView = binding.batteryTemperatureTextView

        statusValueTextView = binding.statusValueTextView
        voltageValueTextView = binding.voltageValueTextView
        healthValueTextView = binding.healthValueTextView
        temperatureValueTextView = binding.temperatureValueTextView
        powerValueTextView = binding.powerValueTextView
        levelValueTextView = binding.levelValueTextView

        acChargerOption = binding.acChargerOption
        wirelessChargerOption = binding.wirelessChargerOption
        usbChargerOption = binding.usbChargerOption
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryProcent: Int? = intent?.let {
                var batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }

            var chargingStatus: Int = 0

            runBlocking {
                launch {
                    // Get charging status
                    chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                    setBatteryStatus(chargingStatus)
                }

                launch {
                    // Set battery percentage
                    setBatteryPercentage(batteryProcent, chargingStatus)
                }

                launch {
                    // Get charge plug / if it plugged in or not
                    val chargePlug: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
                    setPowerAdapter(chargePlug)
                }

                launch {
                    // Get battery temperature
                    val batteryTemperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    setBatteryTemperature(batteryTemperature)
                }

                launch {
                    // Get battery health
                    val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
                    setBatteryHealth(health)
                }

                launch {
                    // Get battery voltage
                    val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    setBatteryVoltage(voltage)
                }
            }
        }
    }

    private suspend fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    private suspend fun setBatteryVoltage(voltage: Int?) {
        voltageValueTextView.text = "$voltage mv"
    }

    private suspend fun setBatteryHealth(health: Int?) {
        when (health) {
            BatteryManager.BATTERY_HEALTH_COLD -> healthValueTextView.text = Constants.COLD
            BatteryManager.BATTERY_HEALTH_DEAD -> healthValueTextView.text = Constants.DEAD
            BatteryManager.BATTERY_HEALTH_GOOD -> healthValueTextView.text = Constants.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> healthValueTextView.text = Constants.OVERHEAT
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> healthValueTextView.text = Constants.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> healthValueTextView.text = Constants.UNKNOWN
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> healthValueTextView.text = Constants.UNSPECIFIED_FAILURE
        }
    }

    private suspend fun setPowerAdapter(chargePlug: Int) {
        when(chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> {
                powerValueTextView.text = Constants.AC_CHARGER
                acChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                powerValueTextView.text = Constants.WIRELESS
                wirelessChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryManager.BATTERY_PLUGGED_USB ->  {
                powerValueTextView.text = Constants.USB
                wirelessChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }
            else -> powerValueTextView.text = Constants.NONE
        }
    }

    private suspend fun setBatteryStatus(chargingStatus: Int) {
        when(chargingStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> statusValueTextView.text = Constants.CHARGING
            BatteryManager.BATTERY_STATUS_UNKNOWN -> statusValueTextView.text = Constants.UNKNOWN
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusValueTextView.text = Constants.NOT_CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> statusValueTextView.text = Constants.FULL
            BatteryManager.BATTERY_STATUS_DISCHARGING -> statusValueTextView.text = Constants.DISCHARGING
        }
    }

    private suspend fun setBatteryTemperature(batteryTemperature: Int?) {
        (batteryTemperature?.div(10))?.toFloat()!!.roundToInt().also {
            temperatureProgressBar.progress = it
            batteryTemperatureTextView.apply {
                text = "$it${Constants.CELSIUS_DEGREES}/${Constants.HIGH_CELSIUS_TEMPERATURE}${Constants.CELSIUS_DEGREES}\""
                hint = Constants.CELSIUS_DEGREES
            }
            temperatureValueTextView.text = it.toString()

            if (it > Constants.HIGH_CELSIUS_TEMPERATURE) {
                temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_warning_24)
            } else if (it < Constants.NORMAL_CELSIUS_TEMPERATURE) {
                temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_done_all_24)
            } else {
                temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_back_hand_24)
            }
        }
    }

    private suspend fun setBatteryPercentage(batteryProcent: Int?, chargingStatus: Int) {
        "${batteryProcent.toString()}%".also {
            tvBatteryPercentage.text = it
            levelValueTextView.text = it
        }
        batteryLevelIndicator.progress = batteryProcent!!

        if (chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            chargingBattery.visibility = View.VISIBLE
            tvBatteryPercentage.visibility = View.GONE
        } else {
            chargingBattery.visibility = View.GONE
            tvBatteryPercentage.visibility = View.VISIBLE
        }
    }

    private fun convertFahrenheitToCelsius() {
        val celsiusTemp = temperatureProgressBar.progress
        batteryTemperatureTextView.apply {
            text = "$celsiusTemp${Constants.CELSIUS_DEGREES}/${Constants.HIGH_CELSIUS_TEMPERATURE}${Constants.CELSIUS_DEGREES}"
            hint = Constants.CELSIUS_DEGREES
        }
        temperatureValueTextView.text = "$celsiusTemp${Constants.CELSIUS_DEGREES}"
    }

    private fun convertCelsiusToFahrenheit() {
        val fahrenheitTemp = Util.convertCelsiusToFahrenheit(temperatureProgressBar.progress)
        batteryTemperatureTextView.apply {
            text = "$fahrenheitTemp${Constants.FAHRENHEIT_DEGREES}/${Constants.HIGH_FAHRENHEIT_TEMPERATURE}${Constants.FAHRENHEIT_DEGREES}"
            hint = Constants.FAHRENHEIT_DEGREES
        }
        temperatureValueTextView.text = "$fahrenheitTemp${Constants.FAHRENHEIT_DEGREES}"
    }

    override fun onResume() {
        super.onResume()
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    override fun onPause() {
        requireActivity().applicationContext.unregisterReceiver(broadcastReceiver)
        super.onPause()
    }
}