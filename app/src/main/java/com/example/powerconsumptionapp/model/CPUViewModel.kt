package com.example.powerconsumptionapp.model

import android.os.Build
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
import kotlinx.coroutines.launch
import java.io.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

class CPUViewModel: ViewModel() {

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
    fun getCpuTemp(): Int {
        if (File(Constants.CPU_TEMPERATURE_PATH).exists()) {
            val reader = RandomAccessFile(Constants.CPU_TEMPERATURE_PATH, "r")
            return (reader.readLine().toInt() / 1000)
        }
        return 0
    }

    // Get the CPU load average from /proc/loadavg
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
                val leftFilePath = "/sdcard/temp_files/cpu${i}_freq"
                val rightFilePath = "/sdcard/temp_files/cpu${i+1}_freq"

                var leftFreq = "-"
                var rightFreq = "-"

                if (getFreq(leftFilePath) != 0) {
                    leftFreq = getFreq(leftFilePath).toString()
                }

                if (getFreq(rightFilePath) != 0) {
                    rightFreq = getFreq(rightFilePath).toString()
                }

                val coreGroup = GridItem(
                    "cpu" + "${i}",
                    " ${leftFreq} [MHz]",
                    coresLoad.getOrDefault(i, 0),
                    "cpu" + "${i + 1}",
                    " ${rightFreq} [MHz]" ,
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
}

