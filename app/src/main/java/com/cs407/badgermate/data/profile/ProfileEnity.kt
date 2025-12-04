package com.cs407.badgermate.data.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

// 只存一条，所以 id 固定为 0
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val grade: String,   // 年级，比如 "Junior"
    val major: String    // 专业，比如 "Computer Science"
)
