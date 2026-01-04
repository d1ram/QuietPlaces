package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import androidx.core.graphics.createBitmap

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val app get() = MyApplication.instance;

    private lateinit var binding: ActivityMainBinding

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
        this.googleMap = googleMap // Мы сохраняем её как googleMap

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark)
            )
            if (!success) Log.e("MapStyle", "Style parsing failed.")
        } catch (e: Resources.NotFoundException) {
            Log.e("MapStyle", "Can't find style.", e)
        }

        val pavlodar = LatLng(52.2870, 76.9654)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pavlodar, 12f))

        googleMap.setOnMarkerClickListener { marker ->
            val place = marker.tag as? Place

            if (place != null) {
                showMarkerPreview(place)
            }
            true
        }

        googleMap.setOnMapClickListener {
            binding.markerPreviewCard.visibility = View.GONE
        }

        UpdateMarkers()
    }

    private fun showMarkerPreview(place: Place) {
        binding.markerPreviewCard.visibility = View.VISIBLE
        binding.previewName.text = place.Name

        // Загрузка картинки
        if (!place.imagePath.isNullOrEmpty()) {
//            val bitmap = BitmapFactory.decodeFile(place.imagePath)
//            binding.previewImage.setImageBitmap(bitmap)
            binding.previewImage.setImageResource(R.drawable.map_marker_dark)
        } else {
            // Если картинки нет — ставим заглушку
            binding.previewImage.setImageResource(R.drawable.map_marker_dark)
        }

        // Кнопка для перехода в детали
        binding.btnOpenDetail.setOnClickListener {
            val intent = Intent(this, PlaceCardActivity::class.java)
            intent.putExtra("PLACE_ID", place.id)
            startActivity(intent)
        }
    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
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
        binding.btnClosePreview.setOnClickListener {
            binding.markerPreviewCard.visibility = View.GONE
        }
    }

    fun addMarkerAt(googleMap: GoogleMap, place : Place, icon : BitmapDescriptor) {
        val position = LatLng(place.Address.lat, place.Address.lng)
        val markerOptions = MarkerOptions()
            .position(position)
            .icon(icon)

        val marker = googleMap.addMarker(markerOptions)
        // Сохраняем весь объект Place, чтобы в клике не искать его снова по ID
        marker?.tag = place
    }
    private fun UpdateMarkers(){
        googleMap.clear()
        val customIcon = bitmapDescriptorFromVector(this, R.drawable.map_marker_light)

        app.QuietPlaces.map { place ->
            addMarkerAt(googleMap, place, customIcon!!)
        }
    }
}