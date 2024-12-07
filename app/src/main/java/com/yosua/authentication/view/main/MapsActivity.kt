package com.yosua.authentication.view.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yosua.authentication.R
import com.yosua.authentication.databinding.ActivityMapsBinding
import com.yosua.authentication.model.Result
import com.yosua.authentication.view.ViewModelFactory
import com.yosua.authentication.viewmodel.MapsViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    private lateinit var binding : ActivityMapsBinding
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    private val mapsViewModel : MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel.storiesWithLocation.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    result.data.listStory.filterNotNull().forEach { story ->
                        story.lat?.let { lat ->
                            story.lon?.let { lon ->
                                val location = LatLng(lat, lon)
                                mMap.addMarker(
                                    MarkerOptions().position(location).title(story.name)
                                )
                            }
                        }
                    }
                    result.data.listStory.firstOrNull()?.let { firstStory ->
                        firstStory.lat?.let { lat ->
                            firstStory.lon?.let { lon ->
                                val firstLocation = LatLng(lat, lon)
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        firstLocation,
                                        10f
                                    )
                                )
                            }
                        }
                    }
                }

                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    Log.e("MapsActivity", "Error fetching data: ${result.error}")
                }
            }
        }

        mapsViewModel.getStoriesWithLocation(1)
    }

    override fun onMapReady(googleMap : GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}
