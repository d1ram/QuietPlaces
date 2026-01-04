package com.example.myapplication

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.UUID
import kotlinx.serialization.json.Json
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
        createNotificationChannel()
    }

    private fun loadAppUuid() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appUuid = prefs.getString("app_uuid", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("app_uuid", it).apply()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel_id",
                "Main Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Application notification"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
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

        sendNotification("New place appeared!")
    }

    fun sendNotification(message: String) {
        // Проверка разрешения для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Если разрешения нет, мы не можем отправить уведомление.
                // Его нужно запросить в Activity.
                return
            }
        }

        val notification = NotificationCompat.Builder(this, "my_channel_id")
            .setSmallIcon(R.drawable.map_marker_dark)
            .setContentTitle("Quiet Places")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
