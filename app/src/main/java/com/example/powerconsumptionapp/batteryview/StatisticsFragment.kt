package com.example.powerconsumptionapp.batteryview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ankushyerwar.floatingsnackbar.SnackBar
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStatisticsBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.service.BatteryMonitoringService
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.format.DateTimeFormatter


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var series: LineGraphSeries<DataPoint>
    private lateinit var tempSeries: LineGraphSeries<DataPoint>
    private var lastX = 0.0
    private var tempLastX = 0.0

    private lateinit var batteryLevelGraph: GraphView
    private lateinit var batteryTemperatureGraph: GraphView

    companion object {
        @JvmStatic
        fun newInstance(): StatisticsFragment = StatisticsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_statistics,
            container,
            false
        )

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        batteryLevelGraph = binding.batteryLevelGraph
        batteryTemperatureGraph = binding.batteryTemperatureGraph

        binding.apply {
            if (MainActivity.isMonitoringServiceRunning) {
                startMonitoringService.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
            }

            startMonitoringService.setOnClickListener {
                if (it.backgroundTintList == ContextCompat.getColorStateList(requireContext(), R.color.gray)) {
                    SnackBar.warning(requireView(), Constants.MONITORING_SERVICE_RUNNING_WARNING, SnackBar.LENGTH_LONG, R.drawable.ic_baseline_system_security_update_warning_24).show();
                } else {
                    SnackBar.success(requireView(), Constants.MONITORING_SERVICE_RUNNING_SUCCESS, SnackBar.LENGTH_LONG).show();
                    MainActivity.isMonitoringServiceRunning = true
                    it.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
                    val monitoringService = Intent(context, BatteryMonitoringService::class.java)
                    requireActivity().applicationContext.startService(monitoringService)
                }
            }
            batteryLevelGraphHandler()
            batteryTemperatureGraphHandler()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun batteryTemperatureGraphHandler() {
        tempSeries = LineGraphSeries()
        BatteryViewModel.batteryTemperatureTimeMap.forEach { (_, value) ->
            tempSeries.appendData(DataPoint(tempLastX, value.toDouble()), false, Constants.MAX_DATA_POINTS)
            tempLastX += 4.0
        }

        batteryTemperatureGraph.addSeries(tempSeries)
        tempSeries.apply {
            isDrawDataPoints = true
            dataPointsRadius = 10.0F
        }

        batteryTemperatureGraph.viewport.apply {
            isYAxisBoundsManual = true
            isXAxisBoundsManual = true
            setMinY(0.0)
            setMaxY(50.0)
            isScrollable = true
            isScalable = true
        }

        batteryTemperatureGraph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
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

        if (BatteryViewModel.batteryTemperatureTimeMap.keys.size >= 2) {
            val staticLabelsFormatter = StaticLabelsFormatter(batteryTemperatureGraph)
            val labels = BatteryViewModel.batteryTemperatureTimeMap.keys.toTypedArray()
            val stringLabels: MutableList<String> = mutableListOf()
            for (element in labels) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formatted = element.format(formatter)
                stringLabels.add(formatted)
            }

            staticLabelsFormatter.setHorizontalLabels(
                stringLabels.toTypedArray()
            )
            batteryTemperatureGraph.gridLabelRenderer.labelFormatter = staticLabelsFormatter
        }

        batteryTemperatureGraph.gridLabelRenderer.apply {
            numHorizontalLabels = 4
            isHorizontalLabelsVisible = true
            horizontalAxisTitle = "Time"
            verticalAxisTitle = "Battery Temperature"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun batteryLevelGraphHandler() {
        series = LineGraphSeries()
        BatteryViewModel.batteryPercentTimeMap.forEach { (_, value) ->
            series.appendData(DataPoint(lastX, value.toDouble()), false, 2500)
            lastX += 4.0
        }
        batteryLevelGraph.addSeries(series)
        series.apply {
            isDrawDataPoints = true
            dataPointsRadius = 10.0F
        }

        batteryLevelGraph.viewport.apply {
            isYAxisBoundsManual = true
            isXAxisBoundsManual = true
            setMinY(0.0)
            setMaxY(100.0)
            isScrollable = true
            isScalable = true
        }

        batteryLevelGraph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
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

        if (BatteryViewModel.batteryPercentTimeMap.keys.size >= 2) {
            val staticLabelsFormatter = StaticLabelsFormatter(batteryLevelGraph)
            val labels = BatteryViewModel.batteryPercentTimeMap.keys.toTypedArray()
            val stringLabels: MutableList<String> = mutableListOf()
            for (element in labels) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formatted = element.format(formatter)
                stringLabels.add(formatted)
            }
            staticLabelsFormatter.setHorizontalLabels(
                stringLabels.toTypedArray()
            )
            batteryLevelGraph.gridLabelRenderer.labelFormatter = staticLabelsFormatter
        }

        batteryLevelGraph.gridLabelRenderer.apply {
            numHorizontalLabels = 4
            isHorizontalLabelsVisible = true
            horizontalAxisTitle = "Time"
            verticalAxisTitle = "Battery Procent"
        }
    }
}