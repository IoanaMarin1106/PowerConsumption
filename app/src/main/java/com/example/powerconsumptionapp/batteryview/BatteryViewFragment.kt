package com.example.powerconsumptionapp.batteryview

import android.content.*
import android.content.res.Resources
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.Util
import com.example.powerconsumptionapp.model.BatteryViewModel
import java.util.*
import kotlin.math.roundToInt
import android.view.WindowManager
import androidx.annotation.MenuRes
import androidx.appcompat.widget.AppCompatButton
import android.graphics.drawable.Drawable
import android.media.Image
import android.opengl.Visibility
import android.os.PowerManager

import android.widget.FrameLayout

import android.widget.PopupWindow

import android.widget.TextView

import android.view.LayoutInflater
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.MenuPopupWindow
import com.google.android.material.button.MaterialButton
import com.madrapps.plot.line.LinePlot


class BatteryViewFragment() : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    private var screenWidth: Int = 0

    private var iFilter: IntentFilter? = IntentFilter()

    // Battery level
    private lateinit var tvBatteryPercentage: TextView
    private lateinit var batteryLevelIndicator: ProgressBar
    private lateinit var chargingBattery: ImageView
    private var hasACCharger: Boolean = false
    private var hasUSBCharger: Boolean = false
    private var hasWirelessCharger: Boolean = false

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

    // Saver container elements
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var textPercentage: TextView
    private var brightness = 0
    private val maxBrightness = Constants.MAX_BRIGHTNESS
    private lateinit var timeoutSpinner: Spinner
    private lateinit var contentResolver: ContentResolver
    private lateinit var window: Window
    private lateinit var acChargerOption: Button
    private lateinit var wirelessChargerOption: Button
    private lateinit var usbChargerOption: Button

    private var areChargingOptionsVisible: Boolean = false

    init {
        screenWidth = Resources.getSystem().displayMetrics.widthPixels / 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_battery_view,
            container,
            false
        )

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign class properties to XML attributes
        assignClassProperties()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            batteryFragment = this@BatteryViewFragment
            viewModel = batteryViewModel
        }

        // Get battery attributes with action_battery_changed
        getBatteryStatus()

        binding.apply {
            batteryInfoBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer,
                    batterySaverContainer,
                    statisticsContainer,
                    View.VISIBLE,
                    View.GONE,
                    View.GONE
                )
            }

            batterySaverBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer,
                    batterySaverContainer,
                    statisticsContainer,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
            }

            batteryLevelStatisticsBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer,
                    batterySaverContainer,
                    statisticsContainer,
                    View.GONE,
                    View.GONE,
                    View.VISIBLE
                )

            }

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

        // Setup brightness control to reduce battery consumption
        brightnessControlHandler()

        // Setup the timeout spinner
        spinnerHandler()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun brightnessControlHandler() {
        // handle brightness control
        // Check whether has the write settings permission or not.
        val settingsCanWrite = hasWriteSettingsPermission(this.requireContext())
        if (!settingsCanWrite) {
            changeWriteSettingsPermission(this.requireContext())
        }

        brightnessSeekBar.apply {
            max = 255
            keyProgressIncrement = 1
        }

        brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        brightnessSeekBar.progress = brightness
        brightnessSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress <= 20) {
                    brightness = 20
                } else {
                    brightness = progress
                }

                val percentage = (brightness / maxBrightness.toFloat()) * 100
                textPercentage.text = "${percentage.toInt()}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
                val lp : WindowManager.LayoutParams = window.attributes
                lp.screenBrightness = brightness / maxBrightness.toFloat()
                window.attributes = lp
            }

        })
    }

    private fun spinnerHandler() {
        val timeoutTimes = resources.getStringArray(R.array.Times)
        val adapter = object: ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, timeoutTimes) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setTextColor(Color.GRAY)
                }
                return view
            }
        }

        timeoutSpinner.adapter = adapter
        timeoutSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {

                var timeout = 180000
                when(timeoutTimes[position]) {
                    "15 seconds" -> timeout = 15000
                    "30 seconds" -> timeout = 30000
                    "1 minute" -> timeout = 60000
                    "2 minutes" -> timeout = 120000
                    "10 minutes" -> timeout = 600000
                    "30 minutes" -> timeout = 180000
                }
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        println("ieeeei " + context?.let { isIgnoringBatteryOptimizations(it) })
    }

    // Check whether this app has android write settings permission.
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasWriteSettingsPermission(context: Context): Boolean {
        var ret = true
        // Get the result from below code.
        ret = Settings.System.canWrite(context)
        return ret
    }

    // Start can modify system settings panel to let user change the write
    // settings permission.
    private fun changeWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        context.startActivity(intent)
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

        brightnessSeekBar = binding.brightnessSeekBar!!
        textPercentage = binding.txtPercentage!!
        timeoutSpinner = binding.timeoutSpinner
        contentResolver = this.requireContext().contentResolver
        window = this.requireActivity().window

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

            // Get charging status
            val chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            setBatteryStatus(chargingStatus)

            // Set battery percentage
            setBatteryPercentage(batteryProcent, chargingStatus)

            // Get charge plug / if it plugged in or not
            val chargePlug: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            setPowerAdapter(chargePlug)

            // Get battery temperature
            val batteryTemperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            setBatteryTemperature(batteryTemperature)

            val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            setBatteryHealth(health)

            val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            setBatteryVoltage(voltage)
        }
    }

    private fun setBatteryVoltage(voltage: Int?) {
        voltageValueTextView.text = "${voltage} mv"
    }

    private fun setBatteryHealth(health: Int?) {
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

    private fun setPowerAdapter(chargePlug: Int) {
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

    private fun setBatteryStatus(chargingStatus: Int) {
        when(chargingStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> statusValueTextView.text = Constants.CHARGING
            BatteryManager.BATTERY_STATUS_UNKNOWN -> statusValueTextView.text = Constants.UNKNOWN
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusValueTextView.text = Constants.NOT_CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> statusValueTextView.text = Constants.FULL
            BatteryManager.BATTERY_STATUS_DISCHARGING -> statusValueTextView.text = Constants.DISCHARGING
        }
    }

    private fun setBatteryTemperature(batteryTemperature: Int?) {
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

    private fun setBatteryPercentage(batteryProcent: Int?, chargingStatus: Int) {
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

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pwrm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pwrm.isIgnoringBatteryOptimizations(name)
        }
        return true
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


