package com.example.powerconsumptionapp.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.BatteryManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.general.Constants
import kotlin.math.roundToInt


class ProcessingAlarmThread(
    private var applicationContext: Context,
    private var bottomLimit: Int,
    private var upperLimit: Int
): Thread() {

    private var isRunning = true
    private var iFilter: IntentFilter? = IntentFilter()
    private var batteryPercent: Int? = 0

    companion object {
        const val TAG = "ALARM_SERVICE"
        const val SLEEP_TIME: Long = 7200000
    }

    override fun run() {
        while (isRunning) {
            getBatteryStatus()
            if (batteryPercent!! != 0) {
                if (batteryPercent!! < bottomLimit) {
                    println("aiciiiiiiiii")
                    sendNotification(Constants.BOTTOM_LIMIT_DROPPED_MESSAGE)
                    sleep(SLEEP_TIME)
                } else if (batteryPercent!! > upperLimit) {
                    sendNotification(Constants.UPPER_LIMIT_DROPPED_MESSAGE)
                    sleep(SLEEP_TIME)
                }
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
        }
    }

    private fun getBatteryStatus() {
        iFilter!!.addAction(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)
    }

    private fun sendNotification(notificationMessage: String) {
        val builder = NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle(Constants.LIMITS_NOTIFICATION_TITLE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
            .setContentText(notificationMessage)
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