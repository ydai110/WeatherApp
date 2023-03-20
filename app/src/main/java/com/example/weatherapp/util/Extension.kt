package com.example.weatherapp.util

import java.math.RoundingMode

fun Double.toFahrenheit() =
    (1.8 * (this - 273) + 32).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
