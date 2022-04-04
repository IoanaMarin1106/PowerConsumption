package com.example.powerconsumptionapp.general

class Util {
    companion object {
        @JvmStatic
        final fun convertCelsiusToFahrenheit(celsiusTemp: Int): Int {
            return celsiusTemp * 9 / 5 + 32
        }

        @JvmStatic
        fun convertFahrenheitToCelsius(fahrenheitTemp: Int): Int {
            return (fahrenheitTemp - 32) * 5 / 9
        }
    }
}