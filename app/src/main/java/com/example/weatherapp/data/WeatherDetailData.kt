package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherDetailData(
    @SerializedName("weather")
    val weather: List<WeatherDetail>,
    @SerializedName("main")
    val temperature: Temperature,
    @SerializedName("visibility")
    val visibility: String,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("dt_txt")
    val dt_txt: String? = null
)

data class WeatherDetail(
    @SerializedName("icon")
    val iconId: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("main")
    val main: String,
    @SerializedName("id")
    val id: Int,
)

data class Temperature(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feels_like")
    val tempFeel: Double,
    @SerializedName("temp_min")
    val minTemp: Double,
    @SerializedName("temp_max")
    val maxTemp: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
)

data class Wind(
    @SerializedName("speed")
    val speed: Double,
)