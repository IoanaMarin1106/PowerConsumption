package com.example.powerconsumptionapp.general

object Constants {
    const val HIGH_TEMPERATURE = 40
    const val NORMAL_TEMPERATURE = 36

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

    // CPU details
    const val CPU_CORES_PATH = "/sys/devices/system/cpu/"
    const val CPU_TEMPERATURE_PATH = "/sys/devices/virtual/thermal/thermal_zone0"
//    const val CPU_TEMPERATURE_PATH = "/sys/class/thermal/thermal_zone0"
}