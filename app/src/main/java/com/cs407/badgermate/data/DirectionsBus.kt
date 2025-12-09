package com.cs407.badgermate.data

data class DirectionsBus(
    val id: String,
    val route: String,
    val destination: String,
    val eta: String,
    val status: String = "On time"
)
