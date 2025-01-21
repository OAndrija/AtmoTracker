package com.example.atmotracker.fragments

import android.os.Bundle
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


class SimulationsFragment : Fragment() {
    private var _binding: FragmentSimulationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var generalAdapter: GeneralAdapter
    private val items: MutableList<Item> = mutableListOf()

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
                }
                is Item.WeatherSimulationItem -> {
                    app.weatherSimulations.remove(removedItem.weather)
                }
            }
            app.saveSimulations()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = generalAdapter
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}