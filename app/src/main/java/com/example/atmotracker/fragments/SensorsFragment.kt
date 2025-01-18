package com.example.atmotracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.databinding.FragmentSensorsBinding

class SensorsFragment : Fragment() {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var app: MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}