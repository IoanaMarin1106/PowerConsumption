package com.example.powerconsumptionapp.cpuinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUInfoBinding
import com.example.powerconsumptionapp.model.CPUViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cpuFragment = this@CPUInfoFragment
            viewModel = cpuViewModel
        }

        binding.apply {
            cpuInformationBttn.setOnClickListener {
                cpuViewModel.containerHandler(cpuInfoContainer, cpuStatsContainer, View.VISIBLE, View.GONE)
            }

            cpuStatisticsBttn.setOnClickListener {
                cpuViewModel.containerHandler(cpuInfoContainer, cpuStatsContainer, View.GONE, View.VISIBLE)
            }
        }
    }
}