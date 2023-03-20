package com.example.weatherapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherDetailData
import com.example.weatherapp.util.toFahrenheit

class WeatherAdapter(private val weatherData: MutableList<WeatherDetailData>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : ViewHolder(itemView) {
        val speed: TextView = itemView.findViewById(R.id.speed)
        val humidity: TextView = itemView.findViewById(R.id.humidity)
        val maxTemp: TextView = itemView.findViewById(R.id.max_temp)
        val minTemp: TextView = itemView.findViewById(R.id.min_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_desc, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val context = holder.itemView.context
        val data = weatherData[position]
        holder.speed.text = String.format(context.getString(R.string.speed_title, data.wind.speed))

        holder.humidity.text =
            String.format(context.getString(R.string.humidity_title, data.temperature.humidity))

        holder.maxTemp.text = String.format(
            context.getString(
                R.string.max_temp_title,
                data.temperature.maxTemp.toFahrenheit()
            )
        )

        holder.minTemp.text = String.format(
            context.getString(
                R.string.min_temp_title,
                data.temperature.minTemp.toFahrenheit()
            )
        )
    }

    override fun getItemCount(): Int = weatherData.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(weatherList: MutableList<WeatherDetailData>) {
        weatherData.clear()
        weatherData.addAll(weatherList)
        notifyDataSetChanged()
    }
}