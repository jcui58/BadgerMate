package com.cs407.badgermate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.badgermate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var todoAdapter: TodoItemAdapter
    private lateinit var eventAdapter: EventAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize adapters with callbacks
        todoAdapter = TodoItemAdapter(
            emptyList(),
            onTodoToggle = { todoId ->
                homeViewModel.toggleTodoCompletion(todoId)
            },
            onTodoDelete = { todoId ->
                homeViewModel.deleteTodoItem(todoId)
            },
            onPriorityChange = { todoId, newPriority ->
                homeViewModel.updateTodoPriority(todoId, newPriority)
            }
        )
        eventAdapter = EventAdapter(emptyList())

        // Setup RecyclerViews
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = todoAdapter
        }

        binding.eventRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = eventAdapter
        }

        // Observe user info
        homeViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            binding.userName.text = user.name
            binding.userEmail.text = user.email
        }

        // Observe todo items
        homeViewModel.todoItems.observe(viewLifecycleOwner) { todos ->
            todoAdapter.updateTodos(todos)
        }

        // Observe events
        homeViewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateEvents(events)
        }

        // Setup FAB button for adding new todo
        binding.fabAddTodo.setOnClickListener {
            AddTodoDialog(requireContext()) { title, description, priority, dueDate ->
                homeViewModel.addTodoItem(title, description, priority, dueDate)
            }.show()
        }

        // Setup top button for adding new todo
        binding.btnAddTodo.setOnClickListener {
            AddTodoDialog(requireContext()) { title, description, priority, dueDate ->
                homeViewModel.addTodoItem(title, description, priority, dueDate)
            }.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

