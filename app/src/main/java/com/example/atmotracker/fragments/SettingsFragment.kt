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
        configureNumberPicker(binding.WeatherNumberPickerHours,23)
        configureNumberPicker(binding.WeatherNumberPickerMinutes,59)
        configureNumberPicker(binding.WeatherNumberPickerSeconds,59)

        configureNumberPicker(binding.airQualityNumberPickerHours,23)
        configureNumberPicker(binding.airQualityNumberPickerMinutes,59)
        configureNumberPicker(binding.airQualityNumberPickerSeconds,59)

        // Load saved values and set them on NumberPickers
        setSavedValues(
            sharedPreferences,
            binding.WeatherNumberPickerHours,
            binding.WeatherNumberPickerMinutes,
            binding.WeatherNumberPickerSeconds,
            "weather"
        )
        setSavedValues(
            sharedPreferences,
            binding.airQualityNumberPickerHours,
            binding.airQualityNumberPickerMinutes,
            binding.airQualityNumberPickerSeconds,
            "air_quality"
        )

        binding.closeButton.setOnClickListener {
            savePickerValues(sharedPreferences)
            view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.retract_in_top))
            parentFragmentManager.popBackStack()
        }
    }

    private fun configureNumberPicker(numberPicker: NumberPicker, max: Int) {
        numberPicker.minValue = 0
        numberPicker.maxValue = max
    }

    private fun setSavedValues(
        sharedPreferences: android.content.SharedPreferences,
        hoursPicker: NumberPicker,
        minutesPicker: NumberPicker,
        secondsPicker: NumberPicker,
        prefix: String
    ) {
        val hours = sharedPreferences.getInt("${prefix}_hours", 0)
        val minutes = sharedPreferences.getInt("${prefix}_minutes", 0)
        val seconds = sharedPreferences.getInt("${prefix}_seconds", 0)

        // Log retrieved values for debugging
        Log.d("SettingsFragment", "Loaded values for $prefix: hours=$hours, minutes=$minutes, seconds=$seconds")

        hoursPicker.value = hours
        minutesPicker.value = minutes
        secondsPicker.value = seconds
    }

    private fun savePickerValues(sharedPreferences: android.content.SharedPreferences) {
        sharedPreferences.edit()
            // Save Weather values
            .putInt("weather_hours", binding.WeatherNumberPickerHours.value)
            .putInt("weather_minutes", binding.WeatherNumberPickerMinutes.value)
            .putInt("weather_seconds", binding.WeatherNumberPickerSeconds.value)
            // Save Air Quality values
            .putInt("air_quality_hours", binding.airQualityNumberPickerHours.value)
            .putInt("air_quality_minutes", binding.airQualityNumberPickerMinutes.value)
            .putInt("air_quality_seconds", binding.airQualityNumberPickerSeconds.value)
            .apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
