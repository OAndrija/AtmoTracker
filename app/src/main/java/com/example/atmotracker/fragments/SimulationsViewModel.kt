package com.example.atmotracker.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.atmotracker.model.AirQualitySimulation
import com.example.atmotracker.model.WeatherSimulation
import com.example.atmotracker.scraper.WebScraper
import kotlinx.coroutines.*

class SimulationsViewModel : ViewModel() {
    private val updateScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val updateJobs: MutableMap<String, Job> = mutableMapOf()

    private val _simulationUpdates = MutableLiveData<Map<String, String>>()
    val simulationUpdates: LiveData<Map<String, String>> get() = _simulationUpdates

    private val simulationStatuses = mutableMapOf<String, String>()

    init {
        Log.d("SimulationsViewModel", "ViewModel created")
    }

    fun startSimulationUpdates(simulation: Any) {
        val id = when (simulation) {
            is AirQualitySimulation -> simulation.id
            is WeatherSimulation -> simulation.id
            else -> {
                Log.e("SimulationsViewModel", "Unknown simulation type: ${simulation::class.simpleName}")
                return
            }
        }

        val frequencyInSeconds = when (simulation) {
            is AirQualitySimulation -> simulation.frequencyUpdate
            is WeatherSimulation -> simulation.frequencyUpdate
            else -> {
                Log.e("SimulationsViewModel", "No frequency found for simulation: $id")
                return
            }
        }

        val frequencyInMillis = frequencyInSeconds * 1000L

        updateJobs[id]?.cancel()
        Log.d("SimulationsViewModel", "Starting updates for simulation: $id with frequency: $frequencyInMillis ms")

        updateJobs[id] = updateScope.launch {
            try {
                sendDataToApi(simulation)
                updateSimulationStatus(id, "Initial update sent")

                while (isActive) {
                    delay(frequencyInMillis)
                    sendDataToApi(simulation)
                    updateSimulationStatus(id, "Updated at ${System.currentTimeMillis()}")
                }
            } catch (e: CancellationException) {
                Log.d("SimulationsViewModel", "Updates canceled for simulation: $id")
                updateSimulationStatus(id, "Canceled")
            } catch (e: Exception) {
                Log.e("SimulationsViewModel", "Error during updates for simulation $id: ${e.message}")
                updateSimulationStatus(id, "Error: ${e.message}")
            }
        }
    }

    fun stopSimulationUpdates(id: String) {
        if (updateJobs[id]?.isActive == true) {
            Log.d("SimulationsViewModel", "Stopping updates for simulation: $id")
            updateJobs[id]?.cancel()
        } else {
            Log.w("SimulationsViewModel", "No active updates to stop for simulation: $id")
        }
        updateJobs.remove(id)
        updateSimulationStatus(id, "Stopped")
    }

    private suspend fun sendDataToApi(simulation: Any) {
        withContext(Dispatchers.IO) {
            try {
                when (simulation) {
                    is AirQualitySimulation -> {
                        WebScraper.sendQualityData(simulation.airQuality)
                        Log.d("SimulationsViewModel", "Sent Air Quality Data: ${simulation.airQuality}")
                    }
                    is WeatherSimulation -> {
                        WebScraper.sendWeatherData(simulation.weather)
                        Log.d("SimulationsViewModel", "Sent Weather Data: ${simulation.weather}")
                    }
                    else -> Log.e("SimulationsViewModel", "Unknown simulation type: ${simulation::class.simpleName}")
                }
            } catch (e: Exception) {
                Log.e("SimulationsViewModel", "Error sending data for simulation: ${simulation::class.simpleName}, error: ${e.message}")
            }
        }
    }

    fun isSimulationRunning(id: String): Boolean {
        return updateJobs[id]?.isActive == true
    }

    private fun updateSimulationStatus(id: String, status: String) {
        simulationStatuses[id] = status
        _simulationUpdates.postValue(simulationStatuses.toMap())
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SimulationsViewModel", "ViewModel cleared, canceling all update jobs")
        updateScope.cancel()
    }
}
