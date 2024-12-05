package com.yosua.authentication.view.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
                    // Tampilkan loading indicator jika ada
                }
                is Result.Success -> {
                    result.data.listStory.forEach {
                        it?.lat?.let { lat ->
                            it.lon?.let { lon ->
                                val location = LatLng(lat, lon)
                                mMap.addMarker(
                                    MarkerOptions().position(location).title(it.name)
                                )
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(location, 10f)
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    // Tampilkan Toast dan Log error
                    val errorMessage = result.error
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("MapsActivity", "Error fetching data: $errorMessage")
                }
            }
        }

        mapsViewModel.getStoriesWithLocation(1)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        checkLocationPermissionAndGetLocation()
    }

    private fun checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                // Handle permission denied case
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                Log.e("MapsActivity", "Location permission denied")
            }
        }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Your Location")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }.addOnFailureListener { exception ->
            // Tampilkan Toast dan Log jika terjadi error saat mendapatkan lokasi
            Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("MapsActivity", "Error fetching current location", exception)
        }
    }
}
