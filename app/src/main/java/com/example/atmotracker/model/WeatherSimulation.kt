package com.example.atmotracker.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class WeatherSimulation (
    var name: String,
    var frequencyUpdate: Long,
    var location: String,
    var weather: Weather,
    val id: String = UUID.randomUUID().toString().replace("-", "")
)