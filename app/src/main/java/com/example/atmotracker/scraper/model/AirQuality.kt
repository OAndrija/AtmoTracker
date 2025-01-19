package com.example.atmotracker.scraper.model

import com.example.atmotracker.scraper.util.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AirQuality(
    val name: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Map<String, String?>
)