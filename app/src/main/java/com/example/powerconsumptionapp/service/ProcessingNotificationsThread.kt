package com.example.powerconsumptionapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.Constants.CHANNEL_ID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class ProcessingNotificationsThread(
    private var applicationContext: Context,
    private var reminderBatteryLevel: Int
): Thread() {

    private var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()
    private var batteryPercent: Int? = 0
    private var chargingStatus: Int? = 0

    companion object {
        const val TAG = "STARTED_SERVICE"
        const val SLEEP_TIME: Long = 7200000
    }

    override fun run() {
        while (isRunning) {
            getBatteryStatus()
            if ((batteryPercent!! >= reminderBatteryLevel) and
                (chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING)) {
                sendNotification()
                sleep(SLEEP_TIME)
            }
        }
        Log.i(TAG,"${currentThread()} has finished his job.")
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            batteryPercent = intent?.let {
                var batteryLevel = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                (batteryLevel * 100 / scale.toFloat()).roundToInt()
            }

            // Get charging status
            chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }


    private fun sendNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle(Constants.REMINDER_BATTERY_NOTIFICATION_TITLE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${Constants.REMINDER_NOTIFICATION_EXTRA_TEXT}${reminderBatteryLevel}%!"))
            .setContentText("${Constants.REMINDER_NOTIFICATION_EXTRA_TEXT}${reminderBatteryLevel}%!")
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager!!.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }

    fun stopThread() {
        isRunning = false
    }
}