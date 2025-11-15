package com.cs407.badgermate.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val userName: String = "Student",
    val dateText: String = "Wednesday, October 23",
    val classesToday: String = "2/4",
    val focusStatus: String = "Focus",
    val steps: String = "6,543",
    val streakDays: Int = 7
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateSteps(newSteps: String) {
        _uiState.value = _uiState.value.copy(steps = newSteps)
    }
}
