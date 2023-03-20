package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.data.CitySuggestionsData
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    init {
        Log.d("SearchViewModel", "init")
    }

    private val _citySuggestion: MutableLiveData<Resource<CitySuggestionsData>> = MutableLiveData()

    val citySuggestion: LiveData<Resource<CitySuggestionsData>> = _citySuggestion

    fun handleQuery(query: String) {
        if (query == "") {
            _citySuggestion.value = Resource.Success(emptyList())
        }
        viewModelScope.launch {
            val response = weatherRepository.requestSearchSuggestions(query = query)
            if (response.isSuccessful) {
                _citySuggestion.value = Resource.Success(response.body() ?: emptyList())
            } else {
                _citySuggestion.value = Resource.Failure(response.message())
            }
        }
    }
}
