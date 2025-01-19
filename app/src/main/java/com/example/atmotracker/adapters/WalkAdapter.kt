package com.example.atmotracker.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.atmotracker.R
import com.example.atmotracker.databinding.ItemAirQualityBinding
import com.example.atmotracker.databinding.ItemWalkBinding
import com.example.atmotracker.databinding.ItemWeatherBinding
import com.example.atmotracker.model.AirQuality
import com.example.atmotracker.model.Walk
import com.example.atmotracker.model.Weather
import java.util.Locale

sealed class Item {
    data class WalkItem(val walk: Walk) : Item()
    data class AirQualityItem(val airQuality: AirQuality) : Item()
    data class WeatherItem(val weather: Weather) : Item()
}

class GeneralAdapter(
    private val items: MutableList<Item>,
    private val onItemRemoved: (Item) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_WALK = 1
        private const val VIEW_TYPE_AIR_QUALITY = 2
        private const val VIEW_TYPE_WEATHER = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Item.WalkItem -> VIEW_TYPE_WALK
            is Item.AirQualityItem -> VIEW_TYPE_AIR_QUALITY
            is Item.WeatherItem -> VIEW_TYPE_WEATHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WALK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_walk, parent, false)
                WalkViewHolder(view)
            }
            VIEW_TYPE_AIR_QUALITY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_air_quality, parent, false)
                AirQualityViewHolder(view)
            }
            VIEW_TYPE_WEATHER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_weather, parent, false)
                WeatherViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.WalkItem -> (holder as WalkViewHolder).bind(item.walk)
            is Item.AirQualityItem -> (holder as AirQualityViewHolder).bind(item.airQuality)
            is Item.WeatherItem -> (holder as WeatherViewHolder).bind(item.weather)
        }
    }

    override fun getItemCount() = items.size

    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    inner class WalkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWalkBinding.bind(view)
        fun bind(walk: Walk) {
            binding.stepsData.text = walk.stepCount.toString()
            binding.caloriesData.text =
                String.format(Locale.getDefault(), "%.2f kcal", walk.caloriesBurned)
            binding.dateWalkedData.text = walk.date
            binding.timeData.text = formatTime(walk.timeSpent)

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(itemView.context, Item.WalkItem(walk), adapterPosition)
                true
            }
        }
    }

    inner class AirQualityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAirQualityBinding.bind(view)
        fun bind(airQuality: AirQuality) {
            binding.nameTextView.text = airQuality.name ?: "Unknown Location"
            binding.timestampTextView.text = airQuality.timestamp.toString()
            binding.dataTextView.text = airQuality.data.toString()

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(
                    itemView.context,
                    Item.AirQualityItem(airQuality),
                    adapterPosition
                )
                true
            }
        }
    }

    inner class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWeatherBinding.bind(view)
        fun bind(weather: Weather) {
            binding.nameTextView.text = weather.name ?: "Unknown City"
            binding.timestampTextView.text = weather.timestamp.toString()
            binding.dataTextView.text = weather.data.toString()

            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(
                    itemView.context,
                    Item.WeatherItem(weather),
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

    private fun formatTime(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds % 3600) / 60
        val seconds = timeInSeconds % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%d:%02d:%02d hours", hours, minutes, seconds)
            minutes > 1 -> String.format(Locale.getDefault(), "%d:%02d minutes", minutes, seconds)
            minutes == 1L -> String.format(Locale.getDefault(), "%d:%02d minute", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%d seconds", seconds)
        }
    }
}