package org.example

import java.time.LocalDateTime
import java.util.*

data class Weather(
    val city: String,
    val temperature: String,
    val windSpeed: String,
    val windGusts: String,
    val precipitation: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val id: UUID = UUID.randomUUID()
)
