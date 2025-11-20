package com.cs407.badgermate.data

data class Restaurant(
    val id: String,
    val name: String,
    val location: String,
    val cuisine: String,
    val rating: Double = 0.0,
    val description: String? = null
)
