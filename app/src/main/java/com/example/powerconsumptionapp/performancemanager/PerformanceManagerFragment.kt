package com.example.powerconsumptionapp.performancemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentPerformanceManagerBinding

class PerformanceManagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentPerformanceManagerBinding>(
            inflater,
            R.layout.fragment_performance_manager,
            container,
            false
        )

        return binding.root
    }
}