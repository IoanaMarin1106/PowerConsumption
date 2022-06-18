package com.example.powerconsumptionapp.general

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.ClipboardManager
import android.util.Log
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.cpuinfo.InfoDialogFragment
import com.example.powerconsumptionapp.service.BatteryMonitoringService


class Util {
    companion object {
        @JvmStatic
        fun convertCelsiusToFahrenheit(celsiusTemp: Int): Int {
            return celsiusTemp * 9 / 5 + 32
        }

        fun convertFahrenheitToCelsius(fahrenheitTemp: Int): Int {
            return (fahrenheitTemp - 32) * 5 / 9
        }

        fun popupMessage(context: Context, popupMessage: String, popupTitle: String, monitoringService: Boolean) {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage(popupMessage)
            alertDialogBuilder.setTitle(popupTitle)
            alertDialogBuilder.setPositiveButton("ok"
            ) { _, _ ->
                if (monitoringService) {
                    Log.i("SERVICE", "Start monitoring service")
                    MainActivity.isMonitoringServiceRunning = true
                    val monitoringService = Intent(context, BatteryMonitoringService::class.java)
                    context.startService(monitoringService)
                }
            }

            alertDialogBuilder.setNegativeButton("Close") {
                _, _ ->
                if (monitoringService) {
                    Log.i("SERVICE", "Monitoring service is not activated.")
                    MainActivity.isMonitoringServiceRunning = false
                }
            }
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        fun setClipboard(context: Context, text: String) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = text
            } else {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", text)
                clipboard.setPrimaryClip(clip)
            }
        }

        fun showDialog(mainActivity: MainActivity, dialogTitle: String, dialogText: String) {
            InfoDialogFragment(
                dialogTitle,
                dialogText
            ).show(mainActivity.supportFragmentManager, "customDialog")
        }

        private fun calculateBeta(x: MutableList<Int>, y: MutableList<Int>): Double {
            val n: Int = x.size

            // sum of array x
            val sx: Int = x.sum()

            // sum of array y
            val sy: Int = y.sum()

            // for sum of product of x and y
            var sxsy = 0

            // sum of square of x
            var sx2 = 0
            for (i in 0 until n) {
                sxsy += x[i] * y[i]
                sx2 += x[i] * x[i]
            }

            return ((n * sxsy - sx * sy).toDouble()
                    / (n * sx2 - sx * sx))
        }

        // Function to find the least regression line
        fun leastRegLine(
            X: MutableList<Int>, Y: MutableList<Int>
        ): String {

            // Finding b
            val b: Double = calculateBeta(X, Y)
            val n = X.size
            val meanX = X.sum() / n
            val meanY = Y.sum() / n

            // calculating a
            val a = meanY - b * meanX

            // Printing regression line
            println("Regression line:")
            print("Y = ")
            System.out.printf("%.3f", a)
            print(" + ")
            System.out.printf("%.3f", b)
            print("*X")

            return "$a $b"
        }
    }
}