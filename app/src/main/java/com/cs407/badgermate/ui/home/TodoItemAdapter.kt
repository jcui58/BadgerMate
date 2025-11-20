package com.cs407.badgermate.ui.home

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.badgermate.R
import com.cs407.badgermate.data.TodoItem
import java.text.SimpleDateFormat
import java.util.Locale

class TodoItemAdapter(
    private var todos: List<TodoItem>,
    private val onTodoToggle: (String) -> Unit,
    private val onTodoDelete: (String) -> Unit,
    private val onPriorityChange: (String, TodoItem.Priority) -> Unit
) : RecyclerView.Adapter<TodoItemAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.todo_title)
        val descriptionView: TextView = itemView.findViewById(R.id.todo_description)
        val dueDateView: TextView = itemView.findViewById(R.id.todo_due_date)
        val checkBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        val priorityView: TextView = itemView.findViewById(R.id.todo_priority)
        val deleteBtn: TextView = itemView.findViewById(R.id.todo_delete_btn)
        val priorityBtn: TextView = itemView.findViewById(R.id.todo_priority_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        holder.titleView.text = todo.title
        holder.descriptionView.text = todo.description ?: ""
        holder.descriptionView.visibility = if (todo.description.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        
        if (todo.dueDate != null) {
            holder.dueDateView.text = "Due: ${dateFormat.format(todo.dueDate)}"
            holder.dueDateView.visibility = android.view.View.VISIBLE
        } else {
            holder.dueDateView.visibility = android.view.View.GONE
        }

        holder.checkBox.isChecked = todo.isCompleted
        holder.checkBox.setOnCheckedChangeListener { _, _ ->
            onTodoToggle(todo.id)
        }

        holder.priorityView.text = todo.priority.name
        when (todo.priority) {
            TodoItem.Priority.HIGH -> holder.priorityView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            TodoItem.Priority.MEDIUM -> holder.priorityView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
            TodoItem.Priority.LOW -> holder.priorityView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        }

        // Delete button
        holder.deleteBtn.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Task?")
                .setMessage("Are you sure you want to delete \"${todo.title}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    onTodoDelete(todo.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Priority change button
        holder.priorityBtn.setOnClickListener {
            val priorities = arrayOf("LOW", "MEDIUM", "HIGH")
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Change Priority")
                .setSingleChoiceItems(
                    priorities,
                    when (todo.priority) {
                        TodoItem.Priority.LOW -> 0
                        TodoItem.Priority.MEDIUM -> 1
                        TodoItem.Priority.HIGH -> 2
                    }
                ) { dialog, which ->
                    val newPriority = when (which) {
                        0 -> TodoItem.Priority.LOW
                        1 -> TodoItem.Priority.MEDIUM
                        else -> TodoItem.Priority.HIGH
                    }
                    onPriorityChange(todo.id, newPriority)
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun getItemCount(): Int = todos.size

    fun updateTodos(newTodos: List<TodoItem>) {
        todos = newTodos
        notifyDataSetChanged()
    }
}
