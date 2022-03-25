package com.example.powerconsumptionapp.startfragment

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStartBinding


class StarterFragment : Fragment() {

    lateinit var viewModel: StartViewModel
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
        binding.tvBatteryPercentage.text = "${viewModel.batteryPct}%"
        viewModel.batteryPct?.let { binding.progressBar.setProgress(it) }

        // Check if battery is charging or not
//        if (viewModel.chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
//            binding.chargingBatteryImage.visibility = View.VISIBLE
//        } else {
//            binding.chargingBatteryImage.visibility = View.GONE
//        }
    }

    private fun getBatteryStatus() {
        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
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