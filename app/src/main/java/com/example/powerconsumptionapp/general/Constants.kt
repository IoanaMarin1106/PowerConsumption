package com.example.powerconsumptionapp.general

import java.util.regex.Pattern

object Constants {

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
    const val BRIGHTNESS_THRESHOLD = 5

    // CPU details
    const val CPU_CORES_PATH = "/sys/devices/system/cpu/"
    const val CPU_TEMPERATURE_PATH = "/sdcard/temp_files/cpu_overall_temp"
    const val CPU_LOADAVG = "/sdcard/temp_files/cpu_loadavg"
    const val CURR_FREQ = "/sdcard/temp_files/cpu_curr_freq"
    const val MIN_FREQ = "/sdcard/temp_files/cpu_min_freq"
    const val MAX_FREQ = "/sdcard/temp_files/cpu_max_freq"
    const val CPU_CORES_LOADAVG = "/sdcard/temp_files/cores_loadavg"

    // CPU Stats
    const val USER = 1          // Normal processes executing in user mode
    const val NICE = 2          // Niced processes executing in user mode
    const val SYSTEM = 3        // Processes executing in kernel mode
    const val IDLE = 4          // Twiddling thumbs
    const val IOWAIT = 5        // Waiting for I/O to complete
    const val IRQ = 6           // Servicing interrupts
    const val SOFTIRQ = 7      // Servicing softirqs
}