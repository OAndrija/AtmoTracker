package com.example.atmotracker.model

import kotlinx.serialization.Serializable

@Serializable
data class SimulationData(
    val airQualitySimulations: List<AirQualitySimulation>,
    val weatherSimulations: List<WeatherSimulation>
)