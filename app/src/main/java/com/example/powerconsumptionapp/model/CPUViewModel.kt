package com.example.powerconsumptionapp.model

import android.os.Build
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.cpuinfo.GridItem
import com.example.powerconsumptionapp.cpuinfo.itemsList
import com.example.powerconsumptionapp.general.Constants
import java.io.*
import java.util.regex.Pattern
import kotlin.math.roundToInt
import kotlin.random.Random

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
        return 0
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCoresLoad(coresNumber: Int): List<Int> {
        val file = File(Constants.CPU_CORES_LOADAVG)
        try {
            BufferedReader(FileReader(file)).use { br ->
                br.lines().forEach {
                    val words = it.split("\\s".toRegex()).toTypedArray()
                    for (i in 0..coresNumber) {
                        val currentCore = "cpu" + "${i}"
                        if (words[0] == currentCore) {
                            words.drop(1)
                            val longs = words.map { it.toLong() }.toTypedArray()
                            val coreUsage = longs.sum()
                            break;
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return listOf(0, 1)
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

