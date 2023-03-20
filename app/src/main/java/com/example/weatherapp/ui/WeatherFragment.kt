package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.data.WeatherDetailData
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.ui.adapter.HourlyForecastAdapter
import com.example.weatherapp.util.Constants.BASE_ICON_URL_PREFIX
import com.example.weatherapp.util.Constants.BASE_ICON_URL_SUFFIX
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.SharedPreferenceManager
import com.example.weatherapp.util.UserLocation
import com.example.weatherapp.util.toFahrenheit
import com.example.weatherapp.viewmodel.LocationViewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


private const val TAG = "WeatherFragment"

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var locationViewModel: LocationViewModel

    //    @Inject
    lateinit var weatherViewModel: WeatherViewModel

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    private lateinit var weatherAdapter: HourlyForecastAdapter

    @Inject
    lateinit var weatherApi: WeatherApi

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).locationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initViewModel()
        initRecyclerView()
    }

    private fun initUI() {
        binding.editLocation.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun initViewModel() {
        weatherViewModel =
            ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]

        locationViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[LocationViewModel::class.java]

        weatherViewModel.weatherDetail.observe(viewLifecycleOwner, Observer { weatherDetailData ->
            when (weatherDetailData) {
                is Resource.Success -> {
                    val progressBar = requireActivity().findViewById<ProgressBar>(R.id.progress_bar)
                    progressBar.isVisible = false
                    updateUI(weatherDetailData.value)
                }
                is Resource.Failure ->
                    handleFailure(weatherDetailData.msg)
                else -> Unit
            }
        })

        weatherViewModel.hourlyForecast.observe(viewLifecycleOwner, Observer { hourlyForecast ->
            when (hourlyForecast) {
                is Resource.Success ->
                    weatherAdapter.setData(hourlyForecast.value.forecast)
                is Resource.Failure -> {
                    handleFailure(hourlyForecast.msg)
                }
                else -> Unit
            }
        })

        locationViewModel.currentLocation.observe(viewLifecycleOwner, Observer { currentLocation ->
            Log.d(TAG, "GPS Location is updated")
            when (currentLocation) {
                is Resource.Success -> {
                    saveUserLocation(currentLocation.value)
                    weatherViewModel.requestWeatherDetail(currentLocation.value)
                    weatherViewModel.requestHourlyForecast(currentLocation.value)
                }
                else -> Unit
            }
        })
    }

    private fun initRecyclerView() {
        weatherAdapter = HourlyForecastAdapter(mutableListOf())

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.hourlyRecyclerview)

        binding.hourlyRecyclerview.apply {
            adapter = weatherAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun updateUI(weatherDetailData: WeatherDetailData) {
        binding.weatherMain.locationName.text =
            (locationViewModel.currentLocation.value as Resource.Success).value.name
        binding.weatherMain.temperature.text =
            buildString {
                append(weatherDetailData.temperature.temp.toFahrenheit().toString())
                append("\u2109")
            }
        Glide.with(this)
            .load(BASE_ICON_URL_PREFIX + weatherDetailData.weather[0].iconId + BASE_ICON_URL_SUFFIX)
            .into(binding.weatherMain.weatherIcon)
        binding.weatherDesc.humidity.text =
            String.format(
                getString(R.string.humidity_title),
                weatherDetailData.temperature.humidity
            )
        binding.weatherDesc.maxTemp.text =
            String.format(
                getString(R.string.max_temp_title),
                weatherDetailData.temperature.maxTemp.toFahrenheit()
            )
        binding.weatherDesc.minTemp.text =
            String.format(
                getString(R.string.min_temp_title),
                weatherDetailData.temperature.minTemp.toFahrenheit()
            )
        binding.weatherDesc.speed.text =
            String.format(
                getString(R.string.speed_title),
                weatherDetailData.wind.speed
            )
    }

    private fun saveUserLocation(userLocation: UserLocation) {
        sharedPreferenceManager.saveUSer(userLocation)
    }

    private fun handleFailure(msg: String?) {
        if (msg != null) {
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
