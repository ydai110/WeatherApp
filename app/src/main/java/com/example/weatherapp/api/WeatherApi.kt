package com.example.weatherapp.api

import com.example.weatherapp.data.CitySuggestionsData
import com.example.weatherapp.data.HourlyForecast
import com.example.weatherapp.data.WeatherDetailData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("geo/1.0/direct")
    suspend fun getSearchSuggestions(
        @Query("q") query: String?,
        @Query("limit") limit: Int,
        @Query("appId") appId: String
    ): Response<CitySuggestionsData>

    @GET("data/2.5/weather")
    suspend fun requestWeatherDetail(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String
    ): Response<WeatherDetailData>

    @GET("data/2.5/forecast")
    suspend fun requestHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String
    ): Response<HourlyForecast>
}
