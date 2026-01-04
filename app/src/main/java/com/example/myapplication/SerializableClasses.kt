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
    val Address : MyAddressSerializable,
    val Name : String,
    val Description : String
)