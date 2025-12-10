package com.cs407.badgermate.data.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val grade: String = "",
    val major: String = "",
    val profileImage: String? = null,
    val heightFeet: String = "",
    val heightInches: String = "",
    val weight: String = "",
    val gender: String = "",
    val targetWeight: String = ""
)