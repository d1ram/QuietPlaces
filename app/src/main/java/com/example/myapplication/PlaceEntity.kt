package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val imagePath: String?
)

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    fun getAll(): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(place: PlaceEntity)

    @Delete
    fun delete(place: PlaceEntity)
}