package com.example.atmotracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.addWalkButton.setOnClickListener {
            val date = binding.dateInput.text.toString()
            val stepCount = binding.stepCountInput.text.toString().toIntOrNull() ?: -1
            val caloriesBurned = binding.caloriesSpentInput.text.toString().toDoubleOrNull() ?: -1.0
            val timeSpent = binding.timeSpentInput.text.toString().toLongOrNull() ?: -1L

            if (date.isNotEmpty() && stepCount > 0 && caloriesBurned > 0.0 && timeSpent > 0L) {
                val newWalk = Walk(date, stepCount, caloriesBurned, timeSpent)

                val app = requireActivity().application as MyApplication
                app.walks.add(newWalk)
                app.saveToFile()

                Toast.makeText(requireContext(), "Walk added successfully!", Toast.LENGTH_SHORT).show()
                println("walks: ${app.walks}")
                println("WALKS SIZE: ${app.walks.size}")

                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}