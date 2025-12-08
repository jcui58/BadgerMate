package com.cs407.badgermate.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.badgermate.data.User
import com.cs407.badgermate.data.TodoItem
import com.cs407.badgermate.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // User Info
    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> = _userInfo

    // Courses
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    // Todo Items
    private val _todoItems = MutableLiveData<List<TodoItem>>()
    val todoItems: LiveData<List<TodoItem>> = _todoItems

    // Events
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    init {
        loadUserData()
        loadCourses()
        loadTodoItems()
        loadEvents()
    }

    // Load user data from Firestore
    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = User.fromMap(document.data ?: emptyMap())
                        _userInfo.value = user
                    } else {
                        // Fallback to basic user info
                        _userInfo.value = User(
                            id = currentUser.uid,
                            name = "User",
                            email = currentUser.email ?: "",
                            profileImage = null
                        )
                    }
                }
                .addOnFailureListener {
                    // Fallback to basic user info
                    _userInfo.value = User(
                        id = currentUser.uid,
                        name = "User",
                        email = currentUser.email ?: "",
                        profileImage = null
                    )
                }
        }
    }

    // Load courses from Firestore
    private fun loadCourses() {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("courses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _courses.value = emptyList()
                    return@addSnapshotListener
                }

                val coursesList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Course(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            daysOfWeek = (doc.get("daysOfWeek") as? List<Long>)?.map { it.toInt() } ?: emptyList(),
                            startTime = doc.getString("startTime") ?: "",
                            endTime = doc.getString("endTime") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                _courses.value = coursesList
            }
    }

    // Load todo items from Firestore
    private fun loadTodoItems() {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _todoItems.value = emptyList()
                    return@addSnapshotListener
                }

                val todosList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        TodoItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description"),
                            dueDate = doc.getDate("dueDate"),
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            priority = TodoItem.Priority.valueOf(
                                doc.getString("priority") ?: "MEDIUM"
                            )
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                _todoItems.value = todosList
            }
    }

    // Load events from Firestore
    private fun loadEvents() {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _events.value = emptyList()
                    return@addSnapshotListener
                }

                val eventsList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Event(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            startTime = doc.getDate("startTime") ?: Date(),
                            endTime = doc.getDate("endTime") ?: Date(),
                            location = doc.getString("location") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                _events.value = eventsList
            }
    }

    // Add new course
    fun addCourse(course: Course) {
        val currentUser = auth.currentUser ?: return

        val courseData = hashMapOf(
            "name" to course.name,
            "daysOfWeek" to course.daysOfWeek,
            "startTime" to course.startTime,
            "endTime" to course.endTime,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("courses")
            .add(courseData)
            .addOnSuccessListener {
                // Course added successfully - listener will update LiveData
            }
    }

    // Delete course
    fun deleteCourse(courseId: String) {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("courses")
            .document(courseId)
            .delete()
    }

    // Helper function to toggle todo completion
    fun toggleTodoCompletion(todoId: String) {
        val currentUser = auth.currentUser ?: return
        val todoItem = _todoItems.value?.find { it.id == todoId } ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .document(todoId)
            .update("isCompleted", !todoItem.isCompleted)
    }

    // Add new todo item
    fun addTodoItem(
        title: String,
        description: String? = null,
        priority: TodoItem.Priority = TodoItem.Priority.MEDIUM,
        dueDate: Date? = null
    ) {
        val currentUser = auth.currentUser ?: return

        val todoData = hashMapOf(
            "title" to title,
            "description" to description,
            "priority" to priority.name,
            "dueDate" to dueDate,
            "isCompleted" to false,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .add(todoData)
    }

    // Delete todo item
    fun deleteTodoItem(todoId: String) {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .document(todoId)
            .delete()
    }

    // Update todo priority
    fun updateTodoPriority(todoId: String, newPriority: TodoItem.Priority) {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .document(todoId)
            .update("priority", newPriority.name)
    }

    // Update todo title and description
    fun updateTodoItem(todoId: String, newTitle: String, newDescription: String? = null) {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("todos")
            .document(todoId)
            .update(
                mapOf(
                    "title" to newTitle,
                    "description" to newDescription
                )
            )
    }
}