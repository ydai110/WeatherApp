package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.data.HourlyForecast
import com.example.weatherapp.data.WeatherDetailData
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.UserLocation
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    init {
        Log.d("WeatherViewModel", "init")
    }

    private val _weatherDetail: MutableLiveData<Resource<WeatherDetailData>> = MutableLiveData()
    val weatherDetail: LiveData<Resource<WeatherDetailData>> = _weatherDetail

    private val _hourlyForecast: MutableLiveData<Resource<HourlyForecast>> = MutableLiveData()
    val hourlyForecast: LiveData<Resource<HourlyForecast>> = _hourlyForecast

    fun requestWeatherDetail(userLocation: UserLocation) {
        viewModelScope.launch {
            val response = weatherRepository.requestWeatherData(
                latitude = userLocation.latitude,
                longitude = userLocation.longitude
            )
            if (response.isSuccessful) {
                _weatherDetail.value = Resource.Success(response.body()!!)
            } else {
                _weatherDetail.value = Resource.Failure(msg = "Cannot load weather page")
            }
        }
    }

    fun requestHourlyForecast(userLocation: UserLocation) {
        viewModelScope.launch {
            val response = weatherRepository.requestHourlyForecast(
                latitude = userLocation.latitude,
                longitude = userLocation.longitude
            )
            if (response.isSuccessful) {
                _hourlyForecast.value = Resource.Success(response.body()!!)
            } else {
                _weatherDetail.value = Resource.Failure(msg = "Cannot load hourly forecast page")
            }
        }
    }
}
