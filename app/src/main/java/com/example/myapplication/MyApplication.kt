package com.example.myapplication

import android.app.Application
import android.content.Context
import java.io.File
import java.util.UUID

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    // UUID приложения на устройстве

    lateinit var QuietPlaces : MutableList<Place>
        private set
    var appUuid: String = ""
        private set

//    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val dataFile by lazy { File(filesDir, "accounts.json") }

    override fun onCreate() {
        super.onCreate()
        instance = this
        loadAppUuid()
//        loadAccountsFromJson()
    }

    // UUID приложения
    private fun loadAppUuid() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appUuid = prefs.getString("app_uuid", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("app_uuid", it).apply()
        }
    }

    // Сохранение в JSON (через DTO)
//    private fun saveAccountsToJson() {
//        // сохраняем все аккуаанты как один тип — SerializableAccount
//        // для обычных аккаунтов percentage = 0
//        val listToSave = bank.getAccs().map { acc ->
//            SerializableAccount(
//                id = acc.ID,
//                person = SerializablePerson(acc.person.Name, acc.person.Surname),
//                balance = acc.balance,
//                percentage = if (acc is com.example.lib.SavingAccount) acc.percentage ?: 0 else 0
//            )
//        }
//        dataFile.writeText(json.encodeToString(listToSave))
//    }

    // ──────────────────────  загрузка  ──────────────────────
//    private fun loadAccountsFromJson() {
//        if (!dataFile.exists()) return
//
//        try {
//            val list = json.decodeFromString<List<SerializableAccount>>(dataFile.readText())
//
//            bank.getAccs().clear()
//
//            list.forEach { dto ->
//                val form = AccountForm(
//                    name = dto.person.name,
//                    surname = dto.person.surname,
//                    balance = dto.balance,
//                    percentage = if (dto.percentage!! > 0) dto.percentage else null   // 0 → обычный аккаунт
//                )
//                addAccountFromForm(form)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
    private fun clearFromJson(){
        if (dataFile.exists()){
            dataFile.delete()
        }
    }
}