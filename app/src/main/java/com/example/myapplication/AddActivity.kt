package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAddBinding
import java.io.File
import java.io.FileOutputStream

class AddActivity : AppCompatActivity() {

    private lateinit var pickMapLauncher: ActivityResultLauncher<Intent>

    private val app get() = MyApplication.instance;

    private var address: MyAddress? = null

    private lateinit var binding: ActivityAddBinding

    private var selectedImageUri: Uri? = null

    var currentPhotoPath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickMapLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val lat = data?.getDoubleExtra("lat", 0.0) ?: 0.0
                val lng = data?.getDoubleExtra("lng", 0.0) ?: 0.0
                val addressString = data?.getStringExtra("address") ?: "X"

                address = MyAddress(lat, lng);

                // Добавляем в поле адреса
                binding.tInputAddressT.setText(addressString)

                // Можно добавить в список меток, если нужно
//                Log.d("AddActivity", "Picked: $lat, $lng ->  $address")
            }
        }
        setOnClickListeners()
    }

    private fun validateInput(): Boolean {
        val name = binding.tInputNameT.text.toString()
        val description = binding.tInputDescriptionT.text.toString()

        return try {
            if (name.isBlank() || description.isBlank() || address == null) {
                Toast.makeText(this, "Please fill name and description, or pick location", Toast.LENGTH_SHORT).show()
                false
            } else true
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(this, "Please pick a location", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun setOnClickListeners(){
        binding.btnPick.setOnClickListener {
            val intent = Intent(this, PickOnMapActivity::class.java)
            pickMapLauncher.launch(intent)
        }
        binding.btnConfirm.setOnClickListener {
            if (validateInput()){
                app.AddPlaceToList(address!!, binding.tInputNameT.text.toString(), binding.tInputDescriptionT.text.toString(), currentPhotoPath.toString())
                finish()
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnChooseImage.setOnClickListener {
            // This tells Android to open anything that handles "image/*" files
            pickImageLauncher.launch("image/*")
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            // Создаем уникальное имя файла
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(this.filesDir, fileName)

            // Открываем потоки для копирования
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Возвращаем абсолютный путь к файлу
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}