package com.example.myapplication

import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.MapStyleOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private val pavlodarCenter = LatLng(52.2870, 76.9654) // центр Павлодара
    private val pavlodarBounds = LatLngBounds(
        LatLng(52.20, 76.85), // юго-запад
        LatLng(52.35, 77.05)  // северо-восток
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        binding.searchView.queryHint = "Искать улицу или место"

//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let { searchLocation(it) }
//                return false
//            }

//            override fun onQueryTextChange(newText: String?): Boolean = false
//        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        try {
            // Загружаем стиль из raw
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark)
            )
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapStyle", "Can't find style.", e)
        }

        // Центр на Павлодаре
        val pavlodar = LatLng(52.2870, 76.9654)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pavlodar, 12f))
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this)
        try {
            val addressList = geocoder.getFromLocationName(locationName, 5)
            val addressInCity = addressList?.firstOrNull { it.locality?.contains("Павлодар") == true }
            if (addressInCity != null) {
                val latLng = LatLng(addressInCity.latitude, addressInCity.longitude)
                googleMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                Toast.makeText(this, "Место не найдено в Павлодаре", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка поиска", Toast.LENGTH_SHORT).show()
        }
    }
}