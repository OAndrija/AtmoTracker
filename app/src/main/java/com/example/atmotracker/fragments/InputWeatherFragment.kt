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

        binding.addWeatherButton.setOnClickListener {
            val temperatureStart = binding.weatherTemperatureInputStart.text.toString()
            val temperatureEnd = binding.weatherTemperatureInputEnd.text.toString()
            val windSpeedStart = binding.weatherWindSpeedInputStart.text.toString()
            val windSpeedEnd = binding.weatherWindSpeedInputEnd.text.toString()
            val windGustsStart = binding.weatherWindGustsInputStart.text.toString()
            val windGustsEnd = binding.weatherWindGustsInputEnd.text.toString()
            val precipitationStart = binding.weatherPrecipitationInputStart.text.toString()
            val precipitationEnd = binding.weatherPrecipitationInputEnd.text.toString()

            parentFragmentManager.popBackStack()
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