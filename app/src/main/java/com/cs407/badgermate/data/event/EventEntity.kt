package com.cs407.badgermate.data.event

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val organization: String,

    val category: String,

    // 新增：用于时间计算
    val startTime: Long,
    val endTime: Long,

    // 用于 UI 展示
    val displayTime: String,

    val location: String,

    var isMyEvent: Boolean = false
)
