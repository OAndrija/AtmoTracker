package com.example.atmotracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.databinding.FragmentInputBinding
import com.example.atmotracker.model.Weather
import com.example.atmotracker.model.WeatherSimulation

class InputWeatherFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!
    private var selectedMarkerName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureNumberPicker(binding.WeatherSimNumberPickerHours, 23)
        configureNumberPicker(binding.WeatherSimNumberPickerMinutes, 59)
        configureNumberPicker(binding.WeatherSimNumberPickerSeconds, 59)

        parentFragmentManager.setFragmentResultListener("markerSelectionKey", this) { _, bundle ->
            selectedMarkerName = bundle.getString("selectedMarkerName")
            Toast.makeText(requireContext(), "Location selected: $selectedMarkerName", Toast.LENGTH_SHORT).show()
        }

        binding.chooseLocationButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                 .replace(R.id.fragment_container, MapFragment())
                 .addToBackStack(null)
                 .commit()
        }

        binding.addWeatherButton.setOnClickListener {
            try {
                val temperatureStartStr = binding.weatherTemperatureInputStart.text.toString()
                val temperatureEndStr = binding.weatherTemperatureInputEnd.text.toString()
                val windSpeedStartStr = binding.weatherWindSpeedInputStart.text.toString()
                val windSpeedEndStr = binding.weatherWindSpeedInputEnd.text.toString()
                val windGustsStartStr = binding.weatherWindGustsInputStart.text.toString()
                val windGustsEndStr = binding.weatherWindGustsInputEnd.text.toString()
                val precipitationStartStr = binding.weatherPrecipitationInputStart.text.toString()
                val precipitationEndStr = binding.weatherPrecipitationInputEnd.text.toString()

                if (temperatureStartStr.isBlank() || temperatureEndStr.isBlank() ||
                    windSpeedStartStr.isBlank() || windSpeedEndStr.isBlank() ||
                    windGustsStartStr.isBlank() || windGustsEndStr.isBlank() ||
                    precipitationStartStr.isBlank() || precipitationEndStr.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val temperatureStart = temperatureStartStr.toInt()
                val temperatureEnd = temperatureEndStr.toInt()
                val windSpeedStart = windSpeedStartStr.toInt()
                val windSpeedEnd = windSpeedEndStr.toInt()
                val windGustsStart = windGustsStartStr.toInt()
                val windGustsEnd = windGustsEndStr.toInt()
                val precipitationStart = precipitationStartStr.toInt()
                val precipitationEnd = precipitationEndStr.toInt()

                if (temperatureStart > temperatureEnd || windSpeedStart > windSpeedEnd ||
                    windGustsStart > windGustsEnd || precipitationStart > precipitationEnd) {
                    Toast.makeText(requireContext(), "Invalid range: Start values must be <= End values", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedMarkerName == null) {
                    Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val hours = binding.WeatherSimNumberPickerHours.value
                val minutes = binding.WeatherSimNumberPickerMinutes.value
                val seconds = binding.WeatherSimNumberPickerSeconds.value
                val frequencyUpdate = (hours * 3600 + minutes * 60 + seconds).toLong()

                if (frequencyUpdate <= 0) {
                    Toast.makeText(requireContext(), "Update frequency must be greater than 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val randomData = mapOf(
                    "temperature" to (temperatureStart..temperatureEnd).random().toString(),
                    "windSpeed" to (windSpeedStart..windSpeedEnd).random().toString(),
                    "windGusts" to (windGustsStart..windGustsEnd).random().toString(),
                    "precipitation" to (precipitationStart..precipitationEnd).random().toString(),
                    )

                val weather = Weather(
                    name = "Weather $selectedMarkerName",
                    data = randomData
                )

                val weatherSimulation = WeatherSimulation(
                    name = "Weather",
                    frequencyUpdate = frequencyUpdate,
                    location = "$selectedMarkerName",
                    weather = weather
                )

                val app = requireActivity().application as MyApplication
                app.weatherSimulations.add(weatherSimulation)
                app.saveSimulations()

                parentFragmentManager.popBackStack()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureNumberPicker(numberPicker: NumberPicker, max: Int) {
        numberPicker.minValue = 0
        numberPicker.maxValue = max
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}