@file:OptIn(
    kotlinx.serialization.ExperimentalSerializationApi::class,
    kotlinx.serialization.InternalSerializationApi::class
)

package com.example.myapplication

import kotlinx.serialization.Serializable

@Serializable
data class MyAddress(
    var lat : Double,
    var lng : Double,
)

@Serializable
data class Place(
    val id: String,
    var Address : MyAddress,
    var Name : String,
    var Description : String,
    var imagePath: String? = null
)