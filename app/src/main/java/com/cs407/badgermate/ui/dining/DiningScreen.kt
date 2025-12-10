package com.cs407.badgermate.ui.dining

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.badgermate.ui.profile.ProfileViewModel

@Composable
fun DiningScreen(viewModel: DiningViewModel, profileViewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by profileViewModel.profile.observeAsState(null)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Text(
                text = "Dining Halls",
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // AI Recommendation Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Personalized Recommendation",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (userProfile != null) {
                            viewModel.generateMenuRecommendation(userProfile!!)
                        }
                    },
                    enabled = userProfile != null && !uiState.isLoadingRecommendation,
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(if (uiState.isLoadingRecommendation) "Generating..." else "Generate")
                }
            }
        }

        item {
            if (uiState.isLoadingRecommendation) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Generating personalized menu...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else if (uiState.recommendationError.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 14.sp,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.recommendationError,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else if (uiState.aiRecommendation.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.aiRecommendation,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray
            )
        }

        // Meal Recommendations
        item {
            Text(
                text = "Today's Meals",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            MealCard(uiState.lunch)
        }

        item {
            MealCard(uiState.dinner)
        }
    }
}

@Composable
private fun MealCard(meal: MealUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.title,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = meal.hall,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "${meal.matchPercent}% match",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Meal items
            meal.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.name, fontSize = 12.sp, color = Color.DarkGray)
                    Text(text = "${item.calories} cal", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: ${meal.totalCalories} calories",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.footerText,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

