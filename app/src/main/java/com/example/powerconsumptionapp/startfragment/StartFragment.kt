package com.example.powerconsumptionapp.startfragment

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding


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
        binding = DataBindingUtil.inflate (
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

        // Set fragments buttons
        viewModel.populateFragmentButtons()
        binding.recyclerViewFragmentStart.apply {
            layoutManager = GridLayoutManager(requireActivity().application, 2)
            adapter = CardAdapter(buttonsList, this@StarterFragment)
        }

        // Setez optiunea de a avea un option menu
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun showBatteryInfo() {
        viewModel.showBatteryInfo()

        // Set progress bar
        "${viewModel.batteryPct}%".also { binding.tvBatteryPercentage.text = it }
        viewModel.batteryPct.let { binding.progressBar.progress = it }

        // Check if battery is charging or not
        if (viewModel.chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            binding.apply {
                chargingBattery.visibility = View.VISIBLE
                tvBatteryPercentage.visibility = View.GONE
            }

        } else {
            binding.apply {
                chargingBattery.visibility = View.GONE
                tvBatteryPercentage.visibility = View.VISIBLE
            }
        }
    }

    private fun getBatteryStatus() {
        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
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
}