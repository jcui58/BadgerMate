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

data class DiningUiState(
    val aiRecommendation: String = "",
    val isLoadingRecommendation: Boolean = false,
    val recommendationError: String = ""
)

class DiningViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DiningUiState())
    val uiState: StateFlow<DiningUiState> = _uiState.asStateFlow()
    
    private lateinit var menuRecommendationRepository: MenuRecommendationRepository
    
    private var hasGeneratedInitialRecommendation = false
    
    // This should be set from your secure storage or BuildConfig
    private val openAIApiKey: String
        get() = BuildConfig.OPENAI_API_KEY

    fun generateInitialRecommendationOnce(userProfile: ProfileEntity?) {
        if (!hasGeneratedInitialRecommendation) {
            hasGeneratedInitialRecommendation = true
            generateMenuRecommendation(userProfile)
        }
    }

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
