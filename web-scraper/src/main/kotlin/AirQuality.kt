package org.example

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class AirQuality(
    val city: String,
    val pm10: String,
    val pm25: String,
    val so2: String,
    val co: String,
    val ozon: String,
    val no2: String,
    val benzen: String,

)
