package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.HttpURLConnection
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.URL

fun sendPostRequest(urlStr: String, jsonData: String) {

    try {
        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.doOutput = true

        OutputStreamWriter(connection.outputStream).use { os ->
            os.write(jsonData)
            os.flush()
        }

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            println("Data was sent successfully.")
        } else {
            println("POST request failed. Response code: ${connection.responseCode}")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


fun main() {

    CoroutineScope(Dispatchers.IO).launch {
        val weatherData = WebScraper.scrapeWeatherData()
        val qualityData = WebScraper.scrapeQualityData()

        val jsonWeather = Json.encodeToString(WeatherResults.serializer(), weatherData)
        val jsonQuality = Json.encodeToString(QualityResults.serializer(), qualityData)

        sendPostRequest("https://api.example.com/weather", jsonWeather)
        sendPostRequest("https://api.example.com/quality", jsonQuality)

    }
}