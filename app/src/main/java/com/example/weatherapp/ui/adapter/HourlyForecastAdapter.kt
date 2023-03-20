package com.example.weatherapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherDetailData
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.toFahrenheit

class HourlyForecastAdapter(private val weatherData: MutableList<WeatherDetailData>) :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder>() {

    class HourlyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val speed: TextView = itemView.findViewById(R.id.speed)
        val humidity: TextView = itemView.findViewById(R.id.humidity)
        val maxTemp: TextView = itemView.findViewById(R.id.max_temp)
        val minTemp: TextView = itemView.findViewById(R.id.min_temp)
        val iconView: ImageView = itemView.findViewById(R.id.weather_icon)
        val temp: TextView = itemView.findViewById(R.id.temperature)
        val dateTime: TextView = itemView.findViewById(R.id.date_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {

        val context = holder.itemView.context
        val data = weatherData[position]

        holder.speed.text = String.format(context.getString(R.string.speed_title, data.wind.speed))

        holder.humidity.text =
            String.format(
                context.getString(R.string.humidity_title),
                data.temperature.humidity
            )

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

        Glide.with(holder.itemView.context)
            .load(Constants.BASE_ICON_URL_PREFIX + weatherData[position].weather[0].iconId + Constants.BASE_ICON_URL_SUFFIX)
            .into(holder.iconView)

        holder.temp.text = buildString {
            append(weatherData[position].temperature.temp.toFahrenheit())
            append("\u2109")
        }

        holder.dateTime.text = weatherData[position].dt_txt

    }

    override fun getItemCount(): Int = weatherData.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(weatherList: List<WeatherDetailData>) {
        weatherData.clear()
        weatherData.addAll(weatherList)
        notifyDataSetChanged()
    }
}