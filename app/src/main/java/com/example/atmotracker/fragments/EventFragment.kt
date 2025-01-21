package com.example.atmotracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.atmotracker.R

class EventFragment : Fragment() {

    private lateinit var editTextMessage: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var spinnerTopic: Spinner
    private lateinit var buttonSubmit: Button
    private lateinit var recyclerViewEvents: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }
}