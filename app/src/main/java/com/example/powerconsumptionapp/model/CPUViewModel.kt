package com.example.powerconsumptionapp.model

import android.hardware.SensorManager
import android.util.Log
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.ViewModel
import com.example.powerconsumptionapp.cpuinfo.CPUInfoFragment
import com.example.powerconsumptionapp.cpuinfo.GridItem
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
import java.io.IOException


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
//        val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
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
            val firstCoreGroup = GridItem(
                "cpu1",
                "1.34GHZ",
                74,
                "cpu2",
                "1.35GHz",
                73
            )
            val secondCoreGroup = GridItem(
                "cpu3",
                "1.24GHZ",
                50,
                "cpu4",
                "1.36GHz",
                56
            )

            itemsList.apply {
                add(firstCoreGroup)
                add(secondCoreGroup)
            }
        }
    }
}

