package com.example.myapplication

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString // ВАЖНО добавить этот импорт
import java.io.FileOutputStream


@Database(entities = [PlaceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}

class MyApplication : Application() {

    val db by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "quiet_places_db")
            .allowMainThreadQueries()
            .build()
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }

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
        loadAccountsFromDB()
    }

    private fun loadAppUuid() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appUuid = prefs.getString("app_uuid", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("app_uuid", it).apply()
        }
    }

    private fun copyFileToInternalStorage(uri: Uri): String? {
        return try {
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun savePlacesToDB() {
        QuietPlaces.forEach { place ->
            val entity = PlaceEntity(
                id = place.id,
                name = place.Name,
                description = place.Description,
                lat = place.Address.lat,
                lng = place.Address.lng,
                imagePath = place.imagePath
            )
            db.placeDao().insert(entity)
        }
    }

    private fun loadAccountsFromDB() {
        try {
            val entities = db.placeDao().getAll()

            QuietPlaces.clear()

            entities.forEach { entity ->
                val place = Place(
                    id = entity.id,
                    Address = MyAddress(entity.lat, entity.lng),
                    Name = entity.name,
                    Description = entity.description,
                    imagePath = entity.imagePath
                )
                QuietPlaces.add(place)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun AddPlaceToList(address: MyAddress, name: String, description: String, imageUri: Uri? = null) {
        val permanentPath = imageUri?.let { copyFileToInternalStorage(it) }

        val id = UUID.randomUUID().toString()
        val place = Place(id, address, name, description, permanentPath)

        QuietPlaces.add(place)

        val entity = PlaceEntity(
            id = place.id,
            name = place.Name,
            description = place.Description,
            lat = place.Address.lat,
            lng = place.Address.lng,
            imagePath = place.imagePath // Тут уже лежит путь к нашей копии
        )
        db.placeDao().insert(entity)
    }
}
