package com.example.atmotracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: SimulationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SimulationsFragment", "onCreateView called")
        _binding = FragmentSimulationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SimulationsFragment", "onViewCreated called")

        viewModel = ViewModelProvider(requireActivity())[SimulationsViewModel::class.java]
        Log.d("SimulationsFragment", "ViewModel initialized")

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

        generalAdapter = GeneralAdapter(items) { removedItem ->
            when (removedItem) {
                is Item.AirQualitySimulationItem -> {
                    app.airQualitySimulations.remove(removedItem.airQuality)
                    viewModel.stopSimulationUpdates(removedItem.airQuality.id)
                }
                is Item.WeatherSimulationItem -> {
                    app.weatherSimulations.remove(removedItem.weather)
                    viewModel.stopSimulationUpdates(removedItem.weather.id)
                }
            }
            app.saveSimulations()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = generalAdapter
        }

        viewModel.simulationUpdates.observe(viewLifecycleOwner) { updates ->
            updates.forEach { (id, status) ->
                Log.d("SimulationsFragment", "Simulation $id: $status")
            }
        }

        app.airQualitySimulations.forEach { simulation ->
            if (!viewModel.isSimulationRunning(simulation.id)) {
                Log.d("SimulationsFragment", "Starting updates for AirQuality simulation: ${simulation.id}")
                viewModel.startSimulationUpdates(simulation)
            }
        }

        app.weatherSimulations.forEach { simulation ->
            if (!viewModel.isSimulationRunning(simulation.id)) {
                Log.d("SimulationsFragment", "Starting updates for Weather simulation: ${simulation.id}")
                viewModel.startSimulationUpdates(simulation)
            }
        }

        binding.addButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.add_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_weather -> navigateToFragment(InputWeatherFragment())
                    R.id.action_air_quality -> navigateToFragment(InputAirQualityFragment())
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SimulationsFragment", "onDestroyView called")
        _binding = null
    }
}

