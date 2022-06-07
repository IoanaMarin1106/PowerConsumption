package com.example.powerconsumptionapp.performancemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ankushyerwar.floatingsnackbar.SnackBar
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.ChoiceChipBinding
import com.example.powerconsumptionapp.databinding.FragmentPerformanceManagerBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

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
            refreshButton.setOnClickListener {
                refreshButtonHandler()
            }

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
            }
        }
    }

    private fun createChip(chipTitle: String, resourceName: String): Chip {
        val chip = ChoiceChipBinding.inflate(layoutInflater).root
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.deep_purple_dark)
        when (resourceName) {
            Constants.MAX_CPU_FRQ -> {
                chip.apply {
                    "$chipTitle KHz".also { text = it }
                    isCheckedIconVisible = true
                    setTextAppearanceResource(R.style.ChipTextStyle)
                    setOnCheckedChangeListener { chip, isChecked ->
                        if (isChecked) {
                            val minFreq = binding.minFrequencyTextView.text.toString().trim().split("\\s+".toRegex()).toTypedArray()[0].toDouble()
                            if (minFreq > chipTitle.toDouble()) {
                                // User cannot change max freq in order to be smaller than min freq => ERROR
                                showError(getString(R.string.error_when_changing_freq))
                                chip.isChecked = false
                            } else {
                                Log.i(TAG,"User will change CPU max frequency to ${chip.text}")

                                // Alert user about the refresh button
                                cpuViewModel.showDialog((activity as MainActivity), getString(R.string.refresh), getString(R.string.refresh_alert_msg))

                                if (binding.refreshButton.visibility == View.GONE) {
                                    binding.refreshButton.visibility = View.VISIBLE
                                }
                                cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.MAX_CPU_FRQ)
                            }
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
                            val maxFreq = binding.maxFrequencyTextView.text.toString().trim().split("\\s+".toRegex()).toTypedArray()[0].toDouble()
                            if (maxFreq < chipTitle.toDouble()) {
                                // User cannot change max freq in order to be smaller than min freq => ERROR
                                showError(getString(R.string.error_when_changing_freq))
                                chip.isChecked = false
                            } else {
                                Log.i(TAG,"User will change CPU min frequency to ${chip.text}")

                                // Alert user about the refresh button
                                cpuViewModel.showDialog((activity as MainActivity), getString(R.string.refresh), getString(R.string.refresh_alert_msg))

                                if (binding.refreshButton.visibility == View.GONE) {
                                    binding.refreshButton.visibility = View.VISIBLE
                                }
                                cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.MIN_CPU_FRQ)
                            }
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

                            // Alert user about the refresh button
                            cpuViewModel.showDialog((activity as MainActivity), getString(R.string.refresh), getString(R.string.refresh_alert_msg))

                            if (binding.refreshButton.visibility == View.GONE) {
                                binding.refreshButton.visibility = View.VISIBLE
                            }
                            cpuViewModel.cpuSettingsHandler(chip.text.toString(), Constants.GOVERNOR)
                        }
                    }
                }
            }
        }
        return chip
    }

    private fun refreshButtonHandler() {
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

        }
    }

    private fun showError(errorMessage: String) {
        SnackBar.error(requireView(), errorMessage , SnackBar.LENGTH_LONG, R.drawable.ic_baseline_error_24).show();
    }
}