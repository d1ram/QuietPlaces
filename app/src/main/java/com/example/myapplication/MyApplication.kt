package com.example.myapplication

import android.app.Application
import android.content.Context
import java.io.File
import java.util.UUID
import com.example.myapplication.PlaceSerializable
import com.example.myapplication.MyAddressSerializable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    lateinit var QuietPlaces : MutableList<Place>
        private set
    var appUuid: String = ""
        private set

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val dataFile by lazy { File(filesDir, "accounts.json") }

    override fun onCreate() {
        super.onCreate()
        instance = this
        QuietPlaces = mutableListOf()

//        clearFromJson()
        loadAppUuid()
        loadAccountsFromJson()
    }

    // UUID приложения
    private fun loadAppUuid() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appUuid = prefs.getString("app_uuid", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("app_uuid", it).apply()
        }
    }

    private fun savePlacesToJson(){
        var listToSave = QuietPlaces.map { ple ->
            PlaceSerializable(
                Address = MyAddressSerializable (
                    lat = ple.Address.lat,
                    lng = ple.Address.lng
                ),
                Name = ple.Name,
                Description = ple.Description
            )
        }
        dataFile.writeText(json.encodeToString(listToSave))
    }

    // ──────────────────────  загрузка  ──────────────────────
    private fun loadAccountsFromJson() {
        if (!dataFile.exists()) return

        try {
            val list = json.decodeFromString<List<PlaceSerializable>>(dataFile.readText())

            QuietPlaces.clear()

            list.forEach { dto ->
                val place = Place(
                    Address = MyAddress(dto.Address.lat, dto.Address.lng),
                    Name = dto.Name,
                    Description = dto.Description
                )
                QuietPlaces.add(place)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearFromJson(){
        if (dataFile.exists()){
            dataFile.delete()
        }
    }

    fun AddPlaceToList(address : MyAddress, name : String, description : String){
        var place = Place(address, name, description);
        QuietPlaces.add(place);
        savePlacesToJson()
    }
}