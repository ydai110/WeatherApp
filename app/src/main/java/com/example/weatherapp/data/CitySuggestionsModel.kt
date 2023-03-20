package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

typealias CitySuggestionsData = List<CitySuggestionsModel>

data class CitySuggestionsModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val latitude: Float,
    @SerializedName("lon")
    val longtitude: Float,
    @SerializedName("country")
    val country: String,
    @SerializedName("state")
    val state: String,
)
