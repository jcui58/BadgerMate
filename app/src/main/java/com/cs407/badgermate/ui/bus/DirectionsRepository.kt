package com.cs407.badgermate.ui.bus

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DirectionsRepository(private val api: GoogleDirectionsApiService) {

    companion object {
        private const val TAG = "DirectionsRepository"
    }

    suspend fun fetchRoute(origin: String, destination: String, apiKey: String): List<LatLng> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "========== FETCHING ROUTE ==========")
                Log.d(TAG, "Origin: $origin")
                Log.d(TAG, "Destination: $destination")
                Log.d(TAG, "API Key: ${apiKey.take(10)}...${apiKey.takeLast(4)}")

                val response = api.getRoute(origin, destination, apiKey)

                Log.d(TAG, "API Response received")
                Log.d(TAG, "Number of routes: ${response.routes.size}")

                if (response.routes.isEmpty()) {
                    Log.w(TAG, "No routes found in response!")
                    return@withContext emptyList()
                }

                val route = response.routes[0]
                val polyline = route.overview_polyline.points

                Log.d(TAG, "Polyline string length: ${polyline.length}")
                Log.d(TAG, "Polyline preview: ${polyline.take(50)}...")

                val decodedPoints = PolyUtil.decode(polyline)
                Log.d(TAG, "Decoded ${decodedPoints.size} points")

                if (decodedPoints.isNotEmpty()) {
                    Log.d(TAG, "First point: ${decodedPoints.first()}")
                    Log.d(TAG, "Last point: ${decodedPoints.last()}")
                }

                decodedPoints

            } catch (e: Exception) {
                Log.e(TAG, "========== ERROR IN REPOSITORY ==========")
                Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Error message: ${e.message}", e)

                // Log the full stack trace
                e.printStackTrace()

                emptyList()
            }
        }
    }
}