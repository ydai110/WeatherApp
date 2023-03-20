package com.example.weatherapp.api

import com.example.weatherapp.data.CitySuggestionsData
import com.example.weatherapp.data.HourlyForecast
import com.example.weatherapp.data.WeatherDetailData
import com.example.weatherapp.util.Constants.APP_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {

    suspend fun requestSearchSuggestions(query: String?): Response<CitySuggestionsData> {
        return withContext(Dispatchers.IO) {
            weatherApi.getSearchSuggestions(
                query = query,
                appId = APP_ID,
                limit = 5,
            )
        }
    }

    suspend fun requestWeatherData(
        latitude: Double, longitude: Double
    ): Response<WeatherDetailData> =
        withContext(Dispatchers.IO) {
            weatherApi.requestWeatherDetail(
                lat = latitude,
                lon = longitude,
                appId = APP_ID,
            )
        }

    suspend fun requestHourlyForecast(
        latitude: Double, longitude: Double
    ): Response<HourlyForecast> =
        withContext(Dispatchers.IO) {
            weatherApi.requestHourlyForecast(
                lat = latitude,
                lon = longitude,
                appId = APP_ID,
            )
        }
}
