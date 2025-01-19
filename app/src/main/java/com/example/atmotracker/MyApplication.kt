package com.example.atmotracker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.atmotracker.model.AirQuality
import com.example.atmotracker.model.AirQualitySimulation
import com.example.atmotracker.model.Weather
import com.example.atmotracker.model.WeatherSimulation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.UUID

const val MY_SP_FILE_NAME = "myshared.data"
const val MY_JSON_FILE_NAME = "app_data.json"

class MyApplication : Application() {
    lateinit var airQualitySimulations: MutableList<AirQualitySimulation>
    lateinit var weatherSimulations: MutableList<WeatherSimulation>

    private lateinit var sharedPref: SharedPreferences
    lateinit var jsonFile: File

    override fun onCreate() {
        super.onCreate()

        airQualitySimulations = mutableListOf()
        weatherSimulations = mutableListOf()

        initShared()

        jsonFile = File(filesDir, MY_JSON_FILE_NAME)
        println("JSON file path: ${jsonFile.absolutePath}")

        generateRandomAirQuality(5)
        generateRandomWeather(5)

        if (!containsID()) {
            saveID(UUID.randomUUID().toString().replace("-", ""))
            println("Created new ID: ${getID()}")
        }

        println("Already had ID: ${getID()}")
    }

    fun initShared() {
        sharedPref = getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveID(id: String) {
        with(sharedPref.edit()) {
            putString("ID", id)
            apply()
        }
    }

    fun containsID(): Boolean {
        return sharedPref.contains("ID")
    }

    fun getID(): String? {
        return sharedPref.getString("ID", "DefaultNoData")
    }

    private fun generateRandomAirQuality(count: Int) {
        for (i in 1..count) {
            val randomData = mapOf(
                "pm10" to (5..50).random().toString(),
                "pm25" to (10..100).random().toString(),
                "ozon" to (0..10).random().toString(),
                "no2" to (0..10).random().toString()
            )

            val airQuality = AirQuality(
                name = "Location $i",
                data = randomData
            )

            val airQualitySimulation = AirQualitySimulation(
                name = "Air Quality",
                frequencyUpdate = (60..3600).random().toLong(),
                location = "Location $i",
                airQuality = airQuality
            )

            airQualitySimulations.add(airQualitySimulation)
        }
    }

    private fun generateRandomWeather(count: Int) {
        for (i in 1..count) {
            val randomData = mapOf(
                "temperature" to (-5..35).random().toString(),
                "precipitation" to (30..80).random().toString(),
                "windSpeed" to (0..20).random().toString(),
                "windGusts" to (0..20).random().toString()
            )
            val weather = Weather(
                name = "City $i",
                data = randomData
            )

            val weatherSimulation = WeatherSimulation(
                name = "Weather",
                frequencyUpdate = (60..3600).random().toLong(),
                location = "Location $i",
                weather = weather
            )

            weatherSimulations.add(weatherSimulation)
        }
    }

    fun saveSimulations() {
        try {
            val data = mapOf(
                "airQualitySimulations" to airQualitySimulations,
                "weatherSimulations" to weatherSimulations
            )
            jsonFile.writeText(Json.encodeToString(data))
            println("Simulations saved successfully.")
        } catch (e: IOException) {
            println("Failed to save simulations: ${e.message}")
        }
    }

    fun loadSimulations() {
        try {
            if (jsonFile.exists()) {
                val data: Map<String, List<*>> = Json.decodeFromString(jsonFile.readText())
                airQualitySimulations = data["airQualitySimulations"]
                    ?.filterIsInstance<AirQualitySimulation>()
                    ?.toMutableList() ?: mutableListOf()

                weatherSimulations = data["weatherSimulations"]
                    ?.filterIsInstance<WeatherSimulation>()
                    ?.toMutableList() ?: mutableListOf()

                println("Simulations loaded successfully.")
            } else {
                println("No simulation file found to load.")
            }
        } catch (e: IOException) {
            println("Failed to load simulations: ${e.message}")
        }
    }
}