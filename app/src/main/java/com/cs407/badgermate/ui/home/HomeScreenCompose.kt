package com.cs407.badgermate.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Data class for TodoItem UI
data class TodoItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val priority: String = "Medium"
)

@Composable
fun HomeScreenCompose(
    userName: String = "",
    userEmail: String = "",
    todoItems: List<TodoItem> = emptyList(),
    courses: List<Course> = emptyList(),
    onAddTodoClick: () -> Unit = {},
    onAddCourse: (Course) -> Unit = {},
    onDeleteCourse: (String) -> Unit = {},
    onTodoToggle: (String) -> Unit = {},
    onTodoDelete: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 80.dp)
    ) {
        // Header
        item {
            Text(
                "My Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // User Info Card
        item {
            // Get greeting based on current time
            val currentHour = remember {
                java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            }
            val greeting = when (currentHour) {
                in 5..11 -> "Good Morning"
                in 12..16 -> "Good Afternoon"
                in 17..21 -> "Good Evening"
                else -> "Good Night"
            }
            
            val firstName = userName.split(" ").firstOrNull() ?: userName

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "$greeting, $firstName!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Course Schedule Section
        item {
            CourseScheduleSection(
                courses = courses,
                onAddCourse = onAddCourse,
                onDeleteCourse = onDeleteCourse
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // My Tasks Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Tasks",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onAddTodoClick,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("+ Add")
                }
            }
        }

        // Todo Items
        if (todoItems.isEmpty()) {
            item {
                Text(
                    "No tasks yet",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(
                items = todoItems,
                key = { it.id }
            ) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggle = { onTodoToggle(todo.id) },
                    onDelete = { onTodoDelete(todo.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CourseScheduleSection(
    courses: List<Course> = emptyList(),
    onAddCourse: (Course) -> Unit = {},
    onDeleteCourse: (String) -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    // Get current day and time
    val calendar = java.util.Calendar.getInstance()
    val currentDayOfWeek = (calendar.get(java.util.Calendar.DAY_OF_WEEK) - 2 + 7) % 7 // 0=Monday
    val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(java.util.Calendar.MINUTE)

    // Sort courses by closest time to current time
    val sortedCourses = courses.sortedBy { course ->
        val (courseHour, courseMinute) = course.startTime.split(":").map { it.toIntOrNull() ?: 0 }
        
        // Find next occurrence of this course
        val daysAhead = course.daysOfWeek
            .map { day ->
                val dayDiff = if (day >= currentDayOfWeek) {
                    day - currentDayOfWeek
                } else {
                    day + 7 - currentDayOfWeek
                }
                
                // If today, check if time has passed
                if (dayDiff == 0) {
                    if (courseHour > currentHour || 
                        (courseHour == currentHour && courseMinute > currentMinute)) {
                        dayDiff * 1440 + (courseHour - currentHour) * 60 + (courseMinute - currentMinute)
                    } else {
                        (dayDiff + 7) * 1440 + (courseHour - currentHour) * 60 + (courseMinute - currentMinute)
                    }
                } else {
                    dayDiff * 1440 + (courseHour - currentHour) * 60 + (courseMinute - currentMinute)
                }
            }
            .minOrNull() ?: Int.MAX_VALUE
        
        daysAhead
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Weekly Course Schedule",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.height(40.dp)
            ) {
                Text("Edit")
            }
        }

        // Display courses
        if (sortedCourses.isEmpty()) {
            Text(
                "No courses added yet",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                sortedCourses.forEach { course ->
                    CourseCard(
                        course = course,
                        currentDayOfWeek = currentDayOfWeek,
                        currentHour = currentHour,
                        currentMinute = currentMinute,
                        onDelete = { onDeleteCourse(course.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddCourseDialog(
            onDismiss = { showDialog = false },
            onAddCourse = { newCourse ->
                onAddCourse(newCourse)
                showDialog = false
            }
        )
    }
}

@Composable
fun CourseCard(
    course: Course,
    currentDayOfWeek: Int = 0,
    currentHour: Int = 0,
    currentMinute: Int = 0,
    onDelete: () -> Unit = {}
) {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val daysText = course.daysOfWeek
        .sorted()
        .joinToString(", ") { dayNames.getOrNull(it) ?: "?" }

    // Calculate time until next class
    val timeUntilClass = remember(course, currentDayOfWeek, currentHour, currentMinute) {
        val (courseHour, courseMinute) = course.startTime.split(":").map { it.toIntOrNull() ?: 0 }
        
        val nextClass = course.daysOfWeek
            .mapNotNull { day ->
                val dayDiff = if (day >= currentDayOfWeek) {
                    day - currentDayOfWeek
                } else {
                    day + 7 - currentDayOfWeek
                }
                
                if (dayDiff == 0) {
                    // Today
                    val totalMinutes = courseHour * 60 + courseMinute - (currentHour * 60 + currentMinute)
                    if (totalMinutes > 0) {
                        Triple(dayDiff, totalMinutes / 60, totalMinutes % 60)
                    } else {
                        null // Already passed today
                    }
                } else {
                    val hourDiff = 24 - currentHour + courseHour
                    val minDiff = courseMinute - currentMinute
                    if (minDiff < 0) {
                        Triple(dayDiff, hourDiff - 1, 60 + minDiff)
                    } else {
                        Triple(dayDiff, hourDiff, minDiff)
                    }
                }
            }
            .minByOrNull { it.first * 1440 + it.second * 60 + it.third }
        
        nextClass
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    course.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    daysText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "${course.startTime} - ${course.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Show time until class
                if (timeUntilClass != null) {
                    val (days, hours, minutes) = timeUntilClass
                    val timeText = when {
                        days > 0 -> "In $days day${if (days > 1) "s" else ""} at ${course.startTime}"
                        hours > 0 -> "Starts in $hours h $minutes min"
                        else -> "Starts in ${minutes} min"
                    }
                    Text(
                        timeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit = {},
    onAddCourse: (Course) -> Unit = {}
) {
    var courseName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                item {
                    Text(
                        "Add New Course",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { 
                            courseName = it
                            errorMessage = ""
                        },
                        label = { Text("Course Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        isError = errorMessage.contains("Course Name")
                    )
                }

                item {
                    Button(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text("Start Time: $startTime")
                    }
                }

                item {
                    Button(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text("End Time: $endTime")
                    }
                }

                item {
                    Text(
                        "Days of Week",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = if (errorMessage.contains("Days")) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }

                items(dayNames.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedDays.contains(index),
                            onCheckedChange = { isChecked ->
                                selectedDays = if (isChecked) {
                                    selectedDays + index
                                } else {
                                    selectedDays - index
                                }
                                errorMessage = ""
                            }
                        )
                        Text(dayNames[index], modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // Show error message if any
                if (errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                errorMessage = ""
                                
                                if (courseName.isBlank()) {
                                    errorMessage = "Please enter a course name"
                                    return@Button
                                }
                                
                                if (selectedDays.isEmpty()) {
                                    errorMessage = "Please select at least one day"
                                    return@Button
                                }
                                
                                val newCourse = Course(
                                    id = System.currentTimeMillis().toString(),
                                    name = courseName,
                                    daysOfWeek = selectedDays.toList(),
                                    startTime = startTime,
                                    endTime = endTime
                                )
                                onAddCourse(newCourse)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            initialTime = startTime,
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { time ->
                startTime = time
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            initialTime = endTime,
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { time ->
                endTime = time
                showEndTimePicker = false
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    initialTime: String = "09:00",
    onDismiss: () -> Unit = {},
    onTimeSelected: (String) -> Unit = {}
) {
    val parts = initialTime.split(":")
    var hour by remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 9) }
    var minute by remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Time", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { hour = (hour + 1) % 24 }) {
                            Text("▲")
                        }
                        Text(
                            String.format("%02d", hour),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(8.dp)
                        )
                        Button(onClick = { hour = if (hour == 0) 23 else hour - 1 }) {
                            Text("▼")
                        }
                    }

                    Text(":", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))

                    // Minute
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { minute = (minute + 1) % 60 }) {
                            Text("▲")
                        }
                        Text(
                            String.format("%02d", minute),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(8.dp)
                        )
                        Button(onClick = { minute = if (minute == 0) 59 else minute - 1 }) {
                            Text("▼")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val timeString = String.format("%02d:%02d", hour, minute)
                            onTimeSelected(timeString)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: TodoItem,
    onToggle: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Set color based on priority
    val priorityColor = when (todo.priority.lowercase()) {
        "high" -> Color(0xFFFF5252)      // Red
        "medium" -> Color(0xFFFFA726)    // Orange
        "low" -> Color(0xFF66BB6A)       // Green
        else -> Color(0xFF90CAF9)        // Blue (default)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = priorityColor.copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, priorityColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = todo.completed,
                onCheckedChange = { onToggle() },
                modifier = Modifier.padding(end = 8.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        todo.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        todo.priority,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                if (todo.description.isNotEmpty()) {
                    Text(
                        todo.description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
