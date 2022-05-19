package com.example.powerconsumptionapp.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.example.powerconsumptionapp.model.CPUViewModel
import java.io.File
import java.io.RandomAccessFile
import java.time.LocalDateTime

class CPUMonitoringThread(): Thread() {

    private var isRunning = true

    companion object {
        const val TAG = "SERVICE"
        const val SLEEP_TIME: Long = 4000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        while (isRunning) {
            // Get current cpu temperature
            val cpuTemperature = getCpuTemp()

            // Get current time
            val currentTime = LocalDateTime.now()
            CPUViewModel.cpuTemperatureTimeMap[currentTime] = cpuTemperature

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

    fun stopThread() {
        isRunning = false
    }
}