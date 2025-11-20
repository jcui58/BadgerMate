package com.cs407.badgermate.ui.dining

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MealItem(
    val name: String,
    val calories: Int
)

data class MealUi(
    val title: String,
    val hall: String,
    val matchPercent: Int,
    val items: List<MealItem>,
    val totalCalories: Int,
    val footerText: String
)

data class Dish(
    val name: String,
    val tags: List<String>,
    val calories: Int
)

data class DiningUiState(
    val completionPercent: Int = 43,
    val dailyCalories: Int = 950,
    val dailyGoalCalories: Int = 2200,
    val remainingCalories: Int = 1250,
    val protein: Int = 75,
    val carbs: Int = 95,
    val fat: Int = 22,
    val lunch: MealUi = MealUi(
        title = "Lunch",
        hall = "Dining Hall #1",
        matchPercent = 95,
        items = listOf(
            MealItem("Grilled Chicken Breast", 220),
            MealItem("Brown Rice", 215),
            MealItem("Broccoli & Mushroom Stir-fry", 75)
        ),
        totalCalories = 510,
        footerText = "High protein, low fat - matches your fitness goals"
    ),
    val dinner: MealUi = MealUi(
        title = "Dinner",
        hall = "Dining Hall #2",
        matchPercent = 89,
        items = listOf(
            MealItem("Steamed Sea Bass", 180),
            MealItem("Quinoa Salad", 220),
            MealItem("Spinach Soup", 40)
        ),
        totalCalories = 440,
        footerText = "Rich in Omega 3, supports recovery"
    ),
    val hall1Menu: List<Dish> = listOf(
        Dish("Kung Pao Chicken", listOf("Spicy", "Protein"), 450),
        Dish("Braised Pork Belly", listOf("High Calorie"), 580),
        Dish("Tomato & Egg", listOf("Vegetarian", "Light"), 220),
        Dish("Mapo Tofu", listOf("Spicy", "Vegetarian"), 280)
    ),
    val hall2Menu: List<Dish> = listOf(
        Dish("Grilled Salmon", listOf("Omega 3"), 420),
        Dish("Caesar Salad", listOf("Light"), 190),
        Dish("Miso Soup", listOf("Warm"), 90)
    )
)

class DiningViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiningUiState())
    val uiState: StateFlow<DiningUiState> = _uiState.asStateFlow()

    // 后面如果要跟后端联动，就在这里写更新函数
}
