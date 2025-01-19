package com.example.atmotracker.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Walk (
    val date: String,
    val stepCount: Int,
    val caloriesBurned: Double,
    val timeSpent: Long,
    val id: String = UUID.randomUUID().toString().replace("-", "")
) {
    override fun toString(): String {
        return "Walk(id=$id, date='$date', stepCount=$stepCount, caloriesBurned=$caloriesBurned, timeSpent=$timeSpent)"
    }
}