package org.example

import java.time.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class AirQuality(
    val name: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Map<String, String?>
)
