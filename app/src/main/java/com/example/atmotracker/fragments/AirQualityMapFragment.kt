package com.example.atmotracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.atmotracker.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException


class AirQualityMapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var chooseMarkedAirQualityButton: Button
    private lateinit var hintAirQualityText: TextView
    private var selectedMarker: Marker? = null

    companion object {
        private const val TAG = "AirQualityMapFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osm_prefs", 0)
        )
        return inflater.inflate(R.layout.fragment_air_quality_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.osm_map_airquality)
        chooseMarkedAirQualityButton = view.findViewById(R.id.chooseMarkedAirQualityButton)
        hintAirQualityText = view.findViewById(R.id.hintAirQualityText)
        chooseMarkedAirQualityButton.visibility = View.GONE

        mapView.setMultiTouchControls(true)
        val mapController: IMapController = mapView.controller
        mapController.setZoom(8.5)
        mapController.setCenter(GeoPoint(46.056946, 14.505751))

        fetchWeatherData()

        chooseMarkedAirQualityButton.setOnClickListener {
            selectedMarker?.let {
                val result = Bundle().apply {
                    putString("selectedMarkerName", it.title)
                }
                parentFragmentManager.setFragmentResult("markerSelectionKey", result)
                parentFragmentManager.popBackStack()
            } ?: run {
                Toast.makeText(requireContext(), "No marker selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://192.168.1.162:3002/dataSeries/airquality")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    Log.d(TAG, "JSON Response: $json")

                    val dataSeriesList = parseAirQualityData(json)

                    activity?.runOnUiThread {
                        addMarkersToMap(dataSeriesList)
                    }
                } else {
                    Log.e(TAG, "Error fetching data: ${response.code}")
                    activity?.runOnUiThread {
                        Snackbar.make(requireView(), "Error fetching data", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Request failed: ${e.message}", e)
                activity?.runOnUiThread {
                    Snackbar.make(requireView(), "Request failed: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun parseAirQualityData(json: String?): List<LocationData> {
        val dataSeriesList = mutableListOf<LocationData>()

        if (!json.isNullOrEmpty()) {
            try {
                val gson = Gson()
                val listType = object : TypeToken<List<DataSeries>>() {}.type
                val dataSeries: List<DataSeries> = gson.fromJson(json, listType)

                for (item in dataSeries) {
                    val latitude = item.location.latitude
                    val longitude = item.location.longitude
                    val name = item.tags.flatten().getOrNull(1) ?: "Unnamed Location"

                    dataSeriesList.add(LocationData(latitude, longitude, name))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing data: ${e.message}", e)
                activity?.runOnUiThread {
                    Snackbar.make(requireView(), "Error parsing data: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            Log.w(TAG, "Received empty or null JSON")
        }

        return dataSeriesList
    }

    private fun addMarkersToMap(dataSeriesList: List<LocationData>) {
        for (data in dataSeriesList) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(data.latitude, data.longitude)
            marker.title = data.name
            marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)

            marker.setOnMarkerClickListener { clickedMarker, mapView ->
                selectedMarker?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)
                selectedMarker = clickedMarker

                clickedMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_selected_marker)
                mapView.controller.animateTo(clickedMarker.position)

                clickedMarker.showInfoWindow()

                showButtonWithAnimation()
                hideHintWithAnimation()

                true
            }

            mapView.overlays.add(marker)
        }

        mapView.zoomToBoundingBox(mapView.boundingBox, true)
    }

    private fun showButtonWithAnimation() {
        if (chooseMarkedAirQualityButton.visibility == View.GONE) {
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 300
            chooseMarkedAirQualityButton.startAnimation(fadeIn)
            chooseMarkedAirQualityButton.visibility = View.VISIBLE
        }
    }

    private fun hideHintWithAnimation() {
        if (hintAirQualityText.visibility == View.VISIBLE) {
            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.duration = 300
            hintAirQualityText.startAnimation(fadeOut)
            hintAirQualityText.visibility = View.GONE
        }
    }

    data class LocationData(val latitude: Double, val longitude: Double, val name: String)

    data class DataSeries(
        val _id: Id,
        val name: String,
        val tags: List<List<String>>,
        val location: Location
    )

    data class Id(val `$oid`: String)
    data class Location(val latitude: Double, val longitude: Double)
}