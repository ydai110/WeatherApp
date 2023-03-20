package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.data.CitySuggestionsModel
import com.example.weatherapp.util.UserLocation

class SuggestionsAdapter(
    private val suggestionData: MutableList<CitySuggestionsModel>,
    private val listener: OnSuggestionItemClickListener
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    interface OnSuggestionItemClickListener {
        fun onClick(userLocation: UserLocation)
    }

    class SuggestionViewHolder(itemView: View) : ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.suggest_city)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.title.text = buildString {
            append(suggestionData[position].name)
            append(", ")
            append(suggestionData[position].state)
            append(", ")
            append(suggestionData[position].country)
        }

        val location = UserLocation(
            name = holder.title.text as String,
            latitude = suggestionData[position].latitude.toDouble(),
            longitude = suggestionData[position].longtitude.toDouble(),
        )
        holder.itemView.setOnClickListener {
            listener.onClick(location)
        }
    }

    override fun getItemCount(): Int = suggestionData.size

    fun setData(data: List<CitySuggestionsModel>) {
        suggestionData.clear()
        suggestionData.addAll(data)
        notifyDataSetChanged()
    }
}