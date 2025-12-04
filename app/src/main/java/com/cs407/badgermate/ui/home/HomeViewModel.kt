package com.cs407.badgermate.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.badgermate.data.User
import com.cs407.badgermate.data.TodoItem
import com.cs407.badgermate.data.Event
import java.util.Date

class HomeViewModel : ViewModel() {

    // User Info
    private val _userInfo = MutableLiveData<User>().apply {
        value = User(
            id = "user_001",
            name = "John Doe",
            email = "john.doe@example.com",
            profileImage = null
        )
    }
    val userInfo: LiveData<User> = _userInfo

    // Courses
    private val _courses = MutableLiveData<List<Course>>().apply {
        value = emptyList()
    }
    val courses: LiveData<List<Course>> = _courses

    // Todo Items
    private val _todoItems = MutableLiveData<List<TodoItem>>().apply {
        value = listOf(
            TodoItem(
                id = "todo_001",
                title = "Complete project report",
                description = "Finish the Q4 project report",
                dueDate = Date(System.currentTimeMillis() + 86400000),
                isCompleted = false,
                priority = TodoItem.Priority.HIGH
            ),
            TodoItem(
                id = "todo_002",
                title = "Review team feedback",
                description = "Check and respond to team feedback",
                dueDate = Date(System.currentTimeMillis() + 172800000),
                isCompleted = false,
                priority = TodoItem.Priority.MEDIUM
            ),
            TodoItem(
                id = "todo_003",
                title = "Update documentation",
                description = "Update API documentation",
                dueDate = Date(System.currentTimeMillis() + 259200000),
                isCompleted = true,
                priority = TodoItem.Priority.LOW
            ),
            TodoItem(
                id = "todo_004",
                title = "Schedule meeting",
                description = "Schedule team standup for next week",
                dueDate = Date(System.currentTimeMillis() + 345600000),
                isCompleted = false,
                priority = TodoItem.Priority.MEDIUM
            )
        )
    }
    val todoItems: LiveData<List<TodoItem>> = _todoItems

    // Events
    private val _events = MutableLiveData<List<Event>>().apply {
        value = listOf(
            Event(
                id = "event_001",
                title = "Team Meeting",
                description = "Weekly team standup",
                startTime = Date(System.currentTimeMillis() + 3600000),
                endTime = Date(System.currentTimeMillis() + 5400000),
                location = "Conference Room A"
            ),
            Event(
                id = "event_002",
                title = "Project Review",
                description = "Q4 project review with stakeholders",
                startTime = Date(System.currentTimeMillis() + 86400000),
                endTime = Date(System.currentTimeMillis() + 90000000),
                location = "Virtual - Zoom"
            ),
            Event(
                id = "event_003",
                title = "Client Presentation",
                description = "Present new features to client",
                startTime = Date(System.currentTimeMillis() + 172800000),
                endTime = Date(System.currentTimeMillis() + 176400000),
                location = "Client Office"
            )
        )
    }
    val events: LiveData<List<Event>> = _events

    // Add new course
    fun addCourse(course: Course) {
        val currentList = _courses.value ?: emptyList()
        _courses.value = currentList + course
    }

    // Delete course
    fun deleteCourse(courseId: String) {
        val currentList = _courses.value ?: return
        _courses.value = currentList.filter { it.id != courseId }
    }

    // Helper function to toggle todo completion
    fun toggleTodoCompletion(todoId: String) {
        val currentList = _todoItems.value ?: return
        val updatedList = currentList.map { todo ->
            if (todo.id == todoId) {
                todo.copy(isCompleted = !todo.isCompleted)
            } else {
                todo
            }
        }
        _todoItems.value = updatedList
    }

    // Add new todo item
    fun addTodoItem(title: String, description: String? = null, priority: TodoItem.Priority = TodoItem.Priority.MEDIUM, dueDate: Date? = null) {
        val currentList = _todoItems.value ?: emptyList()
        val newTodo = TodoItem(
            id = "todo_${System.currentTimeMillis()}",
            title = title,
            description = description,
            dueDate = dueDate,
            isCompleted = false,
            priority = priority
        )
        _todoItems.value = currentList + newTodo
    }

    // Delete todo item
    fun deleteTodoItem(todoId: String) {
        val currentList = _todoItems.value ?: return
        _todoItems.value = currentList.filter { it.id != todoId }
    }

    // Update todo priority
    fun updateTodoPriority(todoId: String, newPriority: TodoItem.Priority) {
        val currentList = _todoItems.value ?: return
        val updatedList = currentList.map { todo ->
            if (todo.id == todoId) {
                todo.copy(priority = newPriority)
            } else {
                todo
            }
        }
        _todoItems.value = updatedList
    }

    // Update todo title and description
    fun updateTodoItem(todoId: String, newTitle: String, newDescription: String? = null) {
        val currentList = _todoItems.value ?: return
        val updatedList = currentList.map { todo ->
            if (todo.id == todoId) {
                todo.copy(title = newTitle, description = newDescription)
            } else {
                todo
            }
        }
        _todoItems.value = updatedList
    }
}
