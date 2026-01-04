package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPlaceBinding // Убедитесь, что XML называется item_place.xml

class PlacesAdapter(
    private val context: Context
) : RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>() {

    private val app get() = MyApplication.instance

    inner class PlacesViewHolder(val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding = ItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlacesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return app.QuietPlaces.size
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = app.QuietPlaces[position]

        holder.binding.tvPlaceItemName.text = place.Name
        holder.binding.tvPlaceItemDescription.text = place.Description

        if (!place.imagePath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(place.imagePath)
            holder.binding.ivPlaceItemImage.setImageBitmap(bitmap)
        } else {
            holder.binding.ivPlaceItemImage.setImageResource(R.drawable.map_marker_light)
        }

        holder.itemView.setOnClickListener {
            Log.d("DEBUG_TAG", "CLICKED on item! ID: ${place.id}")

            try {
                val intent = Intent(context, PlaceCardActivity::class.java)
                intent.putExtra("PLACE_ID", place.id)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("DEBUG_TAG", "FAILED TO START ACTIVITY: ${e.message}")
            }
        }

    }
}
