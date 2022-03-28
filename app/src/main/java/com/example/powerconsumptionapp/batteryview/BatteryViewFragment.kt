package com.example.powerconsumptionapp.batteryview

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.startfragment.StarterFragment

class BatteryViewFragment : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    companion object {
        @JvmStatic var batteryStatus: Intent? = null
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

    private fun getBatteryStatus() {
        BatteryViewFragment.batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
        }
    }
}