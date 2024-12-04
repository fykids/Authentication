package com.yosua.authentication.view.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    // Inisialisasi ViewModel menggunakan ViewModelFactory
    private val mapsViewModel : MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Menggunakan MapsViewModel untuk mengambil data lokasi
        mapsViewModel.storiesWithLocation.observe(this) { result ->
            // Handle result (Loading, Success, or Error)
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    // Update UI dengan data hasil sukses
                    result.data.listStory.forEach {
                        // Tambahkan marker untuk setiap story
                        it?.lat?.let { lat ->
                            it.lon?.let { lon ->
                                val location = LatLng(lat, lon)
                                mMap.addMarker(MarkerOptions().position(location).title(it.name))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                            }
                        }
                    }
                }

                is Result.Error -> {
                    // Tampilkan error message
                }
            }
        }

        // Memanggil fungsi untuk mendapatkan stories berdasarkan lokasi
        mapsViewModel.getStoriesWithLocation(1)
    }

    override fun onMapReady(googleMap : GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true

        // Contoh marker awal (di Sydney)
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener { latLng ->
            // Menambahkan marker di lokasi yang diklik
            mMap.addMarker(MarkerOptions().position(latLng).title("Marker at ${latLng.latitude}, ${latLng.longitude}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }
}
