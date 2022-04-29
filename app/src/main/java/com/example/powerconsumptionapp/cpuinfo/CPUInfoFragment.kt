package com.example.powerconsumptionapp.cpuinfo

import android.os.Build
import android.os.Bundle
import android.os.HardwarePropertiesManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUInfoBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import com.example.powerconsumptionapp.startfragment.buttonsList

class CPUInfoFragment : Fragment() {

    private lateinit var binding: FragmentCPUInfoBinding
    private val cpuViewModel: CPUViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentCPUInfoBinding>(
            inflater,
            R.layout.fragment_c_p_u_info,
            container,
            false
        )

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cpuFragment = this@CPUInfoFragment
            viewModel = cpuViewModel
        }

        binding.apply {
            cpuInformationBttn.setOnClickListener {
                cpuViewModel.containerHandler(
                    cpuInfoContainer, cpuStatsContainer, View.VISIBLE, View.GONE)
            }

            cpuStatisticsBttn.setOnClickListener {
                cpuViewModel.containerHandler(cpuInfoContainer, cpuStatsContainer, View.GONE, View.VISIBLE)
            }

            cpuCoresTextViewValue.text = cpuViewModel.getNumberOfCores().toString()
            cpuHardwareTextViewValue.text = Build.HARDWARE
            "${cpuViewModel.getCpuTemp()}${Constants.CELSIUS_DEGREES}".also { cpuTemperatureTextView.text = it }

            cpuUsageProgressbar?.progress = cpuViewModel.getLoadAvg().toFloat()
            Toast.makeText(context, cpuViewModel.getLoadAvg().toFloat().toString(), Toast.LENGTH_SHORT).show()

            cpuCurrFrequencyTextView?.text = cpuViewModel.getFreq(Constants.CURR_FREQ).toString()
            cpuMaxFrequencyTextViewValue?.text = cpuViewModel.getFreq(Constants.MAX_FREQ).toString()
            cpuMinFrequencyTextView?.text = cpuViewModel.getFreq(Constants.MIN_FREQ).toString()

            cpuViewModel.getCoresLoad(cpuCoresTextViewValue.text.toString().toInt())

            cpuViewModel.populateGridLayoutItems(cpuCoresTextViewValue.text.toString().toInt())
            binding.cpuInfoRecyclerView?.apply {
                layoutManager = GridLayoutManager(requireActivity().application, 1)
                adapter = ItemAdapter(itemsList)
            }
        }

    }
}