package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.util.Constants.BASE_URL
import com.example.weatherapp.util.GpsLocationServiceManager
import com.example.weatherapp.util.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideSharedPreferenceManager(context: Context): SharedPreferenceManager =
        SharedPreferenceManager(context = context)

    @Singleton
    @Provides
    fun provideGPSLocationManager(context: Context): GpsLocationServiceManager =
        GpsLocationServiceManager(context = context)
}
