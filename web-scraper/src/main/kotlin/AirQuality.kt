package org.example

import java.time.LocalDateTime
import java.util.*

data class AirQuality(
    val city: String,
    val pm10: String,
    val pm25: String,
    val so2: String,
    val co: String,
    val ozon: String,
    val no2: String,
    val benzen: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val id: UUID = UUID.randomUUID()
)
