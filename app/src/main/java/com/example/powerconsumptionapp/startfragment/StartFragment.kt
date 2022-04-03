package com.example.powerconsumptionapp.startfragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import eo.view.batterymeter.BatteryMeterView
import kotlin.math.roundToInt

class StarterFragment() : Fragment() {

    private lateinit var batteryLevelIndicator: BatteryMeterView

    private val batteryViewModel: BatteryViewModel by activityViewModels()
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_start,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            startFragment = this@StarterFragment
            viewModel = batteryViewModel
        }

        batteryLevelIndicator = binding.batteryLevelIndicator

        // Get battery status
        getBatteryStatus()

        // Set fragments buttons
        batteryViewModel.populateFragmentButtons()
        binding.recyclerViewFragmentStart.apply {
            layoutManager = GridLayoutManager(requireActivity().application, 2)
            adapter = CardAdapter(buttonsList, this@StarterFragment)
        }

        // Setez optiunea de a avea un option menu
        setHasOptionsMenu(true)
    }

    private fun getBatteryStatus() {
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context?.registerReceiver(broadcastReceiver, ifilter)
        }
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryProcent: Int? = intent?.let {
                var batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }

            val chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            batteryLevelIndicator.apply {
                this.chargeLevel = batteryProcent
                this.isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING
            }
        }
    }

    // Inflate options menu for this fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    // Inflate options menu elements
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        context?.unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}