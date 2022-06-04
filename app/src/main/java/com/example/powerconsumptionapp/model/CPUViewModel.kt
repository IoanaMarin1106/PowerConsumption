package com.example.powerconsumptionapp.model

import android.os.Build
import android.os.HardwarePropertiesManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.cpuinfo.CpuStats
import com.example.powerconsumptionapp.cpuinfo.GridItem
import com.example.powerconsumptionapp.cpuinfo.InfoDialogFragment
import com.example.powerconsumptionapp.cpuinfo.itemsList
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.performancemanager.PerformanceManagerFragment
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Error
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class CPUViewModel: ViewModel() {
    companion object {
        var cpuTemperatureTimeMap: TreeMap<LocalDateTime, Int> = TreeMap<LocalDateTime, Int>()
        var cpuLoadTimeMap: TreeMap<LocalDateTime, Int> = TreeMap<LocalDateTime, Int>()
        var cpuCoreLoadTime: MutableList<TreeMap<LocalDateTime, Int>> = mutableListOf()
    }

    // Get the CPU cores from /sys/devices/system/cpu/
    fun getNumberOfCores(): Int {
        return File(Constants.CPU_CORES_PATH).listFiles { pathname ->
            Pattern.matches("cpu[0-9]", pathname!!.name)
        }?.size ?: 0
    }

    fun getCpuModelName(): Pair<String, String> {
        val cpuinfoFile = File(Constants.CPU_MODEL_NAME)
        try {
            BufferedReader(FileReader(cpuinfoFile)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    val words = line?.split(':')?.toMutableList()

                    if (words?.size == 2 && words[0].contains("model name")) {
                        return words[1].split("@")[0] to words[1].split("@")[1]
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "" to ""
    }

    // Get the CPU temp from /sys/devices/virtual/thermal/thermal_zone0/temp
    @RequiresApi(Build.VERSION_CODES.N)
    fun getCpuTemp(): Int {
        if (File(Constants.CPU_TEMPERATURE_PATH).exists()) {
            val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
            return (reader.readLine().toInt() / 1000)
        }
        return 0
    }

    // Get the CPU load average from /proc/loadavg
    // incepand cu API 26 Google a restrictionat accesul la fisierele /proc/loadavg sau /proc/stats
    fun getLoadAvg() : Int {
        if (File(Constants.CPU_LOADAVG).exists()) {
            val reader = RandomAccessFile(Constants.CPU_LOADAVG, "r")
            val line = reader.readLine()
            val words = line.split("\\s".toRegex()).toTypedArray()
            return (words[2].toFloat() * 100).roundToInt()
        }
        return 0
    }

    /*
        Check if /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
              /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
              /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq
        exist
    */
    fun getFreq(fileName: String) : Int {
        val file = File(fileName)
        if (file.exists()) {
            val reader = RandomAccessFile(fileName, "r")
            val line = reader.readLine()
            return line.toInt()
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
                val leftFilePath = "/sys/devices/system/cpu/cpu${i}/cpufreq/scaling_cur_freq"
                val rightFilePath = "/sys/devices/system/cpu/cpu${i+1}/cpufreq/scaling_cur_freq"

                var leftFreq = "-"
                var rightFreq = "-"

                if (getFreq(leftFilePath) != 0) {
                    leftFreq = getFreq(leftFilePath).toString()
                }

                if (getFreq(rightFilePath) != 0) {
                    rightFreq = getFreq(rightFilePath).toString()
                }

                val coreGroup = GridItem(
                    "cpu$i",
                    " $leftFreq KHz",
                    coresLoad.getOrDefault(i, 0),
                    "cpu" + "${i + 1}",
                    " $rightFreq KHz" ,
                    coresLoad.getOrDefault(i + 1, 0),
                )
                itemsList.add(coreGroup)
            }
        }
    }

    fun showDialog(mainActivity: MainActivity, dialogTitle: String, dialogText: String) {
        InfoDialogFragment(
            dialogTitle,
            dialogText
        ).show(mainActivity.supportFragmentManager, "customDialog")
    }

    fun getCPUGovernor(fileName: String): String {
        val file = File(fileName)
        return if (file.exists()) {
            RandomAccessFile(fileName, "r").readLine()
        } else {
            Log.e("[ERROR]", "File does not exist")
            Constants.ERROR
        }
    }

    fun getAvailableResources(fileName: String): MutableList<String> {
        var availableFreqList = mutableListOf<String>()
        val file = File(fileName)
        if (file.exists()) {
            availableFreqList = RandomAccessFile(fileName, "r").readLine().toString().trim().split("\\s+".toRegex()).toMutableList()
        }
        return availableFreqList
    }

    private fun writeFile(fileName: String, text: String): String {
        val file = File(fileName)
        if (file.exists()) {
            file.printWriter().use { out ->
                out.println(text)
            }
        } else {
            return Constants.ERROR
        }
        return Constants.OK
    }

    fun cpuSettingsHandler(text: String, resourceType: String) {
        val newValue = text.trim().split("\\s+".toRegex()).toTypedArray()[0]
        when (resourceType) {
            Constants.GOVERNOR -> {

                val result = writeFile(Constants.CPU_NEW_GOVERNOR, newValue)
                if (result == Constants.ERROR) {
                    Log.e("[ERROR]", "File does not exist - NEW CPU GOVERNOR")
                } else {
                    Log.i(PerformanceManagerFragment.TAG,"User has changed CPU governor to $newValue.")
                }
            }

            Constants.MIN_CPU_FRQ -> {
                val result = writeFile(Constants.CPU_NEW_MIN_FREQ, newValue)
                if (result == Constants.ERROR) {
                    Log.e("[ERROR]", "File does not exist - NEW CPU MIN FREQ")
                } else {
                    Log.i(PerformanceManagerFragment.TAG,"User has changed CPU min frequency to $newValue KHz")
                }
            }

            Constants.MAX_CPU_FRQ -> {
                val result = writeFile(Constants.CPU_NEW_MAX_FREQ, newValue)
                if (result == Constants.ERROR) {
                    Log.e("[ERROR]", "File does not exist - NEW CPU MAX FREQ")
                } else {
                    Log.i(PerformanceManagerFragment.TAG,"User has changed CPU max frequency to $newValue KHz")
                }
            }
        }
    }
}

