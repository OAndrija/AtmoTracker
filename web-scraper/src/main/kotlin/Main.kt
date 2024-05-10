package org.example


import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

fun sendWeatherData(weather: Weather) {
    val client = OkHttpClient()


    val jsonWeather = Json.encodeToString(Weather.serializer(), weather)


    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = jsonWeather.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://localhost:3001/data")
        .post(body)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Request failed with code ${response.code}")
            println("Response message: ${response.message}")
            println("Response body: ${response.body?.string()}")
            throw IOException("Unexpected code $response")
        }
        println("Response: ${response.body?.string()}")
    }
}


fun sendQualityData(airQuality: AirQuality) {
    val client = OkHttpClient()

    val jsonWeather = Json.encodeToString(AirQuality.serializer(), airQuality)

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = jsonWeather.toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://localhost:3001/data")
        .post(body)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Request failed with code ${response.code}")
            println("Response message: ${response.message}")
            println("Response body: ${response.body?.string()}")
            throw IOException("Unexpected code $response")
        }
        println("Response: ${response.body?.string()}")
    }
}

fun main() {

    val weatherData = WebScraper.scrapeWeatherData()
    weatherData.weatherTableRows.forEach { weather ->
        sendWeatherData(weather)


        val qualityData = WebScraper.scrapeQualityData()
        qualityData.qualityTableRows.forEach { airQuality ->
            sendQualityData(airQuality)
        }
    }
}