package com.cs407.badgermate.ui.bus

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.graphics.Color

@Composable
fun BusScreen(
    origin: String,
    destination: String,
    pathPoints: List<LatLng>,
    onOriginChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onGetRoute: () -> Unit,
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

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
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
}