package com.example.atmotracker.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class AirQualitySimulation (
    var name: String,
    var frequencyUpdate: Long,
    var location: String,
    var airQuality: AirQuality,
    val id: String = UUID.randomUUID().toString().replace("-", "")
)