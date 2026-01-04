@file:OptIn(
    kotlinx.serialization.ExperimentalSerializationApi::class,
    kotlinx.serialization.InternalSerializationApi::class
)

package com.example.myapplication

import kotlinx.serialization.Serializable

@Serializable
data class MyAddressSerializable(
    val lat : Double,
    val lng : Double
)


@Serializable
data class PlaceSerializable(
    val id: String = java.util.UUID.randomUUID().toString(),
    val Address : MyAddressSerializable,
    val Name : String,
    val Description : String,
    var imagePath: String? = null
)