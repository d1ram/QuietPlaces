package com.example.myapplication


data class MyAddress(
    var lat : Double,
    var lng : Double,
)
data class Place(
    var Address : MyAddress,
    var Name : String,
    var Description : String
)