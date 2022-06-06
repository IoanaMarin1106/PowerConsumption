package com.example.powerconsumptionapp.startfragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.service.ActionBatteryLowService
import eo.view.batterymeter.BatteryMeterView
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

class StarterFragment : Fragment() {

    private lateinit var batteryLevelIndicator: BatteryMeterView
    private val batteryViewModel: BatteryViewModel by activityViewModels()
    private lateinit var binding: FragmentStartBinding
    private var iFilter: IntentFilter? = IntentFilter()

    companion object {
        var isActiveActionBatteryService = false
        var isActiveAlarmLimitsService = false
//        var batteryPercentTimeMap: TreeMap<LocalDateTime, Int> = TreeMap<LocalDateTime, Int>()
    }

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

        // pornesc serviciul pentru a notifica user-ul cand bateria lui trebuie incarcata deoarece
        // a ajuns la un nivel prea scazut
        serviceHandler()
    }

    private fun serviceHandler() {
        if (!isActiveAlarmLimitsService) {
            if (!isActiveActionBatteryService) {
                isActiveActionBatteryService = true
                Log.i(ActionBatteryLowService.TAG, "Alarm service is loading...")
                val actionBatteryLowIntent = Intent(requireActivity().applicationContext, ActionBatteryLowService::class.java)
                requireActivity().startService(actionBatteryLowIntent)
            }
        }
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryPercent: Int? = intent?.let {
                val batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }

            checkBatteryPercent(batteryPercent)
            val chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            batteryLevelIndicator.apply {
                this.chargeLevel = batteryPercent
                this.isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING
            }
        }
    }

    private fun checkBatteryPercent(batteryPercent: Int?) {
        if (batteryPercent != null) {
            if (batteryPercent <= 15) binding.snuffBatteryIndicator!!.visibility =
                View.VISIBLE else binding.snuffBatteryIndicator!!.visibility = View.GONE
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    // Inflate options menu for this fragment
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.options_menu, menu)
//    }

    // Inflate options menu elements
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
//                super.onOptionsItemSelected(item)
//    }

    override fun onResume() {
        super.onResume()
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    override fun onPause() {
        requireActivity().applicationContext.unregisterReceiver(broadcastReceiver)
        super.onPause()
    }
}