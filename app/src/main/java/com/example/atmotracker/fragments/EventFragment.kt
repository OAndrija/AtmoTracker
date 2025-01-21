package com.example.atmotracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.atmotracker.R
import com.example.atmotracker.model.Event

class EventFragment : Fragment() {

    private lateinit var editTextMessage: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var spinnerTopic: Spinner
    private lateinit var buttonSubmit: Button
    private lateinit var recyclerViewEvents: RecyclerView

    private val eventsList = mutableListOf<Event>()
    //private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_event, container, false)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        editTextTime = view.findViewById(R.id.editTextTime)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        spinnerTopic = view.findViewById(R.id.spinnerTopic)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)

       // eventAdapter = EventAdapter(eventsList)
       // recyclerViewEvents.layoutManager = LinearLayoutManager(context)
        //recyclerViewEvents.adapter = eventAdapter

        buttonSubmit.setOnClickListener {
            postEvent()
        }

        return view
    }


    private fun postEvent(){
        val message = editTextMessage.text.toString()
        val time = editTextTime.text.toString()
        val location = editTextLocation.text.toString()
        val topic = spinnerTopic.selectedItem.toString()

        if (message.isBlank() || time.isBlank() || location.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(message, time, location, topic)
        val data = "Event: $topic | Message: $message | Time: $time | Location: $location"

        val index = 1
        val hash = "0000a1b2c3d4e5f6"
        val previousHash = "0000z9y8x7w6v5u4"
        val difficulty = 4
        Log.d("BlockchainDetails", "Index: $index, Hash: $hash, PreviousHash: $previousHash, Difficulty: $difficulty, Data: $data")
        eventsList.add(event)
    }
}