package com.example.powerconsumptionapp.batteryview

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatterySettingsBinding
import com.example.powerconsumptionapp.model.BatteryViewModel


class BatterySettingsFragment : Fragment() {

    private lateinit var binding: FragmentBatterySettingsBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_battery_settings,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List available ringtones
        batteryViewModel.listRingtones(requireActivity())

        // Set spinner list
        binding.apply {
            val items = batteryViewModel.ringtonesList.keys.toTypedArray()
            val adapter:ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            ringtoneSpinner.adapter = adapter
            ringtoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View?, position: Int, id: Long) {
                    RingtoneManager.setActualDefaultRingtoneUri(
                        requireContext(),
                        RingtoneManager.TYPE_ALARM,
                        Uri.parse(batteryViewModel.ringtonesList[items[position]].toString()));
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        }
    }
}