package com.example.atmotracker.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atmotracker.MY_SP_FILE_NAME
import com.example.atmotracker.MyApplication
import com.example.atmotracker.R
import com.example.atmotracker.adapters.WalkAdapter
import com.example.atmotracker.databinding.FragmentSimulationsBinding
import com.example.atmotracker.model.Walk


class SimulationsFragment : Fragment() {
    private var _binding: FragmentSimulationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var walkAdapter: WalkAdapter
    private lateinit var appWalks: MutableList<Walk>

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
        appWalks = app.walks

        val sharedPreferences = requireContext().getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)

        walkAdapter = WalkAdapter(appWalks, { removedWalk ->
            app.walks.remove(removedWalk)
            app.saveToFile()
            walkAdapter.notifyDataSetChanged()
        }, sharedPreferences)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = walkAdapter
        }

        val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        binding.addButton.startAnimation(slideIn)

        binding.addButton.setOnClickListener {
            val slideLeft = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
            binding.addButton.startAnimation(slideLeft)

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, InputFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}