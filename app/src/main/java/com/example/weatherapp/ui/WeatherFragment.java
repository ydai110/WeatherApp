package com.example.weatherapp.ui;

import static com.example.weatherapp.util.Constants.BASE_ICON_URL_PREFIX;
import static com.example.weatherapp.util.Constants.BASE_ICON_URL_SUFFIX;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.data.HourlyForecast;
import com.example.weatherapp.data.WeatherDetailData;
import com.example.weatherapp.databinding.FragmentWeatherBinding;
import com.example.weatherapp.ui.adapter.HourlyForecastAdapter;
import com.example.weatherapp.util.Resource;
import com.example.weatherapp.util.SharedPreferenceManager;
import com.example.weatherapp.util.UserLocation;
import com.example.weatherapp.viewmodel.LocationViewModel;
import com.example.weatherapp.viewmodel.WeatherViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.inject.Inject;

public class WeatherFragment extends Fragment {
    private final String TAG = "WeatherFragment";
    private FragmentWeatherBinding binding;

    private LocationViewModel locationViewModel;

    private WeatherViewModel weatherViewModel;

    @Inject
    public SharedPreferenceManager sharedPreferenceManager;

    private HourlyForecastAdapter weatherAdapter;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((MainActivity) requireActivity()).locationComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initViewModel();
        initRecyclerView();
    }

    private void initUI() {
        binding.editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WeatherFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }


    private void initViewModel() {
        weatherViewModel =
                new ViewModelProvider(this, viewModelFactory).get(WeatherViewModel.class);

        locationViewModel =
                new ViewModelProvider(requireActivity(), viewModelFactory)
                        .get(LocationViewModel.class);

        weatherViewModel.getWeatherDetail().observe(
                getViewLifecycleOwner(),
                weatherDetailDataResource -> {
                    if (weatherDetailDataResource instanceof Resource.Success) {
                        ProgressBar progressBar = requireActivity().findViewById(R.id.progress_bar);
                        progressBar.setVisibility(View.GONE);
                        updateUI(((Resource.Success<WeatherDetailData>) weatherDetailDataResource)
                                .getValue());
                    } else if (weatherDetailDataResource instanceof Resource.Failure) {
                        handleFailure(((Resource.Failure) weatherDetailDataResource).getMsg());
                    }
                });

        weatherViewModel.getHourlyForecast().observe(
                getViewLifecycleOwner(), hourlyForecastResource -> {
                    if (hourlyForecastResource instanceof Resource.Success) {
                        weatherAdapter.setData((
                                (Resource.Success<HourlyForecast>) hourlyForecastResource)
                                .getValue().getForecast()
                        );
                    } else if (hourlyForecastResource instanceof Resource.Failure) {
                        handleFailure(((Resource.Failure) hourlyForecastResource).getMsg());
                    }
                });

        locationViewModel.getCurrentLocation().observe(
                getViewLifecycleOwner(), userLocationResource -> {
                    Log.d(TAG, "GPS Location is updated");
                    if (userLocationResource instanceof Resource.Success) {
                        UserLocation currentLocation = (
                                (Resource.Success<UserLocation>) userLocationResource).getValue();
                        saveUserLocation(currentLocation);
                        weatherViewModel.requestWeatherDetail(currentLocation);
                        weatherViewModel.requestHourlyForecast(currentLocation);
                    }
                });
    }

    private void initRecyclerView() {
        weatherAdapter = new HourlyForecastAdapter(new ArrayList<>());

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.hourlyRecyclerview);

        binding.hourlyRecyclerview.setAdapter(weatherAdapter);
        binding.hourlyRecyclerview.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void updateUI(WeatherDetailData weatherDetailData) {
        if (locationViewModel.getCurrentLocation().getValue() instanceof Resource.Success) {
            UserLocation userLocation = ((Resource.Success<UserLocation>) locationViewModel
                    .getCurrentLocation().getValue()).getValue();
            binding.weatherMain.locationName.setText(userLocation.getName());
        }

        String temp = convertToFahrenheit(weatherDetailData.getTemperature().getTemp()) +
                "\u2109";
        binding.weatherMain.temperature.setText(temp);

        Glide.with(this)
                .load(BASE_ICON_URL_PREFIX +
                        weatherDetailData.getWeather().get(0).getIconId() +
                        BASE_ICON_URL_SUFFIX)
                .into(binding.weatherMain.weatherIcon);

        binding.weatherDesc.humidity.setText(
                String.format(
                        getString(R.string.humidity_title),
                        weatherDetailData.getTemperature().getHumidity()
                ));

        binding.weatherDesc.maxTemp.setText(
                String.format(
                        getString(R.string.max_temp_title),
                        convertToFahrenheit(weatherDetailData.getTemperature().getMaxTemp())
                )
        );

        binding.weatherDesc.minTemp.setText(
                String.format(
                        getString(R.string.min_temp_title),
                        convertToFahrenheit(weatherDetailData.getTemperature().getMinTemp())
                )
        );

        binding.weatherDesc.speed.setText(
                String.format(
                        getString(R.string.speed_title),
                        weatherDetailData.getWind().getSpeed()
                )
        );
    }

    private void saveUserLocation(UserLocation userLocation) {
        sharedPreferenceManager.saveUSer(userLocation);
    }

    private void handleFailure(String msg) {
        if (msg != null) {
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        }
    }

    private double convertToFahrenheit(double num) {
        BigDecimal decimal = new BigDecimal(String.valueOf(1.8 * (num - 273) + 32))
                .setScale(2, RoundingMode.UP);
        return decimal.doubleValue();
    }
}
