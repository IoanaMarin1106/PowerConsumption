package com.example.powerconsumptionapp.general

import android.annotation.SuppressLint


object Constants {
    const val ERROR = "error"
    const val OK = "ok"

    // Temperature (F/C) thresholds
    const val HIGH_CELSIUS_TEMPERATURE = 40
    const val NORMAL_CELSIUS_TEMPERATURE = 36
    const val HIGH_FAHRENHEIT_TEMPERATURE = 104

    const val FAHRENHEIT_DEGREES = "\u2109"
    const val CELSIUS_DEGREES = "\u2103"

    // Battery status
    const val CHARGING = "Charging"
    const val DISCHARGING = "Discharging"
    const val UNKNOWN = "Unknown"
    const val NOT_CHARGING = "Not charging"
    const val FULL = "Full"

    // Battery power
    const val AC_CHARGER = "AC Charger"
    const val WIRELESS = "Wireless"
    const val USB = "USB"
    const val NONE = "None"

    // Battery health
    const val COLD = "Cold"
    const val DEAD = "Dead"
    const val GOOD = "Good"
    const val OVERHEAT = "Ovearheat"
    const val OVER_VOLTAGE = "Over voltage"
    const val UNSPECIFIED_FAILURE = "Unspecified failure"

    // Screen brightness
    const val MAX_BRIGHTNESS = 255

    // CPU details
    const val CPU_CORES_PATH = "/sys/devices/system/cpu/"
    @SuppressLint("SdCardPath")
    const val CPU_TEMPERATURE_PATH = "/data/data/com.example.powerconsumptionapp/temp_files/cpu_overall_temp"

    @SuppressLint("SdCardPath")
    const val CPU_LOADAVG = "/data/data/com.example.powerconsumptionapp/temp_files/cpu_loadavg"
    const val CURR_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
    const val MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
    @SuppressLint("SdCardPath")
    const val MAX_FREQ = "/data/data/com.example.powerconsumptionapp/temp_files/cpu_max_freq"
    const val AVAILABLE_FREQUENCIES = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies"
    const val AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors"
    @SuppressLint("SdCardPath")
    const val CPU_CORES_LOADAVG = "/data/data/com.example.powerconsumptionapp/temp_files/cores_loadavg"
    const val CURRENT_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"

    const val CPU_MODEL_NAME = "/proc/cpuinfo"

    // CPU Stats
    const val USER = 1          // Normal processes executing in user mode
    const val NICE = 2          // Niced processes executing in user mode
    const val SYSTEM = 3        // Processes executing in kernel mode
    const val IDLE = 4          // Twiddling thumbs
    const val IOWAIT = 5        // Waiting for I/O to complete
    const val IRQ = 6           // Servicing interrupts
    const val SOFTIRQ = 7      // Servicing softirqs

    const val CURR_CPU_FRQ = "Current CPU Frequency"
    const val MAX_CPU_FRQ = "Max CPU Frequency"
    const val MIN_CPU_FRQ = "Min CPU Frequency"
    const val CURR_CPU_FRQ_DESC = "Current frequency of the CPU as determined by the governor and cpufreq core. If the corresponding file is not found, the value will be replaced with the character -."
    const val MAX_MIN_CPU_FREQ_DESC = "Current \"policy limits\". If the corresponding file is not found, the value will be replaced with \"unknown\"."

    // Notification Service
    const val CHANNEL_ID = "default"
    const val REMINDER_BATTERY_LEVEL = "Reminder Battery Level"
    const val REMINDER_NOTIFICATION_EXTRA_TEXT = "Please switch off power adapter! Your device has reached the level of "
    const val REMINDER_BATTERY_NOTIFICATION_TITLE = "Charging battery reminder"

    // Launch app when power connected
    const val LAUNCH_APP_POWER_CONNECTED_MESSAGE = "POW'R will be automatically launched when power connected!"
    const val SWITCH_OFF_LAUNCH_APP_POWER_CONNECTED_MESSAGE = "Launch app when power connected is off"

    // Setup battery level limits
    const val ALARM_MESSAGE = "The alarm has been set to: "
    const val BOTTOM_LIMIT = "Bottom Limit"
    const val UPPER_LIMIT = "Upper Limit"
    const val LIMITS_NOTIFICATION_TITLE = "Battery level limits exceeded"
    const val BOTTOM_LIMIT_DROPPED_MESSAGE = "The battery level has dropped below the limit! To save battery level, you can turn off Bluetooth, WiFi, mobile data, or turn on Battery Saver mode."
    const val UPPER_LIMIT_DROPPED_MESSAGE = "The battery level has exceeded the upper limit. Please switch off power adapter!"

    // Monitoring battery consumption
    const val MONITORING_BATTERY_TITLE = "Battery Statistics"
    const val MONITORING_BATTERY_MESSAGE = "Press OK if you allow POW'R to monitor battery consumption. You will be able to change this setting later through the application."
    const val MONITORING_SERVICE_RUNNING_WARNING = "Battery monitoring service is already running on your device!"
    const val MONITORING_SERVICE_RUNNING_SUCCESS= "Battery monitoring service is now running on your device!"

    // CPU Monitoring Service
    const val CPU_MONITORING_MESSAGE_ACTIVATED = "The processor load monitoring service is running on your device!"
    const val CPU_MONITORING_SERVICE_RUNNING_WARNING = "The processor load monitoring service is already running on your device!"
    const val MAX_DATA_POINTS = 5000

    // User can change these files
    const val GOVERNOR = "governor"

    @SuppressLint("SdCardPath")
    const val CPU_NEW_MAX_FREQ = "/data/data/com.example.powerconsumptionapp/user_data/cpu_max_freq.txt"

    @SuppressLint("SdCardPath")
    const val CPU_NEW_MIN_FREQ = "/data/data/com.example.powerconsumptionapp/user_data/cpu_min_freq.txt"

    @SuppressLint("SdCardPath")
    const val CPU_NEW_GOVERNOR = "/data/data/com.example.powerconsumptionapp/user_data/cpu_governor.txt"
}
