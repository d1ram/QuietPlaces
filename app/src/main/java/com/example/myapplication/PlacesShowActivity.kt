package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPlacesBinding

class PlacesShowActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPlacesBinding
    private val app get() = MyApplication.instance;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupRecyclerView()
    }

    fun setupClickListeners(){
        binding.bnBack.setOnClickListener{
            finish()
        }
    }
    fun setupRecyclerView(){
//        val adapter = AccountsAdapter(this)
//
//        binding.rvItems.layoutManager = LinearLayoutManager(this)
//        binding.rvItems.adapter = adapter
    }
}