package com.example.myapplication

import android.app.Application
import android.content.Context
import java.io.File
import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString // ВАЖНО добавить этот импорт

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    // Используем обычный список, инициализируем сразу
    var QuietPlaces: MutableList<Place> = mutableListOf()
        private set

    var appUuid: String = ""
        private set

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val dataFile by lazy { File(filesDir, "accounts.json") }

    override fun onCreate() {
        super.onCreate()
        instance = this

        loadAppUuid()
        loadAccountsFromJson()
    }

    private fun loadAppUuid() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appUuid = prefs.getString("app_uuid", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("app_uuid", it).apply()
        }
    }

    // 1. ИСПРАВЛЕНО: Сохранение. Мы должны сохранять список DTO (Serializable), а не основной список
    fun savePlacesToJson() {
        val listToSave = QuietPlaces.map { place ->
            PlaceSerializable(
                id = place.id,
                Address = MyAddressSerializable(place.Address.lat, place.Address.lng),
                Name = place.Name,
                Description = place.Description,
                imagePath = place.imagePath
            )
        }
        dataFile.writeText(json.encodeToString(listToSave))
    }

    // 2. ИСПРАВЛЕНО: Загрузка. Декодируем список PlaceSerializable
    private fun loadAccountsFromJson() {
        if (!dataFile.exists()) return

        try {
            val fileContent = dataFile.readText()
            if (fileContent.isEmpty()) return

            val list = json.decodeFromString<List<PlaceSerializable>>(fileContent)

            QuietPlaces.clear()

            list.forEach { dto ->
                val place = Place(
                    id = dto.id, // Теперь ID подхватывается из файла!
                    Address = MyAddress(dto.Address.lat, dto.Address.lng),
                    Name = dto.Name,
                    Description = dto.Description,
                    imagePath = dto.imagePath
                )
                QuietPlaces.add(place)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Если файл битый - лучше его удалить или очистить
        }
    }

    fun AddPlaceToList(address: MyAddress, name: String, description: String, imagePath: String? = null) {
        val id = UUID.randomUUID().toString()
        val place = Place(id, address, name, description, imagePath)
        QuietPlaces.add(place)
        savePlacesToJson()
    }
}
