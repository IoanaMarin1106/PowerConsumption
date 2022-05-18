package com.example.powerconsumptionapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.powerconsumptionapp.databinding.ActivityMainBinding
import com.example.powerconsumptionapp.general.Constants
import com.example.powerconsumptionapp.general.Util
import com.example.powerconsumptionapp.service.AlarmService
import com.example.powerconsumptionapp.service.BatteryMonitoringService
import com.example.powerconsumptionapp.service.NotificationService
import com.example.powerconsumptionapp.service.StartActivityService


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val MY_READ_EXTERNAL_REQUEST : Int = 1
        const val MY_WRITE_EXTERNAL_REQUEST: Int = 1
        var isMonitoringServiceRunning = false
        var isOrientationChanged = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get permissions for r/w
        getPermissions()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.myToolbar))

        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        createNotificationChannel()

        println(isOrientationChanged)
        if (!isOrientationChanged) {
            Util.popupMessage(this, Constants.MONITORING_BATTERY_MESSAGE, Constants.MONITORING_BATTERY_TITLE, true)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "default"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_READ_EXTERNAL_REQUEST)
        }

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_WRITE_EXTERNAL_REQUEST)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            val serviceNotificationIntent = Intent(applicationContext, NotificationService::class.java)
            stopService(serviceNotificationIntent)

            val serviceStartActivityIntent = Intent(applicationContext, StartActivityService::class.java)
            stopService(serviceStartActivityIntent)

            val alarmServiceIntent = Intent(applicationContext, AlarmService::class.java)
            stopService(alarmServiceIntent)

            val monitoringService = Intent(applicationContext, BatteryMonitoringService::class.java)
            stopService(monitoringService)
        }
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        isOrientationChanged = true
        super.onConfigurationChanged(newConfig)
    }

    override fun onRestart() {
        super.onRestart()
        if (isOrientationChanged) {
            isOrientationChanged = false
        }
    }
}