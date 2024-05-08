package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import sun.net.www.protocol.http.HttpURLConnection
import java.io.IOException
import java.net.URL
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.coroutines.runBlocking

fun sendDataToAPI(weatherData: WeatherResults, qualityData: QualityResults, apiUrl: String) = runBlocking {
    val jsonWeather = Json.encodeToString(WeatherResults.serializer(), weatherData)
    val jsonQuality = Json.encodeToString(QualityResults.serializer(), qualityData)

      // Sending Weather Data
    val (requestWeather, responseWeather, resultWeather) = apiUrl.httpPost()
        .body(jsonWeather)
        .header("Content-Type" to "application/json")
        .response()

    // Check response for Weather Data
    if (resultWeather is Result.Failure) {
        println("Failed to send Weather Data: ${String(responseWeather.data)}")
    } else {
        println("Successfully sent Weather Data")
    }

    // Sending Quality Data
    val (requestQuality, responseQuality, resultQuality) = apiUrl.httpPost()
        .body(jsonQuality)
        .header("Content-Type" to "application/json")
        .response()

    // Check response for Quality Data
    if (resultQuality is Result.Failure) {
        println("Failed to send Quality Data: ${String(responseQuality.data)}")
    } else {
        println("Successfully sent Quality Data")
    }
}




fun main() {


    val weatherData = WebScraper.scrapeWeatherData()
    val qualityData = WebScraper.scrapeQualityData()

    val apiUrl = "https://yourapi.com/data"
    sendDataToAPI(weatherData, qualityData, apiUrl)

}