package com.cs407.badgermate.ui.home

data class Course(
    val id: String = "",
    val name: String = "",
    val daysOfWeek: List<Int> = emptyList(), // 0=Monday, 1=Tuesday, ..., 6=Sunday
    val startTime: String = "",
    val endTime: String = ""
)

data class CourseUiState(
    val courses: List<Course> = emptyList()
)
