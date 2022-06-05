package com.example.powerconsumptionapp.general

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.batteryview.BatterySettingsFragment
import com.example.powerconsumptionapp.cpuinfo.InfoDialogFragment
import com.example.powerconsumptionapp.service.BatteryMonitoringService
import com.example.powerconsumptionapp.service.BatteryMonitoringThread
import com.example.powerconsumptionapp.service.NotificationService
import java.math.RoundingMode
import java.text.DecimalFormat

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

        fun showDialog(mainActivity: MainActivity, dialogTitle: String, dialogText: String) {
            InfoDialogFragment(
                dialogTitle,
                dialogText
            ).show(mainActivity.supportFragmentManager, "customDialog")
        }
    }
}