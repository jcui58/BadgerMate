package com.cs407.badgermate.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImage: String? = null
)
