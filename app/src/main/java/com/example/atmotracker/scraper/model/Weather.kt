package com.example.atmotracker.scraper.model

import com.example.atmotracker.scraper.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Weather(
    val name: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Map<String, String?>
)