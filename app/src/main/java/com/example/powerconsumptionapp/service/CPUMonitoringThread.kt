package com.example.powerconsumptionapp.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.powerconsumptionapp.cpuinfo.CpuStats
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.CPUViewModel
import java.io.File
import java.io.RandomAccessFile
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class CPUMonitoringThread(): Thread() {

    private var isRunning = true

    companion object {
        const val TAG = "SERVICE"
        const val SLEEP_TIME: Long = 4000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        while (isRunning) {
            // Get current CPU temperature
            val cpuTemperature = getCpuTemp()

            // Get current CPU load average
            val cpuLoad = getLoadAvg()

            // Get number of CPU cores
            val cpuCoresNumber = getNumberOfCores()
            val coresLoadMap = getCoreLoad(cpuCoresNumber)

            // Get current time
            val currentTime = LocalDateTime.now()
            CPUViewModel.apply {
                cpuTemperatureTimeMap[currentTime] = cpuTemperature
                cpuLoadTimeMap[currentTime] = cpuLoad
                if (cpuCoreLoadTime.size == 0) {
                    for (i in 0 until cpuCoresNumber) {
                        cpuCoreLoadTime.add(TreeMap<LocalDateTime, Int>())
                    }
                }

                for (i in 0 until cpuCoresNumber) {
                    cpuCoreLoadTime[i][currentTime] = coresLoadMap.getOrDefault(i, 0)
                }
            }
            sleep(SLEEP_TIME)
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    // Get the CPU temp from /sys/devices/virtual/thermal/thermal_zone0/temp
    private fun getCpuTemp(): Int {
        if (File(Constants.CPU_TEMPERATURE_PATH).exists()) {
            val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
            return (reader.readLine().toInt() / 1000)
        }
        return 0
    }

    // Get the CPU load average from /proc/loadavg
    private fun getLoadAvg() : Int {
        if (File(Constants.CPU_LOADAVG).exists()) {
            val reader = RandomAccessFile(Constants.CPU_LOADAVG, "r")
            val line = reader.readLine()
            val words = line.split("\\s".toRegex()).toTypedArray()
            return (words[2].toFloat() * 100).roundToInt()
        }
        return 0
    }

    // Get the CPU cores from /sys/devices/system/cpu/
    private fun getNumberOfCores(): Int {
        return File(Constants.CPU_CORES_PATH).listFiles { pathname ->
            Pattern.matches("cpu[0-9]", pathname!!.name)
        }?.size ?: 0
    }

    // Get CPU cores load average
    private fun getCoreLoad(coresNumber: Int): HashMap<Int, Int> {
        val cpuStats = CpuStats(coresNumber)
        return cpuStats.getCoresUsage()
    }

    fun stopThread() {
        isRunning = false
    }
}