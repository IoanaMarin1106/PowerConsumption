package com.example.powerconsumptionapp.performancemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.contentcapture.ContentCaptureSession
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.ChoiceChipBinding
import com.example.powerconsumptionapp.databinding.FragmentCPUInformationBinding
import com.example.powerconsumptionapp.databinding.FragmentPerformanceManagerBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.slider.Slider
import org.w3c.dom.Text
import kotlin.concurrent.thread

class PerformanceManagerFragment : Fragment() {
    private lateinit var binding: FragmentPerformanceManagerBinding
    private val cpuViewModel: CPUViewModel by activityViewModels()

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
                    "Current CPU Frequency: ${cpuViewModel.getFreq(Constants.CURR_FREQ)} KHz".also { currentFrequencyTextView.text = it }

                    // Get CPU governor
                    ("Scaling " + cpuViewModel.getCPUGovernor(Constants.CURRENT_SCALING_GOVERNOR)).also { currentGovernorTextView.text = it }

                    // Get min CPU frequency
                    "${cpuViewModel.getFreq(Constants.MIN_FREQ)} KHz".also { minFrequencyTextView.text = it }

                    // Get max CPU frequency
                    "${cpuViewModel.getFreq(Constants.MAX_FREQ)} KHz".also { maxFrequencyTextView.text = it }

                    // Get available Frequencies
                    getAvailableFrequencies()

                    // Get available governors
                    getAvailableGovernors()
                }
            }.start()


        }

    }

    private fun getAvailableGovernors() {
        val availableGovernors = cpuViewModel.getAvailableResources(Constants.AVAILABLE_GOVERNORS)
        for (governor  in availableGovernors) {
            binding.availableGovernorsChipGroup.addView(createChip(governor, Constants.GOVERNOR))
        }
    }

    private fun getAvailableFrequencies() {
        val availableFreq = cpuViewModel.getAvailableResources(Constants.AVAILABLE_FREQUENCIES)
        for (freq in availableFreq) {
            binding.apply {
                maxFreqChipGroup.addView(createChip(freq, Constants.FREQUENCY))
                minFreqChipGroup.addView(createChip(freq, Constants.FREQUENCY))
            }
        }
    }

    private fun createChip(chipTitle: String, resourceName: String): Chip {
        val chip = ChoiceChipBinding.inflate(layoutInflater).root
        when (resourceName) {
            Constants.FREQUENCY -> {
                chip.apply {
                    "$chipTitle KHz".also { text = it }
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                }
            }

            Constants.GOVERNOR -> {
                chip.apply {
                    text = chipTitle
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                }
            }
        }
        return chip
    }
}