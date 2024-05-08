package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

fun main() {
    WebScraper.scrapeWeatherData()
    WebScraper.scrapeQualityData()

    val weatherData=WebScraper


}