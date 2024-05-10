package org.example

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable

data class Weather(
    val name: String,
    val timestamp:LocalDateTime= LocalDateTime.now() ,
    val data: Map<String, Any>

)
