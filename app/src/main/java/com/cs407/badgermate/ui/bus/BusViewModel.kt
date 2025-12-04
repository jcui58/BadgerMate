package com.cs407.badgermate.ui.bus

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Holds UI state (origin/destination text and route path) and
 * coordinates calls to the DirectionsRepository.
 */
class BusViewModel : ViewModel() {

    companion object {
        private const val TAG = "BusViewModel"
    }

    // Use mutableStateOf to manage state for Compose
    var origin by mutableStateOf("")
    var destination by mutableStateOf("")
    var pathPoints by mutableStateOf<List<LatLng>>(emptyList())
    var routeDistance by mutableStateOf("")
    var routeDuration by mutableStateOf("")
    var walkingDistance by mutableStateOf("")
    var walkingDuration by mutableStateOf("")
    var bikingDistance by mutableStateOf("")
    var bikingDuration by mutableStateOf("")

    private var apiKey: String = ""
    // Retrofit service for Google Directions API
    private val api: GoogleDirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDirectionsApiService::class.java)
    }

    private val repository by lazy {
        DirectionsRepository(api)
    }
    /**
     * Store the API key so it can be passed to network calls later.
     */

    fun setApiKey(key: String) {
        apiKey = key
        Log.d(TAG, "API Key set: ${if (key.isNotEmpty()) "Key exists (${key.length} chars)" else "Key is EMPTY!"}")
    }

// Some logcat infor for debug
    fun loadRoute() {
        if (origin.isBlank() || destination.isBlank()) {
            Log.d(TAG, "Origin or destination is blank - Origin: '$origin', Destination: '$destination'")
            return
        }

        Log.d(TAG, "========== LOADING ROUTE ==========")
        Log.d(TAG, "Origin: $origin")
        Log.d(TAG, "Destination: $destination")
        Log.d(TAG, "API Key length: ${apiKey.length}")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling Directions API...")
                val routeInfo = repository.fetchRoute(origin, destination, apiKey)
                
                if (routeInfo != null) {
                    Log.d(TAG, "Route fetched successfully: ${routeInfo.pathPoints.size} points")
                    pathPoints = routeInfo.pathPoints
                    routeDistance = routeInfo.distance
                    routeDuration = routeInfo.duration
                    Log.d(TAG, "Distance: ${routeInfo.distance}, Duration: ${routeInfo.duration}")
                    
                    // Fetch walking route estimate
                    val walkingInfo = repository.estimateWalkingRoute(origin, destination, apiKey)
                    if (walkingInfo != null) {
                        walkingDistance = walkingInfo.distance
                        walkingDuration = walkingInfo.duration
                        Log.d(TAG, "Walking Distance: ${walkingInfo.distance}, Duration: ${walkingInfo.duration}")
                    }
                    
                    // Fetch biking route estimate
                    val bikingInfo = repository.estimateBikingRoute(origin, destination, apiKey)
                    if (bikingInfo != null) {
                        bikingDistance = bikingInfo.distance
                        bikingDuration = bikingInfo.duration
                        Log.d(TAG, "Biking Distance: ${bikingInfo.distance}, Duration: ${bikingInfo.duration}")
                    }
                } else {
                    Log.w(TAG, "WARNING: Route is null - no route returned!")
                    pathPoints = emptyList()
                    routeDistance = ""
                    routeDuration = ""
                    walkingDistance = ""
                    walkingDuration = ""
                    bikingDistance = ""
                    bikingDuration = ""
                }

            } catch (e: Exception) {
                Log.e(TAG, "========== ERROR FETCHING ROUTE ==========")
                Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Error message: ${e.message}", e)
                pathPoints = emptyList()
                routeDistance = ""
                routeDuration = ""
                walkingDistance = ""
                walkingDuration = ""
                bikingDistance = ""
                bikingDuration = ""
            }
        }
    }
}