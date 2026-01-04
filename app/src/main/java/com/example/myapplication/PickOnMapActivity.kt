package com.example.myapplication

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityPickOnMapBinding
import com.example.myapplication.databinding.ActivityPlacesBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

class PickOnMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPickOnMapBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickOnMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupClickListeners()
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

    fun setupClickListeners(){
        binding.btnConfirm.setOnClickListener {
            if (!::googleMap.isInitialized) return@setOnClickListener

            val latLng = googleMap.cameraPosition.target

            val resultIntent = Intent().apply {
                putExtra("lat", latLng.latitude)
                putExtra("lng", latLng.longitude)
                putExtra("address", "✓") // позже добавим геокодинг
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}