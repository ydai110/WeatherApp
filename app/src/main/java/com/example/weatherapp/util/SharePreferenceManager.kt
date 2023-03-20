package com.example.weatherapp.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceManager @Inject constructor(val context: Context) {

    private val PREFS_NAME = "sharedpref"

    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUSer(userLocation: UserLocation) {
        sharedPref.edit().putString("user", Gson().toJson(userLocation)).apply()
    }

    fun getUser(): UserLocation? {
        val data = sharedPref.getString("user", null) ?: return null
        return Gson().fromJson(data, UserLocation::class.java)
    }
}