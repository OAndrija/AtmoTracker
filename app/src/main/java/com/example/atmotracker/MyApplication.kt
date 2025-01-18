package com.example.atmotracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.UUID

const val MY_SP_FILE_NAME = "myshared.data"
const val MY_JSON_FILE_NAME = "app_data.json"

class MyApplication : Application() {
//    lateinit var walks: MutableList<Walk>
    private lateinit var sharedPref: SharedPreferences
    lateinit var jsonFile: File

    override fun onCreate() {
        super.onCreate()
//        walks = mutableListOf()
        initShared()

        jsonFile = File(filesDir, MY_JSON_FILE_NAME)
        println("JSON file path: ${jsonFile.absolutePath}")

        if (!containsID()) {
            saveID(UUID.randomUUID().toString().replace("-", ""))
            println("Created new ID: ${getID()}")
        }

        println("Already had ID: ${getID()}")

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tracking_channel",
                "Step Tracking Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for step tracking and goals"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
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

//    fun saveToFile() {
//        try {
//            val jsonString = Json.encodeToString(walks)
//            jsonFile.writeText(jsonString)
//            println("Walks data saved successfully.")
//        } catch (e: IOException) {
//            println("Error saving walks data: ${e.message}")
//            e.printStackTrace()
//        }
//    }

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
}