package com.example.atmotracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.atmotracker.model.Walk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.UUID

const val MY_SP_FILE_NAME = "myshared.data"
const val MY_JSON_FILE_NAME = "app_data.json"

class MyApplication : Application() {
    lateinit var walks: MutableList<Walk>
    private lateinit var sharedPref: SharedPreferences
    lateinit var jsonFile: File

    override fun onCreate() {
        super.onCreate()
        walks = mutableListOf()
        initShared()

        jsonFile = File(filesDir, MY_JSON_FILE_NAME)
        println("JSON file path: ${jsonFile.absolutePath}")

        generateRandomWalks(10)

        if (!containsID()) {
            saveID(UUID.randomUUID().toString().replace("-", ""))
            println("Created new ID: ${getID()}")
        }

        println("Already had ID: ${getID()}")

    }

    fun initShared() {
        sharedPref = getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveID(id: String) {
        with(sharedPref.edit()) {
            putString("ID", id)
            apply()
        }
    }

    fun containsID(): Boolean {
        return sharedPref.contains("ID")
    }

    fun getID(): String? {
        return sharedPref.getString("ID", "DefaultNoData")
    }

    fun saveToFile() {
        try {
            val jsonString = Json.encodeToString(walks)
            jsonFile.writeText(jsonString)
            println("Walks data saved successfully.")
        } catch (e: IOException) {
            println("Error saving walks data: ${e.message}")
            e.printStackTrace()
        }
    }

    fun deleteData() {
        try {
            if (jsonFile.exists()) {
                jsonFile.writeText("")
                println("Walks data deleted successfully.")
            }
        } catch (e: IOException) {
            println("Error deleting walks data: ${e.message}")
        }
    }

    private fun generateRandomWalks(count: Int) {
        walks = mutableListOf()
        for (i in 1..count) {
            val randomWalk = Walk(
                date = generateRandomDate(),
                stepCount = (1000..15000).random(),
                caloriesBurned = (50..500).random().toDouble(),
                timeSpent = (400..7200).random().toLong()
            )
            walks.add(randomWalk)
        }
    }

    private fun generateRandomDate(): String {
        val year = (2024..2025).random()
        val month = (1..12).random().toString().padStart(2, '0')
        val day = (1..28).random().toString().padStart(2, '0')
        return "$day/$month/$year"
    }
}