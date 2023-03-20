package com.example.weatherapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.di.LocationComponent
import com.example.weatherapp.util.SharedPreferenceManager
import com.example.weatherapp.viewmodel.LocationViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

private const val LOCATION_PERMISSION_DENIED = "location_permission_denied"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    //    @Inject
    lateinit var locationViewModel: LocationViewModel

    lateinit var locationComponent: LocationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        locationComponent =
            (application as WeatherApplication).appComponent.locationComponent().create()

        locationComponent.inject(this)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Navigation
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Request Location Permission.
        if (!foregroundPermissionApproved() && !denyLocationPermission()) {
            requestForegroundPermission()
        } else if (sharedPreferenceManager.getUser() != null) {
            binding.progressBar.isVisible = true
        } else {
            Snackbar.make(
                binding.root,
                "Location is needed, " +
                        "Please go to Edit Location to manually set location",
                Snackbar.LENGTH_LONG
            ).show()
        }

        initViewModel()
    }

    private fun initViewModel() {
        locationViewModel =
            ViewModelProvider(this, viewModelFactory)[LocationViewModel::class.java]
        
        sharedPreferenceManager.getUser()?.let {
            locationViewModel.setCurrentLocation(it)
        }
    }

    private fun requestForegroundPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() -> Unit
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                {
                    Snackbar.make(
                        binding.root,
                        "GPS Location is Set.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    locationViewModel.requestGpsLocation(this)
                    binding.progressBar.isVisible = true
                }

                else -> {
                    Snackbar.make(
                        binding.root,
                        "Location is needed, " +
                                "Please go to Edit Location to manually set location",
                        Snackbar.LENGTH_LONG
                    ).show()
                    sharedPreferences.edit().putBoolean(LOCATION_PERMISSION_DENIED, true).apply()
                }
            }
        }
    }

    private fun denyLocationPermission(): Boolean {
        // Check if user already deny permission
        return sharedPreferences.getBoolean(LOCATION_PERMISSION_DENIED, false)
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
