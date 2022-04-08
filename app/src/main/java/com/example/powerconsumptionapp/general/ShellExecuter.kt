package com.example.powerconsumptionapp.general

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class ShellExecuter {
    fun Executer(command: String?): String {
        val output = StringBuffer()
        val p: Process
        try {
            p = Runtime.getRuntime().exec(command)
            p.waitFor()
            val reader = BufferedReader(InputStreamReader(p.inputStream))
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                output.append(line + "n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return output.toString()
    }
}