package com.example.powerconsumptionapp.batteryview

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.cpuinfo.InfoDialogFragment
import com.example.powerconsumptionapp.databinding.FragmentOptimizerBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class OptimizerFragment : Fragment() {

    private lateinit var binding: FragmentOptimizerBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    // Optimizer container elements
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var textPercentage: TextView
    private var brightness = 0
    private val maxBrightness = Constants.MAX_BRIGHTNESS
    private lateinit var timeoutSpinner: Spinner
    private lateinit var contentResolver: ContentResolver
    private lateinit var window: Window
    private lateinit var turnOffDozeModeSwitch: SwitchMaterial

    companion object {
        @JvmStatic
        fun newInstance(): OptimizerFragment = OptimizerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_optimizer,
            container,
            false
        )

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       //  Assign class properties to XML attributes
        assignClassProperties()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            optimizerFragment = this@OptimizerFragment
            viewModel = batteryViewModel
        }

        Thread {
            requireActivity().runOnUiThread {
                // Setup brightness control to reduce battery consumption
                brightnessControlHandler()
            }
        }.start()

        Thread {
            requireActivity().runOnUiThread {
                // Setup the timeout spinner
                spinnerHandler()
            }
        }.start()

        Thread {
            requireActivity().runOnUiThread {
                dozeModeHandler()
            }
        }.start()
    }

    private fun assignClassProperties() {
        brightnessSeekBar = binding.brightnessSeekBar
        textPercentage = binding.txtPercentage
        timeoutSpinner = binding.timeoutSpinner
        contentResolver = this.requireContext().contentResolver
        window = this.requireActivity().window
        turnOffDozeModeSwitch = binding.turnOffDozeModeSwitch
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        context.startActivity(intent)
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
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightness
                )
                val lp: WindowManager.LayoutParams = window.attributes
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
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pwrm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pwrm.isIgnoringBatteryOptimizations(name)
        }
        return true
    }

    private fun turnOffDozeMode() {  //you can use with or without passing context
        val intent = Intent()
        val packageName = requireActivity().applicationContext.packageName
        val pm = requireActivity().applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(packageName)) {// if you want to disable doze mode for this package
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        } else { // if you want to enable doze mode
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        requireActivity().applicationContext.startActivity(intent)
        MainActivity.isInDozeMode = false
    }

    private fun dozeModeHandler() {
        turnOffDozeModeSwitch.isChecked = !MainActivity.isInDozeMode

        turnOffDozeModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                batteryViewModel.showDialog((activity as MainActivity), getString(R.string.doze_mode_title), getString(R.string.doze_mode_warning))
                binding.dozeModeInfoText.visibility = View.VISIBLE
            } else {
                binding.dozeModeInfoText.visibility = View.GONE
            }
            turnOffDozeMode()
        }
    }
}