package com.cs407.badgermate.data

import java.util.Date

data class Event(
    val id: String,
    val title: String,
    val description: String? = null,
    val startTime: Date,
    val endTime: Date,
    val location: String? = null
)
