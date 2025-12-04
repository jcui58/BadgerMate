package com.cs407.badgermate.ui.bus

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsApiService {

    @GET("directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("mode") mode: String = "transit"
    ): DirectionsResponse
}