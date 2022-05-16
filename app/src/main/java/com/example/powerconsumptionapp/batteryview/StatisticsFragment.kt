package com.example.powerconsumptionapp.batteryview

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentStatisticsBinding
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var series: LineGraphSeries<DataPoint>
    private var lastX: Double = 2.0
    private var handler: Handler = Handler()

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

        val graph = binding.batteryGraph as GraphView
        series = LineGraphSeries(
            arrayOf(
                DataPoint(Date(), 25.0),
                DataPoint(Date(), 30.0),
                DataPoint(Date(), 20.0),
                DataPoint(Date(), 60.0),
                DataPoint(Date(), 60.0),
            )
        )
        graph.addSeries(series)
        graph.gridLabelRenderer.numHorizontalLabels = 3

        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val sdf = SimpleDateFormat("hh:mm:ss")
                    return sdf.format(value)
                } else {
                    return super.formatLabel(value, isValueX) + "%"
                }
            }
        }

        // customize graph view
        var viewport: Viewport = graph.viewport
        viewport.isScalable = true
        viewport.setScalableY(true)
        viewport.isYAxisBoundsManual = true
        viewport.setMinY(0.0)
        viewport.setMaxY(100.0)

//        addDataPoint()

        binding.hourButton.setOnClickListener {
            val sdf = SimpleDateFormat("hh:mm")
            val currentDate = sdf.format(Date())
            Toast.makeText(context, currentDate.toString(), Toast.LENGTH_SHORT).show()
        }

        val graph2 = binding.batteryGraph1 as GraphView
        val series2: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)
            )
        )
        graph2.addSeries(series2)
    }

//    //add data to the graph
//    private fun addDataPoint() {
//       handler.postDelayed({
//           lastX += 0.5
//           series.appendData(DataPoint(lastX, 4.0), false, 100)
//           addDataPoint()
//       }, 1000)
//    }
}