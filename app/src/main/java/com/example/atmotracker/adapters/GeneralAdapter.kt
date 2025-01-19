package com.example.atmotracker.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.atmotracker.R
import com.example.atmotracker.databinding.ItemAirQualityBinding
import com.example.atmotracker.databinding.ItemWeatherBinding
import com.example.atmotracker.model.AirQualitySimulation
import com.example.atmotracker.model.WeatherSimulation

sealed class Item {
    data class AirQualitySimulationItem(val airQuality: AirQualitySimulation) : Item()
    data class WeatherSimulationItem(val weather: WeatherSimulation) : Item()
}

class GeneralAdapter(
    private val items: MutableList<Item>,
    private val onItemRemoved: (Item) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_AIR_QUALITY = 1
        private const val VIEW_TYPE_WEATHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Item.AirQualitySimulationItem -> VIEW_TYPE_AIR_QUALITY
            is Item.WeatherSimulationItem -> VIEW_TYPE_WEATHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_AIR_QUALITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_air_quality, parent, false)
                AirQualitySimulationViewHolder(view)
            }
            VIEW_TYPE_WEATHER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_weather, parent, false)
                WeatherSimulationViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.AirQualitySimulationItem -> (holder as AirQualitySimulationViewHolder).bind(item.airQuality)
            is Item.WeatherSimulationItem -> (holder as WeatherSimulationViewHolder).bind(item.weather)
        }
    }

    override fun getItemCount() = items.size

    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    inner class AirQualitySimulationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAirQualityBinding.bind(view)

        fun bind(airQualitySimulation: AirQualitySimulation) {
            binding.airQualityFreqLabel.text = "${airQualitySimulation.frequencyUpdate}s"
            binding.locationLabel.text = "${airQualitySimulation.location}"

            val pm10 = airQualitySimulation.airQuality.data["pm10"] ?: "N/A"
            val pm25 = airQualitySimulation.airQuality.data["pm25"] ?: "N/A"
            val ozon = airQualitySimulation.airQuality.data["ozon"] ?: "N/A"
            val no2 = airQualitySimulation.airQuality.data["no2"] ?: "N/A"

            binding.pm10Data.text = "$pm10 µg/m³"
            binding.pm25Data.text = "$pm25 µg/m³"
            binding.ozonData.text = "$ozon µg/m³"
            binding.no2Data.text = "$no2 µg/m³"

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(
                    itemView.context,
                    Item.AirQualitySimulationItem(airQualitySimulation),
                    adapterPosition
                )
                true
            }
        }
    }

    inner class WeatherSimulationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWeatherBinding.bind(view)

        fun bind(weatherSimulation: WeatherSimulation) {
            binding.weatherFreqLabel.text = "${weatherSimulation.frequencyUpdate}s"
            binding.locationLabel.text = "${weatherSimulation.location}"

            val temperature = weatherSimulation.weather.data["temperature"] ?: "N/A"
            val precipitation = weatherSimulation.weather.data["precipitation"] ?: "N/A"
            val windSpeed = weatherSimulation.weather.data["windSpeed"] ?: "N/A"
            val windGusts = weatherSimulation.weather.data["windGusts"] ?: "N/A"

            binding.temperatureData.text = "$temperature °C"
            binding.precipitationData.text = "$precipitation mm"
            binding.windSpeedData.text = "$windSpeed km/h"
            binding.windGustsData.text = "$windGusts km/h"

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(
                    itemView.context,
                    Item.WeatherSimulationItem(weatherSimulation),
                    adapterPosition
                )
                true
            }
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, item: Item, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
                removeItem(position)
                onItemRemoved(item)
            }
            .setNegativeButton("No", null)
            .show()
    }
}