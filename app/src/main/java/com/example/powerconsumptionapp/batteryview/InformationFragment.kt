package com.example.powerconsumptionapp.batteryview

import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ankushyerwar.floatingsnackbar.SnackBar
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentInformationBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.LoadingAlertDialog
import com.example.powerconsumptionapp.general.Util
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.google.android.material.button.MaterialButton
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
    private val progressDialog = LoadingAlertDialog()

    companion object {
        @JvmStatic
        fun newInstance(): InformationFragment = InformationFragment()
        var standbyBucketDescription = ""
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

    @RequiresApi(Build.VERSION_CODES.P)
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

                    appStandbyBucketButton.apply {
                        appStandbyBucketHandler((requireActivity().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager).appStandbyBucket, this)
                        setOnClickListener {
//                                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            batteryViewModel.showDialog((activity as MainActivity), this.text.toString(), standbyBucketDescription)
                        }
                    }

                    remainingTimeButton.setOnClickListener {
                        if (BatteryViewModel.batteryPercentageEstimation.size < 12) {
                            Util.showDialog((requireActivity() as MainActivity), getString(R.string.no_data_warning_title), getString(R.string.no_data_warning_msg))
                        } else {
                            if (remainingTimeInfoCard.visibility == View.GONE) {
                                // Show progress dialog without Title
                                progressDialog.show(requireContext())
                                Handler(Looper.getMainLooper()).postDelayed({
                                    // Dismiss progress bar after 4 seconds
                                    progressDialog.dialog.dismiss()
                                }, 4000)

                                remainingTimeButton.background.alpha = 0
                                remainingTimeInfoCard.visibility = View.VISIBLE

                                // Get battery time until next charge (approx)

                                // App was opened -> click button (time)
                                val remainingTime = batteryViewModel.estimateTimeRemaining()
                                when {
                                    (remainingTime >= 60) and (remainingTime < 3600) -> {
                                        val minutes = remainingTime / 60
                                        val seconds = remainingTime - (minutes * 60)
                                        "$minutes minutes $seconds seconds".also { timeTextView.text = it }
                                    }
                                    remainingTime < 60 -> {
                                        "$remainingTime seconds".also { timeTextView.text = it }
                                    }
                                    remainingTime >= 3600 -> {
                                        val hours = remainingTime / 3600
                                        val minutes = (remainingTime - (hours * 3600)) / 60
                                        "$hours hours $minutes minutes".also { timeTextView.text = it }
                                    }
                                }
                            } else {
                                remainingTimeInfoCard.visibility = View.GONE
                                remainingTimeButton.background.alpha = 255
                            }
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
            val batteryPercentage: Int? = intent?.let {
                val batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
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
                    setBatteryPercentage(batteryPercentage, chargingStatus)
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

    private fun appStandbyBucketHandler(appStandbyBucket: Int, standbyBucketButton: MaterialButton) {
        when (appStandbyBucket) {
            UsageStatsManager.STANDBY_BUCKET_ACTIVE -> {
                standbyBucketButton.text = getString(R.string.active)
                standbyBucketDescription = getString(R.string.active_info)
            }
            UsageStatsManager.STANDBY_BUCKET_WORKING_SET -> {
                standbyBucketButton.text = getString(R.string.working_set)
                standbyBucketDescription = getString(R.string.working_set_info)
            }
            UsageStatsManager.STANDBY_BUCKET_FREQUENT -> {
                standbyBucketButton.text = getString(R.string.frequent)
                standbyBucketDescription = getString(R.string.frequent_info)
            }
            UsageStatsManager.STANDBY_BUCKET_RARE -> {
                standbyBucketButton.text = getString(R.string.rare)
                standbyBucketDescription = getString(R.string.rare_info)
            }
            UsageStatsManager.STANDBY_BUCKET_RESTRICTED -> {
                standbyBucketButton.text = getString(R.string.restricted)
                standbyBucketDescription = getString(R.string.restricted_info)
            }
            else -> view?.let { SnackBar.error(it, "Error accessing app standby bucket!", SnackBar.LENGTH_LONG).show() }
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    private suspend fun setBatteryVoltage(voltage: Int?) {
        "$voltage mv".also { voltageValueTextView.text = it }
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
        println(BatteryViewModel.acCharger)
        when {
            BatteryViewModel.acCharger -> {
                println("aiciiiii")
                acChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryViewModel.wirelessCharger -> {
                println("aici + 1")
                wirelessChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryViewModel.usbCharger -> {
                println("aici + 2")
                usbChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }
        }

        when(chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> {
                powerValueTextView.text = Constants.AC_CHARGER
                BatteryViewModel.acCharger = true
                acChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                powerValueTextView.text = Constants.WIRELESS
                BatteryViewModel.wirelessCharger = true
                wirelessChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
            }

            BatteryManager.BATTERY_PLUGGED_USB ->  {
                powerValueTextView.text = Constants.USB
                BatteryViewModel.usbCharger = true
                usbChargerOption.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
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
                "$it${Constants.CELSIUS_DEGREES}/${Constants.HIGH_CELSIUS_TEMPERATURE}${Constants.CELSIUS_DEGREES}\"".also { text = it }
                hint = Constants.CELSIUS_DEGREES
            }
            temperatureValueTextView.text = it.toString()

            when {
                it > Constants.HIGH_CELSIUS_TEMPERATURE -> {
                    temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_warning_24)
                }
                it < Constants.NORMAL_CELSIUS_TEMPERATURE -> {
                    temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_done_all_24)
                }
                else -> {
                    temperatureFeedbackImage.setBackgroundResource(R.drawable.ic_baseline_back_hand_24)
                }
            }
        }
    }

    private suspend fun setBatteryPercentage(batteryPercent: Int?, chargingStatus: Int) {
        "${batteryPercent.toString()}%".also {
            tvBatteryPercentage.text = it
            levelValueTextView.text = it
        }
        batteryLevelIndicator.progress = batteryPercent!!

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
            "$celsiusTemp${Constants.CELSIUS_DEGREES}/${Constants.HIGH_CELSIUS_TEMPERATURE}${Constants.CELSIUS_DEGREES}".also { text = it }
            hint = Constants.CELSIUS_DEGREES
        }
        "$celsiusTemp${Constants.CELSIUS_DEGREES}".also { temperatureValueTextView.text = it }
    }

    private fun convertCelsiusToFahrenheit() {
        val fahrenheitTemp = Util.convertCelsiusToFahrenheit(temperatureProgressBar.progress)
        batteryTemperatureTextView.apply {
            "$fahrenheitTemp${Constants.FAHRENHEIT_DEGREES}/${Constants.HIGH_FAHRENHEIT_TEMPERATURE}${Constants.FAHRENHEIT_DEGREES}".also { text = it }
            hint = Constants.FAHRENHEIT_DEGREES
        }
        "$fahrenheitTemp${Constants.FAHRENHEIT_DEGREES}".also { temperatureValueTextView.text = it }
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