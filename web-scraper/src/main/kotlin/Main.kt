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

fun sendWeatherDataIndividual(weatherData: WeatherResults) {
    CoroutineScope(Dispatchers.IO).launch {
        weatherData.weatherTableRows.forEach { weather ->
            try {
                val url = URL("http://yourapi.com/weather")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true

                    outputStream.use { os ->
                        val input = Json.encodeToString(Weather.serializer(), weather)
                        os.write(input.toByteArray())
                    }

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw IOException("HTTP error code: $responseCode")
                    }
                }
            } catch (e: Exception) {
                println("Failed to send data for ${weather.city}: ${e.message}")

            }
        }
    }
}




fun main() {


    sendWeatherDataIndividual(WebScraper.scrapeWeatherData())

}