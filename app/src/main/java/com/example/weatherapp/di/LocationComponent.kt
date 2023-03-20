package com.example.weatherapp.di

import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.ui.SearchFragment
import com.example.weatherapp.ui.WeatherFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ViewModelModule::class])
interface LocationComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LocationComponent
    }

    fun inject(activity: MainActivity)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: WeatherFragment)
}