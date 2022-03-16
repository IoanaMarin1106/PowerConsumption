package com.example.powerconsumptionapp.startfragment

import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.BroadcastReceiver
import android.content.Context
import android.icu.number.Scale
import kotlin.math.roundToInt


class StarterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentStartBinding>(
            inflater,
            R.layout.fragment_start,
            container,
            false
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
                context?.registerReceiver(null, it)
            }

            var level: Int = 0
            var scale: Int = 0

            // Get battery Level
            val batteryPct: Int? = batteryStatus?.let {
                level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (level * 100 / scale.toFloat())?.roundToInt()
            }

            ("Battery Scale: $scale\nBattery Level: $level\nBattery Percentage: $batteryPct%").also { binding.textViewInfo.text = it }
            binding.tvBatteryPercentage.text = "${batteryPct}%"
            batteryPct?.let { binding.progressBar.setProgress(it) }

        }

        binding.showBatteryLevel.setOnClickListener {
            with(lifecycleScope) {
                launch {
                    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
                        context?.registerReceiver(null, it)
                    }
                    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                    var isCharging: String = ""

                    // Get the device status
                    when (status) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> isCharging = "CHARGING"
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> isCharging = "DISCHARGING"
                        BatteryManager.BATTERY_STATUS_FULL -> isCharging = "FULL"
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> isCharging = "NOT CHARGING"
                        BatteryManager.BATTERY_STATUS_UNKNOWN -> isCharging = "UNKNOWN"
                        else -> "NOT AVAILABLE"
                    }

                    // How are we charging?
                    val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
                    val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
                    val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
                    val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS

                    // Get battery Level
                    val batteryPct: Float? = batteryStatus?.let {
                        val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        level * 100 / scale.toFloat()
                    }

                    val message: String = "isCharging: ${isCharging} \n USB: ${usbCharge} \n " +
                            "AC Charge: ${acCharge} \n Wireless Charge: ${wirelessCharge}" +
                            "battery level: ${batteryPct}"
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }

//        binding.apply {
//            startFragmentButton.setOnClickListener {
//                it.findNavController().navigate(R.id.action_starterFragment_to_batteryViewFragment)
//            }
//
//            cpuInfoButton.setOnClickListener {
//                it.findNavController().navigate(R.id.action_starterFragment_to_CPUInfo)
//            }
//
//            perfomanceManagerButton.setOnClickListener {
//                it.findNavController().navigate(R.id.action_starterFragment_to_performanceManagerFragment)
//            }
//        }

        // Setez optiunea de a avea un option menu
        setHasOptionsMenu(true)

        return binding.root
    }

    // facem inflate pe options_menu.xml
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    // click pe fiecare element din options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}