package com.example.powerconsumptionapp.cpuinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUInfoBinding

class CPUInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentCPUInfoBinding>(
            inflater,
            R.layout.fragment_c_p_u_info,
            container,
            false
        )

        binding.apply {
            cpuInformationBttn.setOnClickListener {

            }
        }

        return binding.root

    }
}