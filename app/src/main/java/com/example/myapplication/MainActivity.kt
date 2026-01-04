package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val app get() = MyApplication.instance;

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

        setOnClickListeners()
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

        UpdateMarkers()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onResume() {    super.onResume()
        // Проверяем, инициализирована ли карта, прежде чем обновлять маркеры
        if (::googleMap.isInitialized) {
            googleMap.clear() // Очищаем старые маркеры, чтобы они не дублировались
            UpdateMarkers()
        }
    }

    private fun setOnClickListeners(){
        binding.BottomPanelBtnAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
        binding.BottomPanelBtnList.setOnClickListener {
            val intent = Intent(this, PlacesShowActivity::class.java)
            startActivity(intent)
        }
    }

    private fun UpdateMarkers(){
        googleMap.clear()
        val customIcon = bitmapDescriptorFromVector(this, R.drawable.map_marker)

        app.QuietPlaces.map { place ->
            addMarkerAt(googleMap, place, customIcon!!)
        }
    }
    fun addMarkerAt(googleMap: GoogleMap, place : Place, icon : BitmapDescriptor) {
        val position = LatLng(place.Address.lat, place.Address.lng) // координаты
        val markerOptions = MarkerOptions()
            .position(position)
            .icon(icon)

        val marker = googleMap.addMarker(markerOptions)
        marker?.tag = place
    }
}