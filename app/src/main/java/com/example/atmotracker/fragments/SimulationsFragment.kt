package com.example.atmotracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.adapters.GeneralAdapter
import com.example.atmotracker.adapters.Item
import com.example.atmotracker.databinding.FragmentSimulationsBinding
import com.example.atmotracker.model.AirQualitySimulation
import com.example.atmotracker.model.WeatherSimulation
import com.example.atmotracker.scraper.WebScraper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class SimulationsFragment : Fragment() {

    private var _binding: FragmentSimulationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var generalAdapter: GeneralAdapter
    private val items: MutableList<Item> = mutableListOf()

    // A shared CoroutineScope for updates
    private val updateScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val updateJobs: MutableMap<String, Job> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as MyApplication

        items.clear()
        items.addAll(app.airQualitySimulations.map { Item.AirQualitySimulationItem(it) })
        items.addAll(app.weatherSimulations.map { Item.WeatherSimulationItem(it) })

        items.sortByDescending {
            when (it) {
                is Item.AirQualitySimulationItem -> it.airQuality.airQuality.timestamp
                is Item.WeatherSimulationItem -> it.weather.weather.timestamp
            }
        }

        // Initialize adapter
        generalAdapter = GeneralAdapter(items) { removedItem ->
            when (removedItem) {
                is Item.AirQualitySimulationItem -> {
                    app.airQualitySimulations.remove(removedItem.airQuality)
                    stopSimulationUpdates(removedItem.airQuality.id)
                }
                is Item.WeatherSimulationItem -> {
                    app.weatherSimulations.remove(removedItem.weather)
                    stopSimulationUpdates(removedItem.weather.id)
                }
            }
            app.saveSimulations()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = generalAdapter
        }

        app.airQualitySimulations.forEach { startSimulationUpdates(it) }
        app.weatherSimulations.forEach { startSimulationUpdates(it) }

        // Button animation
        val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        binding.addButton.startAnimation(slideIn)

        binding.addButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.add_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_weather -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                            )
                            .replace(R.id.fragment_container, InputWeatherFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    R.id.action_air_quality -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                            )
                            .replace(R.id.fragment_container, InputAirQualityFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun startSimulationUpdates(simulation: Any) {
        val id = when (simulation) {
            is AirQualitySimulation -> simulation.id
            is WeatherSimulation -> simulation.id
            else -> return
        }

        val frequencyInSeconds = when (simulation) {
            is AirQualitySimulation -> simulation.frequencyUpdate
            is WeatherSimulation -> simulation.frequencyUpdate
            else -> return
        }

        val frequencyInMillis = frequencyInSeconds * 1000L

        updateJobs[id]?.cancel()
        Log.d("SimulationsFragment", "Starting updates for simulation: $id")

        updateJobs[id] = updateScope.launch {
            try {
                // Send data immediately
                sendDataToApi(simulation)
                Log.d("SimulationsFragment", "Initial data sent for simulation: $id")

                // Subsequent updates
                while (isActive) {
                    delay(frequencyInMillis)
                    Log.d("SimulationsFragment", "Running update for simulation: $id")
                    sendDataToApi(simulation)
                }
            } catch (e: CancellationException) {
                Log.d("SimulationsFragment", "Updates canceled for simulation: $id")
            } catch (e: Exception) {
                Log.e("SimulationsFragment", "Error during updates for simulation $id: ${e.message}")
            }
        }
    }

    private fun stopSimulationUpdates(id: String) {
        if (updateJobs.containsKey(id)) {
            Log.d("SimulationsFragment", "Stopping updates for simulation: $id")
            updateJobs[id]?.cancel()
            updateJobs.remove(id)
        } else {
            Log.w("SimulationsFragment", "No updates found to stop for simulation: $id")
        }
    }

    private suspend fun sendDataToApi(simulation: Any) {
        withContext(Dispatchers.IO) {
            try {
                when (simulation) {
                    is AirQualitySimulation -> {
                        WebScraper.sendQualityData(simulation.airQuality)
                        Log.d("SimulationsFragment", "Sent Air Quality Data: ${simulation.airQuality}")
                    }

                    is WeatherSimulation -> {
                        WebScraper.sendWeatherData(simulation.weather)
                        Log.d("SimulationsFragment", "Sent Weather Data: ${simulation.weather}")
                    }

                    else -> {
                        Log.e("SimulationsFragment", "Unknown simulation type: ${simulation::class.java.simpleName}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SimulationsFragment", "Error sending data for simulation ${simulation::class.java.simpleName}: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        updateScope.cancel()
    }
}
