package com.example.atmotracker.model

import kotlinx.serialization.Serializable

@Serializable
class WeatherSimulation (
    var name: String,
    var frequencyUpdate: Long,
    var location: String,
    var weather: Weather
)