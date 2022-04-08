package com.example.powerconsumptionapp.model

import android.hardware.SensorManager
import android.util.Log
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.cpuinfo.CPUInfoFragment
import com.example.powerconsumptionapp.cpuinfo.itemsList
import com.example.powerconsumptionapp.general.Constants
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile
import java.lang.Exception
import java.util.regex.Pattern
import kotlin.reflect.typeOf
import java.lang.Compiler.command

import com.example.powerconsumptionapp.general.ShellExecuter




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
//        val commandToRun = "chmod sys/class/thermal/thermal_zone0/temp 7777"
//        Runtime.getRuntime().exec(commandToRun)
//        File(Constants.CPU_TEMPERATURE_PATH).forEachLine {
//            println(it)
//        }

//        val reader = RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp", "r")
//        val line = reader.readLine()
//        println(line)



//        val exe = ShellExecuter()
//        val command = "cat /proc/stat"
//
//        val outp = exe.Executer(command)
//        Log.d("Output", outp)

        return 0
    }

    fun populateGridLayoutItems(itemsNumber: Int) {
        if (itemsList.isEmpty()) {
//            for ()
        }
    }
}

