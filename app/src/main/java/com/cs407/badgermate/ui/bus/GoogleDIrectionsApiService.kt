package com.cs407.badgermate.ui.bus

import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Retrofit service definition for calling the Google Directions API.
 */
interface GoogleDirectionsApiService {

    @GET("directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("mode") mode: String = "transit"
    ): DirectionsResponse

    @GET("directions/json")
    suspend fun getWalkingRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("mode") mode: String = "walking"
    ): DirectionsResponse

    @GET("directions/json")
    suspend fun getBikingRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("mode") mode: String = "bicycling"
    ): DirectionsResponse
}