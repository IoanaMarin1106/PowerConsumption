package com.example.powerconsumptionapp.cpuinfo

import com.example.powerconsumptionapp.general.Constants
import java.io.File
import kotlin.math.roundToInt

//  CPU Utilization since boot = (user + nice + system + iowait + irq + softirq) / (user + nice + system +iowait + irq + softirq + idle)
class CpuStats(coresNumber: Int) {
    private var coresUsage: HashMap<Int, Int> = HashMap<Int, Int>(coresNumber + 1)
    private lateinit var cpuStatFile: File
    private val coresNumber = coresNumber

    init {
        getCpuUsage()
    }

    private fun getCpuUsage() {
        val returnVal = readFile()
        if (returnVal != -1) {
           cpuStatFile.forEachLine {
               parseLine(it)
           }
        }
    }

    private fun parseLine(line: String) {
        val elements = line.split("\\s".toRegex()).toMutableList()
        if (elements[0].indexOf("cpu") != -1) {
            var cpuId = -1
            var usage = -1.0

            if (elements[0].length == 3) {
                // "cpu" usage
                cpuId = coresNumber
            } else if (elements[0].length == 4) {
                // "cpu0, cpu1, cpu2, .." usage
                cpuId = elements[0][3].digitToInt()
            }

            if (elements[1] == "") {
                //cpu
                val usageWithoutIdle = elements[Constants.USER + 1].toLong() +
                        elements[Constants.NICE + 1].toLong() +
                        elements[Constants.SYSTEM + 1].toLong() +
                        elements[Constants.IOWAIT + 1].toLong() +
                        elements[Constants.IRQ + 1].toLong() +
                        elements[Constants.SOFTIRQ + 1].toLong()
                usage = ((usageWithoutIdle.toDouble()) / (usageWithoutIdle + elements[Constants.IDLE + 1].toLong()).toDouble()) * 100
            } else {
                // cpu0, cpu1...
                val usageWithoutIdle = elements[Constants.USER].toLong() +
                        elements[Constants.NICE].toLong() +
                        elements[Constants.SYSTEM].toLong() +
                        elements[Constants.IOWAIT].toLong() +
                        elements[Constants.IRQ].toLong() +
                        elements[Constants.SOFTIRQ].toLong()
                usage = ((usageWithoutIdle.toDouble()) / (usageWithoutIdle + elements[Constants.IDLE + 1].toLong()).toDouble()) * 100
            }
            coresUsage[cpuId] = usage.roundToInt()
        }
    }

    private fun readFile(): Int {
        cpuStatFile = File(Constants.CPU_CORES_LOADAVG)
        return 0
    }

    fun getCoresUsage(): HashMap<Int, Int> = coresUsage

}
