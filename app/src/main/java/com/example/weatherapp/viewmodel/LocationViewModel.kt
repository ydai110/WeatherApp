package com.example.weatherapp.viewmodel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.di.ActivityScope
import com.example.weatherapp.util.GpsLocationServiceManager
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.UserLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.io.IOException
import java.util.*
import javax.inject.Inject

@ActivityScope
class LocationViewModel @Inject constructor(
    private val gpsLocationServiceManager: GpsLocationServiceManager
) : ViewModel() {

    init {
        Log.d("LocationViewModel", "init")
    }

    private val _currentLocation: MutableLiveData<Resource<UserLocation>> = MutableLiveData()
    val currentLocation: LiveData<Resource<UserLocation>> = _currentLocation

    // When User allow location permission, it will use their GPS Location as Default.
    fun requestGpsLocation(context: Context) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location =
                    UserLocation(
                        name = reverseGeocoding(
                            context,
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude
                        ),
                        latitude = locationResult.lastLocation.latitude,
                        longitude = locationResult.lastLocation.longitude
                    )
                _currentLocation.value = if (location.name.isNotEmpty())
                    Resource.Success(location) else Resource.Failure(msg = "Cannot connect to Geocoder")
            }
        }
        gpsLocationServiceManager.requestGpsLocationResult(locationCallback)
    }

    private fun reverseGeocoding(context: Context, lat: Double, lng: Double): String {
        val mGeocoder = Geocoder(context, Locale.getDefault())
        var addressString = ""

        // Reverse-Geocoding starts
        try {
            val addressList = mGeocoder.getFromLocation(lat, lng, 1)

            // use your lat, long value here
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }

                // Various Parameters of an Address are appended
                // to generate a complete Address
                if (address.premises != null)
                    sb.append(address.premises).append(", ")

                sb.append(address.subAdminArea).append("\n")
                sb.append(address.locality).append(", ")
                sb.append(address.adminArea).append(", ")
                sb.append(address.countryName).append(", ")
                sb.append(address.postalCode)

                // StringBuilder sb is converted into a string
                // and this value is assigned to the
                // initially declared addressString string.
                addressString = sb.toString()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Unable connect to Geocoder", Toast.LENGTH_LONG)
                .show()

        }
        return addressString
    }

    fun setCurrentLocation(userLocation: UserLocation) {
        _currentLocation.value = Resource.Success(userLocation)
    }
}

