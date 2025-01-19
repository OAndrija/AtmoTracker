package com.example.atmotracker.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.atmotracker.MY_SP_FILE_NAME
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.databinding.FragmentSensorsBinding
import com.example.atmotracker.scraper.WebScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SensorsFragment : Fragment() {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication

    private val weatherTimeHandler = Handler(Looper.getMainLooper())
    private val airQualityTimeHandler = Handler(Looper.getMainLooper())

    private var weatherStartTime: Long = 0L
    private var airQualityStartTime: Long = 0L

    private var weatherSavedTime: Long = 0L
    private var airQualitySavedTime: Long = 0L

    private val updateInterval: Long = 1000L // Update every second

    private val weatherUpdater = object : Runnable {
        override fun run() {
            updateWeatherElapsedTime()
            weatherTimeHandler.postDelayed(this, updateInterval)
        }
    }

    private val airQualityUpdater = object : Runnable {
        override fun run() {
            updateAirQualityElapsedTime()
            airQualityTimeHandler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedTimes()

        weatherStartTime = System.currentTimeMillis()
        airQualityStartTime = System.currentTimeMillis()

        binding.weatherProgressbar.progressMax = weatherSavedTime.toFloat()
        binding.airQualityProgressbar.progressMax = airQualitySavedTime.toFloat()

        weatherTimeHandler.post(weatherUpdater)
        airQualityTimeHandler.post(airQualityUpdater)

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        parentFragmentManager.setFragmentResultListener("settings_result", this) { _, result ->
            val valuesChanged = result.getBoolean("values_changed", false)
            if (valuesChanged) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loadSavedTimes()

                    binding.weatherProgressbar.progressMax = weatherSavedTime.toFloat()
                    binding.airQualityProgressbar.progressMax = airQualitySavedTime.toFloat()

                    weatherStartTime = System.currentTimeMillis()
                    airQualityStartTime = System.currentTimeMillis()
                }, 300)
            }
        }
    }

    private fun loadSavedTimes() {
        val sharedPreferences = requireContext().getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)

        // Weather time values
        val weatherHours = sharedPreferences.getInt("weather_hours", 0)
        val weatherMinutes = sharedPreferences.getInt("weather_minutes", 0)
        val weatherSeconds = sharedPreferences.getInt("weather_seconds", 0)

        Log.d("SensorsFragment", "AFTER UPDATING: Weather Time: $weatherSavedTime, Air Quality Time: $airQualitySavedTime")

        weatherSavedTime = hoursToMillis(weatherHours, weatherMinutes, weatherSeconds)
        val weatherTimeFormatted = formatElapsedTime(weatherSavedTime)
        binding.weatherUpdateTimeText.text = weatherTimeFormatted

        // Air Quality time values
        val airQualityHours = sharedPreferences.getInt("air_quality_hours", 0)
        val airQualityMinutes = sharedPreferences.getInt("air_quality_minutes", 0)
        val airQualitySeconds = sharedPreferences.getInt("air_quality_seconds", 0)

        Log.d("SensorsFragment", "Loaded air quality values: Hours=$airQualityHours, Minutes=$airQualityMinutes, Seconds=$airQualitySeconds")

        airQualitySavedTime = hoursToMillis(airQualityHours, airQualityMinutes, airQualitySeconds)
        val airQualityTimeFormatted = formatElapsedTime(airQualitySavedTime)
        binding.airQualityUpdateTimeText.text = airQualityTimeFormatted
    }

    private fun hoursToMillis(hours: Int, minutes: Int, seconds: Int): Long {
        return TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())
    }

    private fun updateWeatherElapsedTime() {
        val elapsedTime = System.currentTimeMillis() - weatherStartTime

        if (elapsedTime >= weatherSavedTime) {
            weatherStartTime = System.currentTimeMillis()
            binding.weatherTimeElapsedText.text = formatElapsedTime(0)
            binding.weatherProgressbar.setProgressWithAnimation(0f)

            fetchWeatherData()
        } else {
            binding.weatherTimeElapsedText.text = formatElapsedTime(elapsedTime)
            binding.weatherProgressbar.setProgressWithAnimation(elapsedTime.toFloat())
        }
    }

    private fun updateAirQualityElapsedTime() {
        val elapsedTime = System.currentTimeMillis() - airQualityStartTime

        if (elapsedTime >= airQualitySavedTime) {
            airQualityStartTime = System.currentTimeMillis()
            binding.airQualityTimeElapsedText.text = formatElapsedTime(0)
            binding.airQualityProgressbar.setProgressWithAnimation(0f)

            fetchAirQualityData()
        } else {
            binding.airQualityTimeElapsedText.text = formatElapsedTime(elapsedTime)
            binding.airQualityProgressbar.setProgressWithAnimation(elapsedTime.toFloat())
        }
    }

    private fun formatElapsedTime(elapsedTimeMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        weatherTimeHandler.removeCallbacks(weatherUpdater)
        airQualityTimeHandler.removeCallbacks(airQualityUpdater)
        _binding = null
    }

    private fun fetchWeatherData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherResults = WebScraper.scrapeWeatherData()

                if (weatherResults.weatherTableRows.isNotEmpty()) {
                    Log.d("SensorsFragment", "Fetched Weather Data: ${weatherResults.weatherTableRows}")

                    weatherResults.weatherTableRows.forEach { weather ->
                        WebScraper.sendWeatherData(weather)
                        Log.d("SensorsFragment", "Sent Weather Data: $weather")
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "All weather data updated!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("SensorsFragment", "No weather data found!")
                }
            } catch (e: Exception) {
                Log.e("SensorsFragment", "Error fetching weather data: ${e.message}")
            }
        }
    }

    private fun fetchAirQualityData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val qualityResults = WebScraper.scrapeQualityData()

                if (qualityResults.qualityTableRows.isNotEmpty()) {
                    Log.d("SensorsFragment", "Fetched Air Quality Data: ${qualityResults.qualityTableRows}")

                    qualityResults.qualityTableRows.forEach { quality ->
                        WebScraper.sendQualityData(quality)
                        Log.d("SensorsFragment", "Sent Weather Data: $quality")
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "All air quality data updated!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("SensorsFragment", "No air quality data found!")
                }
            } catch (e: Exception) {
                Log.e("SensorsFragment", "Error fetching air quality data: ${e.message}")
            }
        }
    }
}