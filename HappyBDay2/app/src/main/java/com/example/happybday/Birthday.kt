package com.example.happybday

data class Birthday(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val message: String,
    val mediaUri: String = "" //optional — fara media atasata
)