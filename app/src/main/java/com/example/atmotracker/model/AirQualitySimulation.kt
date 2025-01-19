package com.example.atmotracker.model

import kotlinx.serialization.Serializable

@Serializable
class AirQualitySimulation (
    var name: String,
    var frequencyUpdate: Long,
    var location: String,
    var airQuality: AirQuality
)