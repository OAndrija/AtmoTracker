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
import com.example.atmotracker.databinding.FragmentInputAirQualityBinding
import com.example.atmotracker.model.AirQuality
import com.example.atmotracker.model.AirQualitySimulation


class InputAirQualityFragment : Fragment() {

    private var _binding: FragmentInputAirQualityBinding? = null
    private val binding get() = _binding!!
    private var selectedMarkerName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputAirQualityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureNumberPicker(binding.AirQualitySimNumberPickerHours, 23)
        configureNumberPicker(binding.AirQualitySimNumberPickerMinutes, 59)
        configureNumberPicker(binding.AirQualitySimNumberPickerSeconds, 59)

        parentFragmentManager.setFragmentResultListener("markerSelectionKey", this) { _, bundle ->
            selectedMarkerName = bundle.getString("selectedMarkerName")
            Toast.makeText(requireContext(), "Location selected: $selectedMarkerName", Toast.LENGTH_SHORT).show()
        }

        binding.chooseLocationButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AirQualityMapFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.addWeatherButton.setOnClickListener {
            try {
                val pm10StartStr = binding.airQualityPM10InputStart.text.toString()
                val pm10EndStr = binding.airQualityPM10InputEnd.text.toString()
                val pm25StartStr = binding.airQualityPM25InputStart.text.toString()
                val pm25EndStr = binding.airQualityPM25InputEnd.text.toString()
                val ozonStartStr = binding.airQualityOzonInputStart.text.toString()
                val ozonEndStr = binding.airQualityOzonInputEnd.text.toString()
                val no2StartStr = binding.airQualityNO2InputStart.text.toString()
                val no2EndStr = binding.airQualityNO2InputEnd.text.toString()

                if (pm10StartStr.isBlank() || pm10EndStr.isBlank() ||
                    pm25StartStr.isBlank() || pm25EndStr.isBlank() ||
                    ozonStartStr.isBlank() || ozonEndStr.isBlank() ||
                    no2StartStr.isBlank() || no2EndStr.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val pm10Start = pm10StartStr.toInt()
                val pm10End = pm10EndStr.toInt()
                val pm25Start = pm25StartStr.toInt()
                val pm25End = pm25EndStr.toInt()
                val ozonStart = ozonStartStr.toInt()
                val ozonEnd = ozonEndStr.toInt()
                val no2Start = no2StartStr.toInt()
                val no2End = no2EndStr.toInt()

                if (pm10Start > pm10End || pm25Start > pm25End ||
                    ozonStart > ozonEnd || no2Start > no2End) {
                    Toast.makeText(requireContext(), "Invalid range: Start values must be <= End values", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedMarkerName == null) {
                    Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val hours = binding.AirQualitySimNumberPickerHours.value
                val minutes = binding.AirQualitySimNumberPickerMinutes.value
                val seconds = binding.AirQualitySimNumberPickerSeconds.value
                val frequencyUpdate = (hours * 3600 + minutes * 60 + seconds).toLong()

                if (frequencyUpdate <= 0) {
                    Toast.makeText(requireContext(), "Update frequency must be greater than 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val randomData = mapOf(
                    "pm10" to (pm10Start..pm10End).random().toString(),
                    "pm25" to (pm25Start..pm25End).random().toString(),
                    "ozon" to (ozonStart..ozonEnd).random().toString(),
                    "no2" to (no2Start..no2End).random().toString()
                )

                val airQuality = AirQuality(
                    name = "AirQuality $selectedMarkerName",
                    data = randomData
                )

                val airQualitySimulation = AirQualitySimulation(
                    name = "Air Quality",
                    frequencyUpdate = frequencyUpdate,
                    location = "$selectedMarkerName",
                    airQuality = airQuality
                )

                val app = requireActivity().application as MyApplication
                app.airQualitySimulations.add(airQualitySimulation)
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