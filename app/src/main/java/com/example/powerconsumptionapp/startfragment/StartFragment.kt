package com.example.powerconsumptionapp.startfragment

import android.content.Intent
import android.content.IntentFilter
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

class StarterFragment : Fragment() {

    private val batteryViewModel: BatteryViewModel by activityViewModels()
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            startFragment = this@StarterFragment
            viewModel = batteryViewModel
        }

        // Get battery status
        getBatteryStatus()

        // Get battery info from the view model
        Toast.makeText(context,"${R.attr.batteryMeterChargeLevel}" , Toast.LENGTH_LONG).show()

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