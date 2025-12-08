package com.cs407.badgermate.ui.bus

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

/**
 *  - shows origin/destination text fields and a "Get Route" button
 *  - displays a Google Map centered on Madison
 *  - draws the transit route polyline and start/end markers when available
 *  - puts the Google Map and route info cards in the same LazyColumn so they scroll together
 */
@Composable
fun BusScreen(
    origin: String,
    destination: String,
    pathPoints: List<LatLng>,
    onOriginChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onGetRoute: () -> Unit,
    routeDistance: String = "",
    routeDuration: String = "",
    walkingDistance: String = "",
    walkingDuration: String = "",
    bikingDistance: String = "",
    bikingDuration: String = "",
) {
    val context = LocalContext.current

    // Default map location -> Madison, Wisconsin
    val madison = LatLng(43.0731, -89.4012)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(madison, 13f)
    }

    // Auto adjust camera when route is loaded
    LaunchedEffect(pathPoints) {
        if (pathPoints.isNotEmpty()) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(pathPoints.first(), 14f)
        }
    }

    // Check whether the app has location permission to enable "my location" features
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
    ) {

        // Origin & Destination text fields
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = origin,
                onValueChange = onOriginChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("e.g. Memorial Union") },
                label = { Text("Origin") },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = destination,
                onValueChange = onDestinationChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g. State Street") },
                label = { Text("Destination") },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onGetRoute() }
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // Get Route Button
        Button(
            onClick = onGetRoute,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = origin.isNotBlank() && destination.isNotBlank()
        ) {
            Text("Get Route")
        }

        Spacer(Modifier.height(12.dp))

        // Scrollable content: Google Map + route information cards in the same LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 80.dp)
        ) {
            // Google Map as first item so it scrolls together with the cards
            item {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = hasLocationPermission
                    )
                ) {
                    // Draw route polyline
                    if (pathPoints.isNotEmpty()) {
                        Polyline(
                            points = pathPoints,
                            color = Color.Blue,
                            width = 10f
                        )
                    }

                    // Start and end markers
                    if (pathPoints.size >= 2) {
                        Marker(
                            state = MarkerState(position = pathPoints.first()),
                            title = "Start",
                            snippet = origin
                        )
                        Marker(
                            state = MarkerState(position = pathPoints.last()),
                            title = "End",
                            snippet = destination
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            // Route info cards
            if (pathPoints.isNotEmpty()) {
                // Transit route summary
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Transit Route",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (routeDistance.isNotEmpty()) {
                                Text("Distance: $routeDistance")
                            }
                            if (routeDuration.isNotEmpty()) {
                                Text("Duration: $routeDuration")
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }

                // Walking estimate
                if (walkingDistance.isNotEmpty() || walkingDuration.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Walking",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (walkingDistance.isNotEmpty()) {
                                    Text("Distance: $walkingDistance")
                                }
                                if (walkingDuration.isNotEmpty()) {
                                    Text("Duration: $walkingDuration")
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Biking estimate
                if (bikingDistance.isNotEmpty() || bikingDuration.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Biking",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (bikingDistance.isNotEmpty()) {
                                    Text("Distance: $bikingDistance")
                                }
                                if (bikingDuration.isNotEmpty()) {
                                    Text("Duration: $bikingDuration")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
