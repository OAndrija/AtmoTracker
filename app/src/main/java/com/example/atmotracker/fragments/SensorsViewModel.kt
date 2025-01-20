package com.example.atmotracker.fragments

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.atmotracker.MY_SP_FILE_NAME
import com.example.atmotracker.scraper.WebScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SensorsViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherTimeHandler = Handler(Looper.getMainLooper())
    private val airQualityTimeHandler = Handler(Looper.getMainLooper())

    private val _weatherElapsedTime = MutableLiveData<Long>()
    val weatherElapsedTime: LiveData<Long> get() = _weatherElapsedTime

    private val _airQualityElapsedTime = MutableLiveData<Long>()
    val airQualityElapsedTime: LiveData<Long> get() = _airQualityElapsedTime

    private val _weatherSavedTime = MutableLiveData<Long>()
    val weatherSavedTime: LiveData<Long> get() = _weatherSavedTime

    private val _airQualitySavedTime = MutableLiveData<Long>()
    val airQualitySavedTime: LiveData<Long> get() = _airQualitySavedTime

    private var weatherStartTime = System.currentTimeMillis()
    private var airQualityStartTime = System.currentTimeMillis()

    private val _fetchStatus = MutableLiveData<String>()
    val fetchStatus: LiveData<String> get() = _fetchStatus

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val updateInterval = 1000L

    init {
        loadSavedTimes()
        startTimers()
    }

    fun loadSavedTimes() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)

        val weatherTime = hoursToMillis(
            sharedPreferences.getInt("weather_hours", 0),
            sharedPreferences.getInt("weather_minutes", 0),
            sharedPreferences.getInt("weather_seconds", 0)
        )
        _weatherSavedTime.value = weatherTime

        val airQualityTime = hoursToMillis(
            sharedPreferences.getInt("air_quality_hours", 0),
            sharedPreferences.getInt("air_quality_minutes", 0),
            sharedPreferences.getInt("air_quality_seconds", 0)
        )
        _airQualitySavedTime.value = airQualityTime
    }

    fun resetStartTimes() {
        weatherStartTime = System.currentTimeMillis()
        airQualityStartTime = System.currentTimeMillis()
    }

    private fun hoursToMillis(hours: Int, minutes: Int, seconds: Int): Long {
        return TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())
    }

    private fun startTimers() {
        weatherTimeHandler.post(object : Runnable {
            override fun run() {
                updateWeatherElapsedTime()
                weatherTimeHandler.postDelayed(this, updateInterval)
            }
        })

        airQualityTimeHandler.post(object : Runnable {
            override fun run() {
                updateAirQualityElapsedTime()
                airQualityTimeHandler.postDelayed(this, updateInterval)
            }
        })
    }

    private fun updateWeatherElapsedTime() {
        val elapsedTime = System.currentTimeMillis() - weatherStartTime
        val savedTime = _weatherSavedTime.value ?: 0L
        if (elapsedTime >= savedTime) {
            weatherStartTime = System.currentTimeMillis()
            _weatherElapsedTime.value = 0
            fetchWeatherData()
        } else {
            _weatherElapsedTime.value = elapsedTime
        }
    }

    private fun updateAirQualityElapsedTime() {
        val elapsedTime = System.currentTimeMillis() - airQualityStartTime
        val savedTime = _airQualitySavedTime.value ?: 0L
        if (elapsedTime >= savedTime) {
            airQualityStartTime = System.currentTimeMillis()
            _airQualityElapsedTime.value = 0
            fetchAirQualityData()
        } else {
            _airQualityElapsedTime.value = elapsedTime
        }
    }

    private fun fetchWeatherData() {
        ioScope.launch {
            try {
                val weatherResults = WebScraper.scrapeWeatherData()

                if (weatherResults.weatherTableRows.isNotEmpty()) {
                    Log.d("SensorsViewModel", "Fetched Weather Data: ${weatherResults.weatherTableRows}")

                    weatherResults.weatherTableRows.forEach { weather ->
                        WebScraper.sendWeatherData(weather)
                        Log.d("SensorsViewModel", "Sent Weather Data: $weather")
                    }

                    withContext(Dispatchers.Main) {
                        _fetchStatus.value = "All weather data updated!"
                    }
                } else {
                    Log.e("SensorsViewModel", "No weather data found!")
                    withContext(Dispatchers.Main) {
                        _fetchStatus.value = "No weather data found!"
                    }
                }
            } catch (e: Exception) {
                Log.e("SensorsViewModel", "Error fetching weather data: ${e.message}")
                withContext(Dispatchers.Main) {
                    _fetchStatus.value = "Error fetching weather data: ${e.message}"
                }
            }
        }
    }

    private fun fetchAirQualityData() {
        ioScope.launch {
            try {
                val qualityResults = WebScraper.scrapeQualityData()

                if (qualityResults.qualityTableRows.isNotEmpty()) {
                    Log.d("SensorsViewModel", "Fetched Air Quality Data: ${qualityResults.qualityTableRows}")

                    qualityResults.qualityTableRows.forEach { quality ->
                        WebScraper.sendQualityData(quality)
                        Log.d("SensorsViewModel", "Sent Air Quality Data: $quality")
                    }

                    withContext(Dispatchers.Main) {
                        _fetchStatus.value = "All air quality data updated!"
                    }
                } else {
                    Log.e("SensorsViewModel", "No air quality data found!")
                    withContext(Dispatchers.Main) {
                        _fetchStatus.value = "No air quality data found!"
                    }
                }
            } catch (e: Exception) {
                Log.e("SensorsViewModel", "Error fetching air quality data: ${e.message}")
                withContext(Dispatchers.Main) {
                    _fetchStatus.value = "Error fetching air quality data: ${e.message}"
                }
            }
        }
    }
}
