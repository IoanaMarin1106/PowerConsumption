package com.example.powerconsumptionapp.batteryview


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.BatteryManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import java.util.*
import kotlin.math.roundToInt

class BatteryViewFragment() : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    private var screenWidth: Int = 0

    private lateinit var tvBatteryPercentage: TextView
    private lateinit var batteryLevelIndicator: ProgressBar
    private lateinit var chargingBattery: ImageView

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

        tvBatteryPercentage = binding.tvBatteryPercentage
        batteryLevelIndicator = binding.batteryLevelIndicator
        chargingBattery = binding.chargingBattery

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            batteryFragment = this@BatteryViewFragment
            viewModel = batteryViewModel
        }

        // compute the battery intent
        getBatteryStatus()

        binding.apply {
            batteryInfoBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.VISIBLE,
                    View.GONE,
                    View.GONE
                )
            }

            batterySaverBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
            }

            batteryLevelStatisticsBttn?.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.GONE,
                    View.VISIBLE
                )

            }

            batteryTempStatisticsBttn?.setOnClickListener {
                Toast.makeText(context, "aici aici", Toast.LENGTH_SHORT).show()
            }

            bttn4?.setOnClickListener {
                Toast.makeText(context, "shkbcdeshbfclsedhbcfesw", Toast.LENGTH_SHORT).show()
            }
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

            tvBatteryPercentage.text = batteryProcent.toString()
            batteryLevelIndicator.progress = batteryProcent!!
            if (chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                chargingBattery.visibility = View.VISIBLE
                tvBatteryPercentage.visibility = View.GONE
            } else {
                chargingBattery.visibility = View.GONE
                tvBatteryPercentage.visibility = View.VISIBLE
            }
        }
    }

    private fun getBatteryStatus() {
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(broadcastReceiver, it)
        }
    }

    override fun onDestroy() {
        context?.unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}