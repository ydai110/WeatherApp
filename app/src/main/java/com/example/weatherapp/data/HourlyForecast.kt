package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class HourlyForecast(
    @SerializedName("list")
    val forecast: List<WeatherDetailData>
)
