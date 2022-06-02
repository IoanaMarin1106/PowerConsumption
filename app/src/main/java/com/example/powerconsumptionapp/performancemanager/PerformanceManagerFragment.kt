package com.example.powerconsumptionapp.performancemanager

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.ChoiceChipBinding
import com.example.powerconsumptionapp.databinding.FragmentPerformanceManagerBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import com.google.android.material.chip.Chip
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.RandomAccessFile

class PerformanceManagerFragment : Fragment() {
    private lateinit var binding: FragmentPerformanceManagerBinding
    private val cpuViewModel: CPUViewModel by activityViewModels()

    private lateinit var availableFrequencies: MutableList<String>
    private lateinit var availableCPUGovernors: MutableList<String>

    companion object {
        const val TAG = "FREQUENCY"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_performance_manager,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Thread {
                requireActivity().runOnUiThread {
                    // Get current CPU frequency
                    "Current CPU Frequency: ${cpuViewModel.getFreq(Constants.CURR_FREQ)} KHz".also {
                        currentFrequencyTextView.text = it
                    }
                }
            }.start()

            Thread {
                requireActivity().runOnUiThread {
                    // Get CPU governor
                    ("Scaling " + cpuViewModel.getCPUGovernor(Constants.CURRENT_SCALING_GOVERNOR)).also { currentGovernorTextView.text = it }
                }
            }.start()


            Thread {
                requireActivity().runOnUiThread {
                    // Get min CPU frequency
                    "${cpuViewModel.getFreq(Constants.MIN_FREQ)} KHz".also { minFrequencyTextView.text = it }
                }
            }.start()

            Thread {
                requireActivity().runOnUiThread {
                    // Get max CPU frequency
                    "${cpuViewModel.getFreq(Constants.MAX_FREQ)} KHz".also { maxFrequencyTextView.text = it }
                }
            }.start()


            Thread {
                requireActivity().runOnUiThread {
                    // Get available Frequencies
                    getAvailableFrequencies()
                }
            }.start()

            Thread {
                requireActivity().runOnUiThread {
                    // Get available governors
                    getAvailableGovernors()
                }
            }.start()
        }
    }

    private fun getAvailableGovernors() {
        availableCPUGovernors = cpuViewModel.getAvailableResources(Constants.AVAILABLE_GOVERNORS)
        for (governor  in availableCPUGovernors) {
            binding.availableGovernorsChipGroup.addView(createChip(governor, Constants.GOVERNOR))
        }
    }

    private fun getAvailableFrequencies() {
        availableFrequencies = cpuViewModel.getAvailableResources(Constants.AVAILABLE_FREQUENCIES)
        for (freq in availableFrequencies) {
            binding.apply {
                maxFreqChipGroup.addView(createChip(freq, Constants.MAX_CPU_FRQ))
                minFreqChipGroup.addView(createChip(freq, Constants.MIN_CPU_FRQ))
                currentFreqChipGroup.addView(createChip(freq, Constants.CURR_CPU_FRQ))
            }
        }
    }

    private fun createChip(chipTitle: String, resourceName: String): Chip {
        val chip = ChoiceChipBinding.inflate(layoutInflater).root
        when (resourceName) {
            Constants.MAX_CPU_FRQ -> {
                chip.apply {
                    "$chipTitle KHz".also { text = it }
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                    setOnCheckedChangeListener { chip, isChecked ->
                        if (isChecked) {
                            Log.i(TAG,"User will change CPU max frequency to ${chip.text}")
                            cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.MAX_CPU_FRQ)
                        }
                    }
                }
            }

            Constants.MIN_CPU_FRQ -> {
                chip.apply {
                    "$chipTitle KHz".also { text = it }
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                    setOnCheckedChangeListener { chip, isChecked ->
                        if (isChecked) {
                            Log.i(TAG,"User will change CPU min frequency to ${chip.text}")
                            cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.MIN_CPU_FRQ)
                        }
                    }
                }
            }

            Constants.CURR_CPU_FRQ -> {
                chip.apply {
                    "$chipTitle KHz".also { text = it }
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                    setOnCheckedChangeListener { chip, isChecked ->
                        if (isChecked) {
                            Log.i(TAG,"User will change current cpu frequency to ${chip.text}")
                            cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.CURR_CPU_FRQ)
                        }
                    }
                }
            }

            Constants.GOVERNOR -> {
                chip.apply {
                    text = chipTitle
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                    setOnCheckedChangeListener { chip, isChecked ->
                        if (isChecked) {
                            Log.i(TAG,"User will change CPU governor to ${chip.text}")
                            cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.GOVERNOR)
                        }
                    }
                }
            }
        }
        return chip
    }
}