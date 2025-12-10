package com.cs407.badgermate.ui.dining

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cs407.badgermate.BuildConfig
import com.cs407.badgermate.data.profile.ProfileEntity
import com.cs407.badgermate.data.repository.MenuRecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    ),
    val aiRecommendation: String = "",
    val isLoadingRecommendation: Boolean = false,
    val recommendationError: String = ""
)

class DiningViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DiningUiState())
    val uiState: StateFlow<DiningUiState> = _uiState.asStateFlow()
    
    private lateinit var menuRecommendationRepository: MenuRecommendationRepository
    
    // This should be set from your secure storage or BuildConfig
    private val openAIApiKey: String
        get() = BuildConfig.OPENAI_API_KEY

    fun generateMenuRecommendation(userProfile: ProfileEntity?) {
        Log.d("DiningViewModel", "generateMenuRecommendation called")
        
        if (userProfile == null) {
            updateError("User profile not available")
            return
        }
        
        if (openAIApiKey.isEmpty()) {
            Log.e("DiningViewModel", "OpenAI API Key is empty!")
            updateError("OpenAI API Key not configured")
            return
        }
        
        Log.d("DiningViewModel", "Creating MenuRecommendationRepository with key length: ${openAIApiKey.length}")
        menuRecommendationRepository = MenuRecommendationRepository(openAIApiKey)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingRecommendation = true, recommendationError = "")
            
            try {
                Log.d("DiningViewModel", "Generating menu recommendation")
                val recommendation = menuRecommendationRepository.generateMenuRecommendation(
                    userProfile = userProfile
                )
                
                Log.d("DiningViewModel", "Recommendation received: ${recommendation.take(50)}...")
                _uiState.value = _uiState.value.copy(
                    aiRecommendation = recommendation,
                    isLoadingRecommendation = false
                )
            } catch (e: Exception) {
                Log.e("DiningViewModel", "Exception caught: ${e.message}", e)
                updateError("Failed to generate recommendation: ${e.message}")
            }
        }
    }
    
    private fun updateError(error: String) {
        _uiState.value = _uiState.value.copy(
            recommendationError = error,
            isLoadingRecommendation = false
        )
    }
}
