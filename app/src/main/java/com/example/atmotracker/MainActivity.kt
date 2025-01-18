package com.example.atmotracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.atmotracker.databinding.ActivityMainBinding
import com.example.atmotracker.fragments.EventFragment
import com.example.atmotracker.fragments.SensorsFragment
import com.example.atmotracker.fragments.SimulationsFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNav.add(
            CurvedBottomNavigation.Model(1, "Events", R.drawable.ic_broadcast)
        )
        binding.bottomNav.add(
            CurvedBottomNavigation.Model(2, "Sensors", R.drawable.ic_sensor)
        )
        binding.bottomNav.add(
            CurvedBottomNavigation.Model(3, "Simulations", R.drawable.ic_data)
        )


        binding.bottomNav.setOnClickMenuListener {
            when(it.id) {
                1 -> {
                    replaceFragment(EventFragment())
                }
                2 -> {
                    replaceFragment(SensorsFragment())
                }
                3 -> {
                    replaceFragment(SimulationsFragment())
                }
            }
        }

        replaceFragment(SensorsFragment())
        binding.bottomNav.show(2)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}