package com.cs407.badgermate.ui.bus

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RouteEta(
    val route: String,
    val eta: String
)

data class Arrival(
    val route: String,
    val destination: String,
    val eta: String,
    val next: String,
    val status: String
)

data class SavedRoute(
    val title: String,
    val subtitle: String,
    val routeLabel: String,
    val timeLabel: String
)

data class BusUiState(
    val issuesNearby: Int = 3,
    val liveRoutes: List<RouteEta> = listOf(
        RouteEta("Route 80", "3 min"),
        RouteEta("Route 81", "7 min"),
        RouteEta("Route 82", "12 min")
    ),
    val nextClassName: String = "Data Structures & Algorithms",
    val nextClassStartsIn: String = "starts in 15 minutes",
    val departTime: String = "09:30",
    val arrivals: List<Arrival> = listOf(
        Arrival("Route 8C", "CS Building", "3 min", "Next: 15 min", "Arriving"),
        Arrival("Route 81", "Library", "7 min", "Next: 22 min", "On Time"),
        Arrival("Route 82", "Sports Center", "12 min", "Next: 27 min", "On Time")
    ),
    val savedRoutes: List<SavedRoute> = listOf(
        SavedRoute(
            title = "To Class",
            subtitle = "Dorm Building A → CS Building",
            routeLabel = "Route 80",
            timeLabel = "8 min"
        ),
        SavedRoute(
            title = "Back to Dorm",
            subtitle = "Library → Dorm Building A",
            routeLabel = "Route 81",
            timeLabel = "12 min"
        )
    )
)

class BusViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BusUiState())
    val uiState: StateFlow<BusUiState> = _uiState.asStateFlow()

    // 后面如果要接后端，就在这里更新 _uiState
}
