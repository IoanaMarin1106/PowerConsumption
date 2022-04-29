package com.example.powerconsumptionapp.model

import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.cpuinfo.GridItem
import com.example.powerconsumptionapp.cpuinfo.itemsList
import com.example.powerconsumptionapp.general.Constants
import java.io.File
import java.io.RandomAccessFile
import java.util.regex.Pattern
import kotlin.math.roundToInt

class CPUViewModel: ViewModel() {
    fun containerHandler(
        cpuInfoContainer: ScrollView,
        statisticsContainer: LinearLayout,
        cpuInfoVisibility: Int,
        statisticsVisibility: Int
    ) {
        cpuInfoVisibility.also { cpuInfoContainer.visibility = it }
        statisticsVisibility.also { statisticsContainer.visibility = it }
    }

    // Get the CPU cores from /sys/devices/system/cpu/
    fun getNumberOfCores(): Int? {
        return File(Constants.CPU_CORES_PATH).listFiles { pathname ->
            Pattern.matches("cpu[0-9]", pathname!!.name)
        }?.size
    }

    fun getCpuTemp(): Int {
        val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
        return (reader.readLine().toInt() / 1000)
    }

    fun getLoadAvg() : Int {
        val reader = RandomAccessFile(Constants.CPU_LOADAVG, "r")
        val line = reader.readLine()
        val words = line.split("\\s".toRegex()).toTypedArray()
        return (words[2].toFloat() * 100).roundToInt()
    }

    fun getFreq(file: String) : Int {
//        val reader = RandomAccessFile(file, "r")
//        val line = reader.readLine()
//        if (line != null) {
//            return (line.toInt() / 1000)
//        } else {
//            return 0
//        }
        return 0
    }



    fun populateGridLayoutItems(itemsNumber: Int) {
        if (itemsList.isEmpty()) {
            for (i in 1..itemsNumber step 2) {
                val coreGroup = GridItem(
                    "cpu" + "${i}",
                    "1.34GHZ",
                    74,
                    "cpu" + "${i+1}",
                    "1.35GHz",
                    73
                )

                itemsList.add(coreGroup)
            }
        }
    }
}

