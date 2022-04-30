package com.example.powerconsumptionapp.model

import android.os.Build
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.cpuinfo.CpuStats
import com.example.powerconsumptionapp.cpuinfo.GridItem
import com.example.powerconsumptionapp.cpuinfo.itemsList
import com.example.powerconsumptionapp.general.Constants
import java.io.*
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

    // Get the CPU temp from /sys/devices/virtual/thermal/thermal_zone0/temp
    fun getCpuTemp(): Int {
        val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
        return (reader.readLine().toInt() / 1000)
    }

    // Get the CPU load average from /proc/loadavg
    fun getLoadAvg() : Int {
        val reader = RandomAccessFile(Constants.CPU_LOADAVG, "r")
        val line = reader.readLine()
        val words = line.split("\\s".toRegex()).toTypedArray()
        return (words[2].toFloat() * 100).roundToInt()
    }

    // Check if /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
    //          /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
    //          /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq
    fun getFreq(fileName: String) : Int {
        var file = File(fileName)
        if (file.exists()) {
            val reader = RandomAccessFile(fileName, "r")
            val line = reader.readLine()
            return (line.toInt() / 100)
        } else {
            Log.e("[ERROR]", "File does not exist")
        }
        return 0
    }

    private fun getCoreLoad(coresNumber: Int): HashMap<Int, Int> {
        val cpuStats = CpuStats(coresNumber)
        return cpuStats.getCoresUsage()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun populateGridLayoutItems(itemsNumber: Int) {
        if (itemsList.isEmpty()) {
            val coresLoad: HashMap<Int, Int> = getCoreLoad(itemsNumber)
            for (i in 0 until itemsNumber step 2) {
                val coreGroup = GridItem(
                    "cpu" + "${i}",
                    "1.34GHZ",
                    coresLoad.getOrDefault(i, 0),
                    "cpu" + "${i+1}",
                    "1.35GHz",
                    coresLoad.getOrDefault(i + 1, 0),
                    )

                itemsList.add(coreGroup)
            }
        }
    }
}

