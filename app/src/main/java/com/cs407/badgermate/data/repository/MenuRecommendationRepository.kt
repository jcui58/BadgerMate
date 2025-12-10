package com.cs407.badgermate.data.repository

import android.util.Log
import com.cs407.badgermate.api.ChatCompletionRequest
import com.cs407.badgermate.api.Message
import com.cs407.badgermate.api.OpenAIApiService
import com.cs407.badgermate.data.profile.ProfileEntity
import com.cs407.badgermate.ui.dining.Dish
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MenuRecommendationRepository(
    private val openAIApiKey: String
) {
    
    init {
        Log.d("MenuRecommendationRepository", "API Key length: ${openAIApiKey.length}")
        Log.d("MenuRecommendationRepository", "API Key starts with: ${openAIApiKey.take(10)}")
        Log.d("MenuRecommendationRepository", "API Key is empty: ${openAIApiKey.isEmpty()}")
    }
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            Log.d("MenuRecommendationRepository", "Original request URL: ${originalRequest.url}")
            
            val requestWithHeaders = originalRequest.newBuilder()
                .header("Authorization", "Bearer $openAIApiKey")
                .header("Content-Type", "application/json")
                .build()
            
            Log.d("MenuRecommendationRepository", "Auth header: Bearer ${openAIApiKey.take(10)}...")
            Log.d("MenuRecommendationRepository", "Full request headers: ${requestWithHeaders.headers}")
            
            chain.proceed(requestWithHeaders)
        }
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val openAIService = retrofit.create(OpenAIApiService::class.java)
    
    /**
     * Generate personalized menu recommendations based on user profile and available dishes
     */
    suspend fun generateMenuRecommendation(
        userProfile: ProfileEntity,
        availableDishes: List<Dish>
    ): String {
        // Calculate BMI and other metrics
        val heightInches = (userProfile.heightFeet.toIntOrNull() ?: 0) * 12 + 
                          (userProfile.heightInches.toIntOrNull() ?: 0)
        val weight = userProfile.weight.toDoubleOrNull() ?: 0.0
        val bmi = if (heightInches > 0 && weight > 0) {
            (weight * 703) / (heightInches * heightInches)
        } else {
            0.0
        }
        
        val targetWeight = userProfile.targetWeight.toDoubleOrNull() ?: weight
        val weightDifference = targetWeight - weight
        
        // Create prompt for ChatGPT
        val systemPrompt = """
            You are a professional nutrition advisor. Recommend 3-4 dishes from the menu that suit the user's profile and goals.
            Keep explanations brief and focus on key benefits. End with a summary of the recommended menu.
        """.trimIndent()
        
        val userPrompt = """
            Your Profile:
            - Height: ${userProfile.heightFeet}'${userProfile.heightInches}"
            - Current Weight: ${userProfile.weight} lbs
            - Target Weight: ${userProfile.targetWeight} lbs
            - Gender: ${userProfile.gender}
            - BMI: ${"%.1f".format(bmi)}
            
            Available Dishes:
            ${availableDishes.joinToString("\n") { dish ->
                "- ${dish.name} (${dish.calories} calories) - Tags: ${dish.tags.joinToString(", ")}"
            }}
            
            Recommend 3-4 dishes. For each, briefly explain why it's good for you and total calories.
            At the end, provide a "RECOMMENDED MENU" section with all dishes combined with total calories.
            Keep each explanation to 1-2 sentences max.
        """.trimIndent()
        
        val request = ChatCompletionRequest(
            messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = userPrompt)
            )
        )
        
        return try {
            val response = openAIService.generateMenuRecommendation(
                authorization = "Bearer $openAIApiKey",
                request = request
            )
            response.choices.firstOrNull()?.message?.content ?: "No recommendation available"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error generating recommendation: ${e.message}"
        }
    }
}
