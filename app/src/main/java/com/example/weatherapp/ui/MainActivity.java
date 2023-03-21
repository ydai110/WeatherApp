package com.example.weatherapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherApplication;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.di.LocationComponent;
import com.example.weatherapp.util.SharedPreferenceManager;
import com.example.weatherapp.viewmodel.LocationViewModel;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferenceManager sharedPreferenceManager;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private LocationViewModel locationViewModel;

    private final int REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34;

    private final String LOCATION_PERMISSION_DENIED = "location_permission_denied";

    LocationComponent locationComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        locationComponent =
                ((WeatherApplication) getApplication()).getAppComponent().locationComponent().create();

        locationComponent.inject(this);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        sharedPreferences =
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        // Request Location Permission.
        if (!foregroundPermissionApproved() && !denyLocationPermission()) {
            requestForegroundPermission();
        } else if (sharedPreferenceManager.getUser() != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(
                    binding.getRoot(),
                    "Location is needed, " +
                            "Please go to Edit Location to manually set location",
                    Snackbar.LENGTH_LONG
            ).show();
        }

        initViewModel();
    }

    private void initViewModel() {
        locationViewModel =
                new ViewModelProvider(this, viewModelFactory).get(LocationViewModel.class);

        if (sharedPreferenceManager.getUser() != null) {
            locationViewModel.setCurrentLocation(sharedPreferenceManager.getUser());
        }
    }

    private void requestForegroundPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(
                        binding.getRoot(),
                        "GPS Location is Set.",
                        Snackbar.LENGTH_LONG
                ).show();
                locationViewModel.requestGpsLocation(this);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                Snackbar.make(
                        binding.getRoot(),
                        "Location is needed, " +
                                "Please go to Edit Location to manually set location",
                        Snackbar.LENGTH_LONG
                ).show();
                sharedPreferences.edit().putBoolean(LOCATION_PERMISSION_DENIED, true).apply();
            }
        }
    }

    private boolean denyLocationPermission() {
        // Check if user already deny permission
        return sharedPreferences.getBoolean(LOCATION_PERMISSION_DENIED, false);
    }

    private boolean foregroundPermissionApproved() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
