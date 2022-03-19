package com.example.powerconsumptionapp.startfragment

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class StarterFragment : Fragment() {

    private lateinit var viewModel: StartViewModel
    private lateinit var binding: FragmentStartBinding

    companion object {
        @JvmStatic var batteryStatus:Intent? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentStartBinding>(
            inflater,
            R.layout.fragment_start,
            container,
            false
        )

        // Get battery status
        getBatteryStatus()

        // Get the ViewModel or creat it
        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)

        // Get battery info from the view model
        showBatteryInfo()

        binding.apply {
            showBatteryLevel.setOnClickListener {
                showChargingStatus()
            }

            batteryInfoButton.setOnClickListener {
                batteryView()
            }

        }

        // Setez optiunea de a avea un option menu
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun batteryView() {
        view?.findNavController()?.navigate(StarterFragmentDirections.actionStarterFragmentToBatteryViewFragment(viewModel.batteryPct))
    }

    private fun showBatteryInfo() {
        viewModel.showBatteryInfo()

        binding.tvBatteryPercentage.text = "${viewModel.batteryPct}%"
        viewModel.batteryPct?.let { binding.progressBar.setProgress(it) }

        ("Battery Level: ${viewModel.batteryLevel}\nBattery Percentage: ${viewModel.batteryPct}%").also { binding.textViewInfo.text = it }

        // Check if battery is charging or not
        if (viewModel.chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            binding.chargingBatteryImage.visibility = View.VISIBLE
        } else {
            binding.chargingBatteryImage.visibility = View.GONE
        }
    }

    private fun getBatteryStatus() {
        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
        }
    }

    private fun showChargingStatus() {
        with(lifecycleScope) {
            launch {
                var isCharging: String = ""

                // Get the device status
                when (viewModel.chargingStatus) {
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

                val message: String = "isCharging: ${isCharging} \n USB: ${usbCharge} \n " +
                        "AC Charge: ${acCharge} \n Wireless Charge: ${wirelessCharge}" +
                        "battery level: ${viewModel.batteryPct}"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
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