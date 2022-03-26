package com.example.powerconsumptionapp.batteryview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding

class BatteryViewFragment : Fragment() {

    private lateinit var menu: HorizontalScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentBatteryViewBinding>(
            inflater,
            R.layout.fragment_battery_view,
            container,
            false
        )

        menu = binding.horizontalScrollMenu

        binding.apply {
            information.setOnClickListener {
                informationContainer?.visibility = View.VISIBLE
                saverContainer?.visibility = View.GONE
                levelStatisticsContainer?.visibility = View.GONE

            }

            saver.setOnClickListener {
                informationContainer?.visibility = View.GONE
                saverContainer?.visibility = View.VISIBLE
                levelStatisticsContainer?.visibility = View.GONE
            }

            batteryLevelStatistics?.setOnClickListener {
                informationContainer?.visibility = View.GONE
                saverContainer?.visibility = View.GONE
                levelStatisticsContainer?.visibility = View.VISIBLE

            }

            batteryTempStatistics?.setOnClickListener {
                Toast.makeText(context, "aiciaici", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }
}