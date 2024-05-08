package org.example

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val city: String,
    val temperature: String,
    val windSpeed: String,
    val windGusts: String,
    val precipitation: String,

)
