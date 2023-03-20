package com.example.weatherapp.util

sealed class Resource<out T> {
    data class Success<out R>(val value: R) : Resource<R>()
    data class Failure(val msg: String?) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
