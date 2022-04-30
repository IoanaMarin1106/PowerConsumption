package com.example.powerconsumptionapp.general

import java.math.RoundingMode
import java.text.DecimalFormat

class Util {
    companion object {
        @JvmStatic
        fun convertCelsiusToFahrenheit(celsiusTemp: Int): Int {
            return celsiusTemp * 9 / 5 + 32
        }
    }
}