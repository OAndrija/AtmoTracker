package com.example.atmotracker.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.NumberPicker
import com.example.atmotracker.MY_SP_FILE_NAME
import com.example.atmotracker.R
import com.example.atmotracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top))

        val sharedPreferences = requireContext().getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)

        // Configure NumberPickers
        configureNumberPicker(binding.WeatherNumberPickerHours, 23)
        configureNumberPicker(binding.WeatherNumberPickerMinutes, 59)
        configureNumberPicker(binding.WeatherNumberPickerSeconds, 59)

        configureNumberPicker(binding.airQualityNumberPickerHours, 23)
        configureNumberPicker(binding.airQualityNumberPickerMinutes, 59)
        configureNumberPicker(binding.airQualityNumberPickerSeconds, 59)

        // Load saved values into maps
        val weatherValues = loadSavedValues(sharedPreferences, "weather")
        val airQualityValues = loadSavedValues(sharedPreferences, "air_quality")

        Log.d("SettingsFragment", "Loaded weather values: $weatherValues")
        Log.d("SettingsFragment", "Loaded air quality values: $airQualityValues")

        // Set values on NumberPickers
        setPickerValues(weatherValues, binding.WeatherNumberPickerHours, binding.WeatherNumberPickerMinutes, binding.WeatherNumberPickerSeconds)
        setPickerValues(airQualityValues, binding.airQualityNumberPickerHours, binding.airQualityNumberPickerMinutes, binding.airQualityNumberPickerSeconds)

        binding.closeButton.setOnClickListener {
            val newWeatherValues = getPickerValues(binding.WeatherNumberPickerHours, binding.WeatherNumberPickerMinutes, binding.WeatherNumberPickerSeconds)
            val newAirQualityValues = getPickerValues(binding.airQualityNumberPickerHours, binding.airQualityNumberPickerMinutes, binding.airQualityNumberPickerSeconds)

            Log.d("SettingsFragment", "New weather values: $newWeatherValues")
            Log.d("SettingsFragment", "New air quality values: $newAirQualityValues")

            val valuesChanged = newWeatherValues != weatherValues || newAirQualityValues != airQualityValues

            if (valuesChanged) {
                parentFragmentManager.setFragmentResult(
                    "settings_result",
                    Bundle().apply {
                        putBoolean("values_changed", true)
                    }
                )
            }

            savePickerValues(sharedPreferences)
            view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.retract_in_top))
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSavedValues(sharedPreferences: android.content.SharedPreferences, prefix: String): Map<String, Int> {
        return mapOf(
            "hours" to sharedPreferences.getInt("${prefix}_hours", 0),
            "minutes" to sharedPreferences.getInt("${prefix}_minutes", 0),
            "seconds" to sharedPreferences.getInt("${prefix}_seconds", 0)
        )
    }

    private fun setPickerValues(values: Map<String, Int>, hoursPicker: NumberPicker, minutesPicker: NumberPicker, secondsPicker: NumberPicker) {
        hoursPicker.value = values["hours"] ?: 0
        minutesPicker.value = values["minutes"] ?: 0
        secondsPicker.value = values["seconds"] ?: 0
    }

    private fun getPickerValues(hoursPicker: NumberPicker, minutesPicker: NumberPicker, secondsPicker: NumberPicker): Map<String, Int> {
        return mapOf(
            "hours" to hoursPicker.value,
            "minutes" to minutesPicker.value,
            "seconds" to secondsPicker.value
        )
    }

    private fun configureNumberPicker(numberPicker: NumberPicker, max: Int) {
        numberPicker.minValue = 0
        numberPicker.maxValue = max
    }

    private fun savePickerValues(sharedPreferences: android.content.SharedPreferences) {
        Log.d("SettingsFragment", "Saving weather values: Hours=${binding.WeatherNumberPickerHours.value}, Minutes=${binding.WeatherNumberPickerMinutes.value}, Seconds=${binding.WeatherNumberPickerSeconds.value}")
        Log.d("SettingsFragment", "Saving air quality values: Hours=${binding.airQualityNumberPickerHours.value}, Minutes=${binding.airQualityNumberPickerMinutes.value}, Seconds=${binding.airQualityNumberPickerSeconds.value}")

        sharedPreferences.edit()
            // Save Weather values
            .putInt("weather_hours", binding.WeatherNumberPickerHours.value)
            .putInt("weather_minutes", binding.WeatherNumberPickerMinutes.value)
            .putInt("weather_seconds", binding.WeatherNumberPickerSeconds.value)
            // Save Air Quality values
            .putInt("air_quality_hours", binding.airQualityNumberPickerHours.value)
            .putInt("air_quality_minutes", binding.airQualityNumberPickerMinutes.value)
            .putInt("air_quality_seconds", binding.airQualityNumberPickerSeconds.value)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
