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
import androidx.lifecycle.ViewModelProvider
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.startfragment.StarterFragment

class BatteryViewFragment : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private lateinit var viewModel: BatteryViewModel

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

        // compute the battery intent
        getBatteryStatus()

        // Get the ViewModel or creat it
        viewModel = ViewModelProvider(this).get(BatteryViewModel::class.java)

        binding.apply {
            information.setOnClickListener {
                viewModel.containerHandler (
                    informationContainer!!,
                    saverContainer!!,
                    levelStatisticsContainer!!,
                    View.VISIBLE,
                    View.GONE,
                    View.GONE
                )
            }

            saver.setOnClickListener {
                viewModel.containerHandler(
                    informationContainer!!,
                    saverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
            }

            batteryLevelStatistics?.setOnClickListener {
                viewModel.containerHandler(
                    informationContainer!!,
                    saverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.GONE,
                    View.VISIBLE
                )

            }

            batteryTempStatistics?.setOnClickListener {
                Toast.makeText(context, "aici aici", Toast.LENGTH_SHORT).show()
            }

            bttn4?.setOnClickListener {
                Toast.makeText(context, "shkbcdeshbfclsedhbcfesw", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun getBatteryStatus() {
        StarterFragment.batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
        }
    }
}