package com.cs407.badgermate.data

import java.util.Date

data class TodoItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val dueDate: Date? = null,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
) {
    enum class Priority {
        LOW, MEDIUM, HIGH
    }
}
