package com.cs407.badgermate.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.cs407.badgermate.data.TodoItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTodoDialog(
    private val context: Context,
    private val onAdd: (title: String, description: String?, priority: TodoItem.Priority, dueDate: Date?) -> Unit
) {

    private var selectedDate: Date? = null

    fun show() {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val titleInput = EditText(context).apply {
            hint = "Task title"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        val descriptionInput = EditText(context).apply {
            hint = "Description (optional)"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            minLines = 2
        }

        val prioritySpinner = Spinner(context).apply {
            val priorities = arrayOf("LOW", "MEDIUM", "HIGH")
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, priorities)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        // Date selection section
        val dateSection = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        val dateLabel = TextView(context).apply {
            text = "Due Date: "
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 8
            }
        }

        val dateDisplay = TextView(context).apply {
            text = "Not set"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            textSize = 14f
        }

        val dateButton = Button(context).apply {
            text = "Pick Date"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                showDatePicker { selectedDateValue ->
                    selectedDate = selectedDateValue
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    dateDisplay.text = dateFormat.format(selectedDateValue)
                }
            }
        }

        dateSection.addView(dateLabel)
        dateSection.addView(dateDisplay)
        dateSection.addView(dateButton)

        container.addView(titleInput)
        container.addView(descriptionInput)
        container.addView(prioritySpinner)
        container.addView(dateSection)

        AlertDialog.Builder(context)
            .setTitle("Add New Task")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    val description = descriptionInput.text.toString().trim()
                        .takeIf { it.isNotEmpty() }
                    val priority = when (prioritySpinner.selectedItemPosition) {
                        0 -> TodoItem.Priority.LOW
                        1 -> TodoItem.Priority.MEDIUM
                        else -> TodoItem.Priority.HIGH
                    }
                    onAdd(title, description, priority, selectedDate)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            onDateSelected(selectedCalendar.time)
        }, year, month, day).show()
    }
}

