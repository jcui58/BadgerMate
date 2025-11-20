package com.cs407.badgermate.ui.bus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BusScreen(
    viewModel: BusViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5FB))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(top = 50.dp, bottom = 80.dp) // 预留顶部和底部空间
    ) {
        RealTimeTransitCard(
            issuesNearby = uiState.issuesNearby,
            routes = uiState.liveRoutes
        )

        Spacer(Modifier.height(16.dp))

        TimeToLeaveCard(
            className = uiState.nextClassName,
            hint = uiState.nextClassStartsIn,
            departTime = uiState.departTime
        )

        Spacer(Modifier.height(16.dp))

        SectionHeader(
            title = "Arrivals",
            subtitle = "",
            actionText = "View All"
        )

        Spacer(Modifier.height(8.dp))

        ArrivalsList(uiState.arrivals)

        Spacer(Modifier.height(16.dp))

        SectionHeader(
            title = "Saved Routes",
            subtitle = "",
            actionText = ""
        )

        Spacer(Modifier.height(8.dp))

        SavedRoutesCard(uiState.savedRoutes)

        Spacer(Modifier.height(16.dp))

        LiveCampusMapCard()
    }
}

/* ---------- Top Real-Time Transit Card ---------- */

@Composable
private fun RealTimeTransitCard(
    issuesNearby: Int,
    routes: List<RouteEta>
) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFF4FACFE),
            Color(0xFF00F2FE)
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Real-Time Transit ⚡",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Smart tracking, never miss your ride",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$issuesNearby issues nearby",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF3B9BFF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Live",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        routes.forEach { route ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = route.route,
                                    fontSize = 11.sp,
                                    color = Color(0xFF999999)
                                )
                                Text(
                                    text = route.eta,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF333333)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Time to Leave Card ---------- */

@Composable
private fun TimeToLeaveCard(
    className: String,
    hint: String,
    departTime: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7EC)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFA84C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⏰", fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Time to Leave!",
                        fontSize = 13.sp,
                        color = Color(0xFFB56500),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = className,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3A2C15)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = hint,
                fontSize = 12.sp,
                color = Color(0xFFB98F4A)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Take Route",
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "80 · 8 min",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Depart at",
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = departTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { /* TODO: view route */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8A3C)
                )
            ) {
                Text("View Route")
            }
        }
    }
}

/* ---------- Section Header ---------- */

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF27243A)
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF9A97B8)
                )
            }
        }
        if (actionText.isNotEmpty()) {
            Text(
                text = actionText,
                fontSize = 12.sp,
                color = Color(0xFF6A5AE0),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/* ---------- Arrivals List ---------- */

@Composable
private fun ArrivalsList(arrivals: List<Arrival>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        arrivals.forEach { arrival ->
            ArrivalCard(arrival)
        }
    }
}

@Composable
private fun ArrivalCard(arrival: Arrival) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = Color(0xFF4E6BFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = arrival.route,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF27243A)
                )
                Text(
                    text = arrival.destination,
                    fontSize = 12.sp,
                    color = Color(0xFF7876A1)
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = arrival.eta,
                        fontSize = 11.sp,
                        color = Color(0xFF27243A)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = arrival.next,
                        fontSize = 11.sp,
                        color = Color(0xFF9A97B8)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when (arrival.status) {
                            "Arriving" -> Color(0xFFE7F8EF)
                            else -> Color(0xFFEFF3FF)
                        }
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = arrival.status,
                    fontSize = 11.sp,
                    color = when (arrival.status) {
                        "Arriving" -> Color(0xFF30A56A)
                        else -> Color(0xFF6A5AE0)
                    }
                )
            }
        }
    }
}

/* ---------- Saved Routes ---------- */

@Composable
private fun SavedRoutesCard(routes: List<SavedRoute>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC85D),
                    modifier = Modifier.size(18.dp)
                )
            }

            routes.forEachIndexed { index, route ->
                SavedRouteRow(route)
                if (index != routes.lastIndex) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color(0xFFF0F0F3),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedRouteRow(route: SavedRoute) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = route.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF27243A)
            )
            Text(
                text = route.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF9A97B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = {},
                    label = { Text(route.routeLabel, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFEFF3FF)
                    )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = route.timeLabel,
                    fontSize = 11.sp,
                    color = Color(0xFF7876A1)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.DirectionsBus,
            contentDescription = null,
            tint = Color(0xFF4E6BFF),
            modifier = Modifier.size(18.dp)
        )
    }
}

/* ---------- Live Campus Map ---------- */

@Composable
private fun LiveCampusMapCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Live Campus Map",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF27243A)
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFE3F2FF),
                                Color(0xFFEBFFFA)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4E6BFF),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Real-time Tracking",
                        fontSize = 13.sp,
                        color = Color(0xFF27406A),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "All buses on campus",
                        fontSize = 11.sp,
                        color = Color(0xFF7A8AA6)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { /* TODO: open full map */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3465FF)
                )
            ) {
                Text("Open Full Map")
            }
        }
    }
}
