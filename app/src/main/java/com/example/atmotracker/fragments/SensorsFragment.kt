package com.example.atmotracker.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.atmotracker.MY_SP_FILE_NAME
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.databinding.FragmentSensorsBinding
import com.example.atmotracker.scraper.WebScraper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SensorsFragment : Fragment() {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorsViewModel: SensorsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorsViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SensorsViewModel::class.java)

        observeViewModel()

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        parentFragmentManager.setFragmentResultListener("settings_result", this) { _, result ->
            val valuesChanged = result.getBoolean("values_changed", false)
            if (valuesChanged) {
                Handler(Looper.getMainLooper()).postDelayed({
                    sensorsViewModel.loadSavedTimes()

                    binding.weatherProgressbar.progressMax = sensorsViewModel.weatherSavedTime.value?.toFloat() ?: 0f
                    binding.airQualityProgressbar.progressMax = sensorsViewModel.airQualitySavedTime.value?.toFloat() ?: 0f

                    sensorsViewModel.resetStartTimes()
                }, 300)
            }
        }
    }

    private fun observeViewModel() {
        sensorsViewModel.weatherElapsedTime.observe(viewLifecycleOwner) { elapsedTime ->
            binding.weatherTimeElapsedText.text = formatElapsedTime(elapsedTime)
            binding.weatherProgressbar.setProgressWithAnimation(elapsedTime.toFloat())
        }

        sensorsViewModel.airQualityElapsedTime.observe(viewLifecycleOwner) { elapsedTime ->
            binding.airQualityTimeElapsedText.text = formatElapsedTime(elapsedTime)
            binding.airQualityProgressbar.setProgressWithAnimation(elapsedTime.toFloat())
        }

        sensorsViewModel.weatherSavedTime.observe(viewLifecycleOwner) { savedTime ->
            binding.weatherProgressbar.progressMax = savedTime.toFloat()
            binding.weatherUpdateTimeText.text = formatElapsedTime(savedTime)
        }

        sensorsViewModel.airQualitySavedTime.observe(viewLifecycleOwner) { savedTime ->
            binding.airQualityProgressbar.progressMax = savedTime.toFloat()
            binding.airQualityUpdateTimeText.text = formatElapsedTime(savedTime)
        }
    }

    private fun formatElapsedTime(elapsedTimeMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
