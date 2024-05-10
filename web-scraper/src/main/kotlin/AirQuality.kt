package org.example

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class AirQuality(
    val name: String,
    val timestamp:LocalDateTime= LocalDateTime.now() ,
    val data: Map<String, Any>

)
