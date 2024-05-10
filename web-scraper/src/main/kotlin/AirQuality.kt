package org.example

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual


@Serializable
data class AirQuality(
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class) val timestamp:LocalDateTime= LocalDateTime.now() ,

    @Contextual val data: Map<String, String>

)
