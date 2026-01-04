package com.example.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPlaceCardBinding

class PlaceCardActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPlaceCardBinding
    private val app get() = MyApplication.instance;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeId = intent.getStringExtra("PLACE_ID")
        Log.d("DEBUG_TAG", "ID received in Activity: $placeId")

        if (placeId != null) {
            // Проверяем, сколько вообще элементов в списке
            Log.d("DEBUG_TAG", "Total places in app: ${app.QuietPlaces.size}")

            val place = app.QuietPlaces.find { it.id == placeId }

            if (place != null) {
                binding.tvNameRes.text = place.Name
                binding.tvLatRes.text = place.Address.lat.toString()
                binding.tvLngRes.text = place.Address.lng.toString()
                binding.tvDescRes.text = place.Description

                // Если есть картинка - грузим (добавь эту проверку!)
                if (!place.imagePath.isNullOrEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(place.imagePath)
                    binding.iv.setImageBitmap(bitmap)
                }

                setupClickListeners() // Вызываем ТОЛЬКО если место найдено
            } else {
                // Если мы тут, значит ID есть, но в списке QuietPlaces такого объекта НЕТ
                Log.e("DEBUG_TAG", "Place NOT FOUND in list for ID: $placeId")
                Toast.makeText(this, "Place not found in database", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.e("DEBUG_TAG", "Intent extra PLACE_ID is NULL")
            finish()
        }
    }


    fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}