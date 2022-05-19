package com.example.powerconsumptionapp.cpuinfo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ankushyerwar.floatingsnackbar.SnackBar
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUStatisticsBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import com.example.powerconsumptionapp.service.CPUMonitoringService
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.format.DateTimeFormatter

class CPUStatisticsFragment : Fragment() {

    private lateinit var binding: FragmentCPUStatisticsBinding

    private lateinit var cpuTemperatureGraph: GraphView
    private lateinit var coresGraph: GraphView
    private lateinit var cpuLoadGraph: GraphView

    private lateinit var tempSeries: LineGraphSeries<DataPoint>
    private var tempLastX: Double = 0.0

    private lateinit var loadSeries: LineGraphSeries<DataPoint>
    private var loadLastX: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_c_p_u_statistics,
            container,
            false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cpuTemperatureGraph = binding.cpuTemperatureGraph
        coresGraph = binding.coresGraph
        cpuLoadGraph = binding.cpuLoadGraph

        binding.apply {
            if (MainActivity.isCPUMonitoringServiceRunning) {
                startCpuMonitoringService.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
            }

            startCpuMonitoringService.setOnClickListener {
                if (it.backgroundTintList == ContextCompat.getColorStateList(requireContext(), R.color.gray)) {
                    SnackBar.warning(requireView(), Constants.CPU_MONITORING_SERVICE_RUNNING_WARNING, SnackBar.LENGTH_LONG, R.drawable.ic_baseline_system_security_update_warning_24).show();
                } else {
                    it.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
                    startCpuService(requireActivity().applicationContext)
                    SnackBar.success(requireView(), Constants.CPU_MONITORING_MESSAGE_ACTIVATED, SnackBar.LENGTH_LONG).show()
                    MainActivity.isCPUMonitoringServiceRunning = true
                }
            }
        }
        cpuTemperatureGraphHandler()
        cpuLoadGraphHandler()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cpuLoadGraphHandler() {
        loadSeries = LineGraphSeries<DataPoint>()

        CPUViewModel.cpuLoadTimeMap.forEach { (_, value) ->
            loadSeries.appendData(DataPoint(loadLastX, value.toDouble()), false, Constants.MAX_DATA_POINTS)
            loadLastX += 4.0
        }

        cpuLoadGraph.addSeries(loadSeries)
        loadSeries.apply {
            isDrawDataPoints = true
            dataPointsRadius = 10.0F
        }

        cpuLoadGraph.viewport.apply {
            isYAxisBoundsManual = true
            isXAxisBoundsManual = true
            setMinY(0.0)
            setMaxY(100.0)
            isScrollable = true
            isScalable = true
        }

        cpuLoadGraph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // show normal x values
                    super.formatLabel(value, isValueX)
                } else {
                    // show currency for y values
                    super.formatLabel(value, isValueX) + "%"
                }
            }
        }

        if (CPUViewModel.cpuLoadTimeMap.keys.size >= 2) {
            val staticLabelsFormatter = StaticLabelsFormatter(cpuLoadGraph)
            val labels = CPUViewModel.cpuLoadTimeMap.keys.toTypedArray()
            val stringLabels: MutableList<String> = mutableListOf()
            for (element in labels) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formatted = element.format(formatter)
                stringLabels.add(formatted)
            }

            staticLabelsFormatter.setHorizontalLabels(
                stringLabels.toTypedArray()
            )
            cpuLoadGraph.gridLabelRenderer.labelFormatter = staticLabelsFormatter
        }

        cpuLoadGraph.gridLabelRenderer.apply {
            numHorizontalLabels = 4
            isHorizontalLabelsVisible = true
            horizontalAxisTitle = getString(R.string.time)
            verticalAxisTitle = getString(R.string.cpu_load)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cpuTemperatureGraphHandler() {
        tempSeries = LineGraphSeries<DataPoint>()

        CPUViewModel.cpuTemperatureTimeMap.forEach { (_, value) ->
            tempSeries.appendData(DataPoint(tempLastX, value.toDouble()), false, Constants.MAX_DATA_POINTS)
            tempLastX += 4.0
        }

        cpuTemperatureGraph.addSeries(tempSeries)
        tempSeries.apply {
            isDrawDataPoints = true
            dataPointsRadius = 10.0F
        }

        cpuTemperatureGraph.viewport.apply {
            isYAxisBoundsManual = true
            isXAxisBoundsManual = true
            setMinY(0.0)
            setMaxY(50.0)
            isScrollable = true
            isScalable = true
        }

        cpuTemperatureGraph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // show normal x values
                    super.formatLabel(value, isValueX)
                } else {
                    // show currency for y values
                    super.formatLabel(value, isValueX) + Constants.CELSIUS_DEGREES
                }
            }
        }

        if (CPUViewModel.cpuTemperatureTimeMap.keys.size >= 2) {
            val staticLabelsFormatter = StaticLabelsFormatter(cpuTemperatureGraph)
            val labels = CPUViewModel.cpuTemperatureTimeMap.keys.toTypedArray()
            val stringLabels: MutableList<String> = mutableListOf()
            for (element in labels) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formatted = element.format(formatter)
                stringLabels.add(formatted)
            }

            staticLabelsFormatter.setHorizontalLabels(
                stringLabels.toTypedArray()
            )
            cpuTemperatureGraph.gridLabelRenderer.labelFormatter = staticLabelsFormatter
        }

        cpuTemperatureGraph.gridLabelRenderer.apply {
            numHorizontalLabels = 4
            isHorizontalLabelsVisible = true
            horizontalAxisTitle = getString(R.string.time)
            verticalAxisTitle = getString(R.string.cpu_temperature)
        }
    }

    private fun startCpuService(applicationContext: Context) {
        val cpuMonitoringService = Intent(applicationContext, CPUMonitoringService::class.java)
        applicationContext.startService(cpuMonitoringService)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CPUStatisticsFragment()
    }
}