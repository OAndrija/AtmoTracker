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

        binding.chooseLocationButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                 .replace(R.id.fragment_container, MapFragment())
                 .addToBackStack(null)
                 .commit()
        }

        binding.addWeatherButton.setOnClickListener {
            try {
                val temperatureStart = binding.weatherTemperatureInputStart.text.toString().toInt()
                val temperatureEnd = binding.weatherTemperatureInputEnd.text.toString().toInt()
                val windSpeedStart = binding.weatherWindSpeedInputStart.text.toString().toInt()
                val windSpeedEnd = binding.weatherWindSpeedInputEnd.text.toString().toInt()
                val windGustsStart = binding.weatherWindGustsInputStart.text.toString().toInt()
                val windGustsEnd = binding.weatherWindGustsInputEnd.text.toString().toInt()
                val precipitationStart =
                    binding.weatherPrecipitationInputStart.text.toString().toInt()
                val precipitationEnd = binding.weatherPrecipitationInputEnd.text.toString().toInt()
                val hours = binding.WeatherSimNumberPickerHours.value
                val minutes = binding.WeatherSimNumberPickerMinutes.value
                val seconds = binding.WeatherSimNumberPickerSeconds.value
                val frequencyUpdate = (hours * 3600 + minutes * 60 + seconds).toLong()

                val randomData = mapOf(
                    "temperature" to (temperatureStart..temperatureEnd).random().toString(),
                    "precipitation" to (precipitationStart..precipitationEnd).random().toString(),
                    "windSpeed" to (windSpeedStart..windSpeedEnd).random().toString(),
                    "windGusts" to (windGustsStart..windGustsEnd).random().toString()
                )

                val weather = Weather(
                    name = "Custom Location",
                    data = randomData
                )

                val weatherSimulation = WeatherSimulation(
                    name = "Weather",
                    frequencyUpdate = frequencyUpdate,
                    location = "Custom Location",
                    weather = weather
                )

                val app = requireActivity().application as MyApplication
                app.weatherSimulations.add(weatherSimulation)
                app.saveSimulations()

                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Please enter valid ranges", Toast.LENGTH_SHORT)
                    .show()
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