package com.cs407.badgermate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.badgermate.databinding.FragmentHomeBinding
import com.cs407.badgermate.data.TodoItem as DataTodoItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup Compose UI
        binding.composeContainer?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // Use remember to hold mutable state that will trigger recompose
                var userInfo by remember { mutableStateOf(homeViewModel.userInfo.value) }
                var todoItems by remember { mutableStateOf(homeViewModel.todoItems.value) }
                var courses by remember { mutableStateOf(homeViewModel.courses.value) }

                // Observe LiveData changes
                DisposableEffect(Unit) {
                    val userObserver = androidx.lifecycle.Observer<com.cs407.badgermate.data.User> {
                        userInfo = it
                    }
                    val todoObserver = androidx.lifecycle.Observer<List<DataTodoItem>> {
                        todoItems = it
                    }
                    val courseObserver = androidx.lifecycle.Observer<List<Course>> {
                        courses = it
                    }

                    homeViewModel.userInfo.observe(viewLifecycleOwner, userObserver)
                    homeViewModel.todoItems.observe(viewLifecycleOwner, todoObserver)
                    homeViewModel.courses.observe(viewLifecycleOwner, courseObserver)

                    onDispose {
                        homeViewModel.userInfo.removeObserver(userObserver)
                        homeViewModel.todoItems.removeObserver(todoObserver)
                        homeViewModel.courses.removeObserver(courseObserver)
                    }
                }

                HomeScreenCompose(
                    userName = userInfo?.name ?: "",
                    userEmail = userInfo?.email ?: "",
                    todoItems = todoItems?.map { todoItem ->
                        TodoItem(
                            id = todoItem.id,
                            title = todoItem.title,
                            description = todoItem.description ?: "",
                            completed = todoItem.isCompleted,
                            priority = todoItem.priority.name
                        )
                    } ?: emptyList(),
                    courses = courses ?: emptyList(),
                    onAddTodoClick = {
                        AddTodoDialog(requireContext()) { title, description, priority, dueDate ->
                            homeViewModel.addTodoItem(title, description, priority, dueDate)
                        }.show()
                    },
                    onAddCourse = { course ->
                        homeViewModel.addCourse(course)
                    },
                    onDeleteCourse = { courseId ->
                        homeViewModel.deleteCourse(courseId)
                    },
                    onTodoToggle = { todoId ->
                        homeViewModel.toggleTodoCompletion(todoId)
                    },
                    onTodoDelete = { todoId ->
                        homeViewModel.deleteTodoItem(todoId)
                    }
                )
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

