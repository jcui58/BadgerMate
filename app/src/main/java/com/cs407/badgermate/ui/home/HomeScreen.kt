package com.cs407.badgermate.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5FB))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        GreetingCard(
            userName = uiState.userName,
            dateText = uiState.dateText,
            classesToday = uiState.classesToday,
            focusStatus = uiState.focusStatus,
            steps = uiState.steps
        )
        Spacer(modifier = Modifier.height(16.dp))
        NextClassCard()
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Today's Schedule", subtitle = "3 classes")
        Spacer(modifier = Modifier.height(8.dp))
        ScheduleList()
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Upcoming Deadlines", subtitle = "3 urgent")
        Spacer(modifier = Modifier.height(8.dp))
        DeadlineList()
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "Weekly Progress", subtitle = "${uiState.streakDays} day streak")
        Spacer(modifier = Modifier.height(8.dp))
        WeeklyProgress()
        Spacer(modifier = Modifier.height(16.dp))
        MotivationCard()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun GreetingCard(
    userName: String,
    dateText: String,
    classesToday: String,
    focusStatus: String,
    steps: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A5AE0),
                        Color(0xFFF857C3)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Good Afternoon, $userName!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = dateText,
                color = Color(0xFFE0D9FF),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallStatCard(
                    title = "Classes Today",
                    value = classesToday,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "Status",
                    value = focusStatus,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallStatCard(
                    title = "Steps",
                    value = steps,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.16f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(14.dp)) {
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = Color(0xFFE5E1FF),
                    fontSize = 11.sp
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}



@Composable
private fun NextClassCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF1FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Next Class",
                fontSize = 11.sp,
                color = Color(0xFF7876A1)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Data Structures & Algorithms",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF27243A)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:30 - 11:00",
                    fontSize = 12.sp,
                    color = Color(0xFF27243A)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB7B2FF))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "CS Building 703",
                    fontSize = 12.sp,
                    color = Color(0xFF7876A1)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF27243A),
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF9A97B8)
            )
        }
        Text(
            text = "See all",
            fontSize = 12.sp,
            color = Color(0xFF6A5AE0),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ScheduleList() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ScheduleItemCard(
            title = "Data Structures & Algorithms",
            time = "10:00 - 11:30",
            location = "CS Building 408",
            accentColor = Color(0xFF6A5AE0)
        )
        ScheduleItemCard(
            title = "Calculus II",
            time = "14:00 - 15:00",
            location = "Math Building 210",
            accentColor = Color(0xFFFFC85D)
        )
        ScheduleItemCard(
            title = "English Composition",
            time = "17:00 - 17:50",
            location = "Humanities 105",
            accentColor = Color(0xFF4AD991)
        )
    }
}

@Composable
private fun ScheduleItemCard(
    title: String,
    time: String,
    location: String,
    accentColor: Color
) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF27243A)
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color(0xFF7876A1),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = Color(0xFFB1AEC9),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DeadlineList() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DeadlineCard(
            title = "Physics Lab Report",
            course = "General Physics",
            due = "Today · 11:59 PM",
            accentColor = Color(0xFFFF6B6B),
            isUrgent = true
        )
        DeadlineCard(
            title = "Programming Assignment",
            course = "Data Structures",
            due = "Tomorrow · 11:59 PM",
            accentColor = Color(0xFFFFC85D),
            isUrgent = false
        )
        DeadlineCard(
            title = "Modern Paper",
            course = "English Composition",
            due = "In 3 days",
            accentColor = Color(0xFF4AD991),
            isUrgent = false
        )
    }
}

@Composable
private fun DeadlineCard(
    title: String,
    course: String,
    due: String,
    accentColor: Color,
    isUrgent: Boolean
) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF27243A)
                )
                Text(
                    text = course,
                    fontSize = 12.sp,
                    color = Color(0xFF9A97B8),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isUrgent) Color(0xFFFFF1F1) else Color(0xFFF4F2FF))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = due,
                    fontSize = 11.sp,
                    color = if (isUrgent) Color(0xFFFF6B6B) else Color(0xFF6A5AE0)
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgress() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProgressRow(
            title = "Class Attendance",
            progress = 0.82f
        )
        ProgressRow(
            title = "Assignment Completion",
            progress = 0.58f
        )
        ProgressRow(
            title = "Mood Tracking",
            progress = 0.70f
        )
    }
}

@Composable
private fun ProgressRow(
    title: String,
    progress: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color(0xFF27243A)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 13.sp,
                color = Color(0xFF6A5AE0),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
        )
    }
}

@Composable
private fun MotivationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E8)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "“Success is the sum of small efforts repeated day in and day out.”",
                fontSize = 13.sp,
                color = Color(0xFF5F4A22)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep going, you're doing great! ✨",
                fontSize = 12.sp,
                color = Color(0xFF8C6C2F)
            )
        }
    }
}
