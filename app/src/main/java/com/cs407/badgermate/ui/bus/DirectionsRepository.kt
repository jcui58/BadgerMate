package com.cs407.badgermate.ui.bus

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class RouteInfo(
    val pathPoints: List<LatLng>,
    val distance: String,
    val duration: String
)

class DirectionsRepository(private val api: GoogleDirectionsApiService) {

    companion object {
        private const val TAG = "DirectionsRepository"
    }
    /**
     * Fetch a route between the given origin and destination using the Directions API.
     * Returns RouteInfo containing path points, distance, and duration on success,
     * or null if anything goes wrong.
     */
    suspend fun fetchRoute(origin: String, destination: String, apiKey: String): RouteInfo? {
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
                    return@withContext null
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

                // Extract distance and duration from the first leg
                val leg = route.legs.firstOrNull()
                val distance = leg?.distance?.text ?: "Unknown"
                val duration = leg?.duration?.text ?: "Unknown"

                Log.d(TAG, "Distance: $distance")
                Log.d(TAG, "Duration: $duration")

                RouteInfo(
                    pathPoints = decodedPoints,
                    distance = distance,
                    duration = duration
                )

            } catch (e: Exception) {
                Log.e(TAG, "========== ERROR IN REPOSITORY ==========")
                Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Error message: ${e.message}", e)

                // Log the full stack trace
                e.printStackTrace()

                null
            }
        }
    }

    /**
     * Estimate walking distance and duration based on the two points.
     */
    suspend fun estimateWalkingRoute(origin: String, destination: String, apiKey: String): RouteInfo? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching walking route...")
                val response = api.getWalkingRoute(origin, destination, apiKey)

                if (response.routes.isEmpty()) {
                    return@withContext null
                }

                val route = response.routes[0]
                val polyline = route.overview_polyline.points
                val decodedPoints = PolyUtil.decode(polyline)
                val leg = route.legs.firstOrNull()
                val distance = leg?.distance?.text ?: "Unknown"
                val duration = leg?.duration?.text ?: "Unknown"

                RouteInfo(
                    pathPoints = decodedPoints,
                    distance = distance,
                    duration = duration
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching walking route", e)
                null
            }
        }
    }

    /**
     * Fetch biking route with distance and duration.
     */
    suspend fun estimateBikingRoute(origin: String, destination: String, apiKey: String): RouteInfo? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching biking route...")
                val response = api.getBikingRoute(origin, destination, apiKey)

                if (response.routes.isEmpty()) {
                    return@withContext null
                }

                val route = response.routes[0]
                val polyline = route.overview_polyline.points
                val decodedPoints = PolyUtil.decode(polyline)
                val leg = route.legs.firstOrNull()
                val distance = leg?.distance?.text ?: "Unknown"
                val duration = leg?.duration?.text ?: "Unknown"

                RouteInfo(
                    pathPoints = decodedPoints,
                    distance = distance,
                    duration = duration
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching biking route", e)
                null
            }
        }
    }
}