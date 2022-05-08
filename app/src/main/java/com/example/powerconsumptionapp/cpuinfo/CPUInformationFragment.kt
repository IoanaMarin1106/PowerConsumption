package com.example.powerconsumptionapp.cpuinfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUInfoBinding
import com.example.powerconsumptionapp.databinding.FragmentCPUInformationBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.Util
import com.example.powerconsumptionapp.model.CPUViewModel

class CPUInformationFragment : Fragment() {
    private lateinit var binding: FragmentCPUInformationBinding
    private lateinit var cpuViewModel: CPUViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_c_p_u_information,
            container,
            false
        )

        cpuViewModel = ViewModelProvider(this).get(CPUViewModel::class.java)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cpuInformationFragment = this@CPUInformationFragment
            viewModel = cpuViewModel

            cpuCoresTextViewValue.text = cpuViewModel.getNumberOfCores().toString()

            val (modelName, model_freq) = cpuViewModel.getCpuModelName()
            cpuFreqTextViewValue.text = model_freq
            cpuHardwareTextViewValue.text = modelName

            cpuHardwareTextViewValue.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/#q=" + "${cpuHardwareTextViewValue.text}@${cpuFreqTextViewValue}")
                    )
                )
            }

            if (cpuViewModel.getCpuTemp() != 0) {
                "${cpuViewModel.getCpuTemp()}${Constants.CELSIUS_DEGREES}".also { cpuTemperatureTextView.text = it }
                cpuTemperatureTextView.hint = Constants.CELSIUS_DEGREES

                cpuTemperatureTextView.setOnClickListener {
                    val degrees = cpuTemperatureTextView.text.toString().filter { it.isDigit() }.toInt()

                    if (cpuTemperatureTextView.hint.equals(Constants.CELSIUS_DEGREES)) {
                        "${Util.convertCelsiusToFahrenheit(degrees)}${Constants.FAHRENHEIT_DEGREES}".also { cpuTemperatureTextView.text = it }
                        cpuTemperatureTextView.hint = Constants.FAHRENHEIT_DEGREES
                    } else if (cpuTemperatureTextView.hint.equals(Constants.FAHRENHEIT_DEGREES)) {
                        "${Util.convertFahrenheitToCelsius(degrees)}${Constants.CELSIUS_DEGREES}".also { cpuTemperatureTextView.text = it }
                        cpuTemperatureTextView.hint = Constants.CELSIUS_DEGREES
                    }
                }
            } else {
                cpuTemperatureTextView.text = "-"
            }

            cpuUsageProgressbar.progress = cpuViewModel.getLoadAvg().toFloat()

            if (cpuViewModel.getFreq(Constants.CURR_FREQ) == 0) {
                cpuCurrFrequencyTextView.text = "-"
            } else {
                cpuCurrFrequencyTextView.text = cpuViewModel.getFreq(Constants.CURR_FREQ).toString()
            }

            if (cpuViewModel.getFreq(Constants.MIN_FREQ) == 0) {
                cpuMinFrequencyTextView.text = "-"
            } else {
                cpuMinFrequencyTextView.text = cpuViewModel.getFreq(Constants.MIN_FREQ).toString()
            }

            if (cpuViewModel.getFreq(Constants.MAX_FREQ) == 0) {
                cpuMaxFrequencyTextView.text = "-"
            } else {
                cpuMaxFrequencyTextView.text = cpuViewModel.getFreq(Constants.MAX_FREQ).toString()
            }

            cpuViewModel.populateGridLayoutItems(cpuCoresTextViewValue.text.toString().toInt())

            binding.cpuInfoRecyclerView.apply {
                layoutManager = GridLayoutManager(requireActivity().application, 1)
                adapter = ItemAdapter(itemsList)
            }

            cpuCurrFrequencyTextView.setOnClickListener {
                cpuViewModel.showDialog((activity as MainActivity), Constants.CURR_CPU_FRQ, Constants.CURR_CPU_FRQ_DESC)
            }

            cpuMaxFrequencyTextView.setOnClickListener {
                cpuViewModel.showDialog((activity as MainActivity), Constants.MAX_CPU_FRQ, Constants.MAX_MIN_CPU_FREQ_DESC)
            }

            cpuMinFrequencyTextView.setOnClickListener {
                cpuViewModel.showDialog((activity as MainActivity), Constants.MIN_CPU_FRQ, Constants.MAX_MIN_CPU_FREQ_DESC)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CPUInformationFragment()
    }
}