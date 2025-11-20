package com.cs407.badgermate.ui.dining

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
fun DiningScreen(
    viewModel: DiningViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5FB))
            .verticalScroll(rememberScrollState())

            .padding(horizontal = 16.dp, vertical = 12.dp)

            .padding(top = 50.dp, bottom = 80.dp)
    ) {
        SmartDiningCard(
            completionPercent = uiState.completionPercent,
            dailyCalories = uiState.dailyCalories,
            dailyGoal = uiState.dailyGoalCalories,
            remaining = uiState.remainingCalories
        )

        Spacer(Modifier.height(12.dp))

        MacrosCard(
            protein = uiState.protein,
            carbs = uiState.carbs,
            fat = uiState.fat
        )

        Spacer(Modifier.height(16.dp))

        AIRecommendationsSection(
            lunch = uiState.lunch,
            dinner = uiState.dinner
        )

        Spacer(Modifier.height(16.dp))

        FullMenuSection(
            hall1Menu = uiState.hall1Menu,
            hall2Menu = uiState.hall2Menu
        )

        Spacer(Modifier.height(24.dp))
    }
}

/* ---------- Smart Dining 顶部卡片 ---------- */

@Composable
private fun SmartDiningCard(
    completionPercent: Int,
    dailyCalories: Int,
    dailyGoal: Int,
    remaining: Int
) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFFFF6A88),
            Color(0xFFFFC46A)
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
                    text = "Smart Dining",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AI-powered nutrition for your health goals",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // completion 小圆圈
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(80.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$completionPercent%",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Today",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "Daily Calories",
                                fontSize = 12.sp,
                                color = Color(0xFF999999)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$dailyCalories",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "of $dailyGoal cal",
                                fontSize = 11.sp,
                                color = Color(0xFF999999)
                            )

                            Spacer(Modifier.height(8.dp))

                            val progress = (completionPercent / 100f).coerceIn(0f, 1f)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFF1F1F1))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progress)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0xFFFF6A88))
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "$remaining cal remaining",
                                fontSize = 11.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Today's Macros ---------- */

@Composable
private fun MacrosCard(
    protein: Int,
    carbs: Int,
    fat: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Macros",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                MacroItem(label = "Protein", value = "${protein}g")
                MacroItem(label = "Carbs", value = "${carbs}g")
                MacroItem(label = "Fat", value = "${fat}g")
            }
        }
    }
}

@Composable
private fun MacroItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF7F7FA))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF777777)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE0E0E5))
        )
    }
}

/* ---------- AI Recommendations ---------- */

@Composable
private fun AIRecommendationsSection(
    lunch: MealUi,
    dinner: MealUi
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AI Recommendations",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(Modifier.width(8.dp))

            AssistChip(
                onClick = {},
                label = { Text("Smart", fontSize = 11.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFFEAF2FF)
                )
            )

            Spacer(Modifier.width(6.dp))

            AssistChip(
                onClick = {},
                label = { Text("Optimized", fontSize = 11.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFFEAFBF1)
                )
            )
        }

        Spacer(Modifier.height(10.dp))

        MealCard(meal = lunch)

        Spacer(Modifier.height(12.dp))

        MealCard(meal = dinner)
    }
}

@Composable
private fun MealCard(meal: MealUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF2E5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = meal.title.first().uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFF8A3C)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = meal.hall,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text("${meal.matchPercent}% Match", fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFEAFBF1)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            meal.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color(0xFFFF8A3C),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${item.calories}",
                        fontSize = 12.sp,
                        color = Color(0xFFFF8A3C)
                    )
                    Text(
                        text = " cal",
                        fontSize = 11.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total Calories",
                    fontSize = 12.sp,
                    color = Color(0xFF777777),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${meal.totalCalories}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF6A3C)
                )
                Text(
                    text = " cal",
                    fontSize = 11.sp,
                    color = Color(0xFFAAAAAA)
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = meal.footerText,
                fontSize = 11.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

/* ---------- Full Menu ---------- */

@Composable
private fun FullMenuSection(
    hall1Menu: List<Dish>,
    hall2Menu: List<Dish>
) {
    var selectedHallIndex by remember { mutableStateOf(0) }

    Column {
        Text(
            text = "Full Menu",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFF0F0F3))
                .padding(4.dp)
        ) {
            SegmentButton(
                text = "Dining Hall #1",
                selected = selectedHallIndex == 0,
                modifier = Modifier.weight(1f)
            ) { selectedHallIndex = 0 }

            SegmentButton(
                text = "Dining Hall #2",
                selected = selectedHallIndex == 1,
                modifier = Modifier.weight(1f)
            ) { selectedHallIndex = 1 }
        }

        Spacer(Modifier.height(10.dp))

        val dishes = if (selectedHallIndex == 0) hall1Menu else hall2Menu

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(8.dp)) {
                dishes.forEach { dish ->
                    DishRow(dish)
                }
            }
        }
    }
}


@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) Color.White else Color.Transparent
    val contentColor = if (selected) Color(0xFF333333) else Color(0xFF888888)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor
        )
    }
}

@Composable
private fun DishRow(dish: Dish) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dish.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(2.dp))

                Row {
                    dish.tags.forEachIndexed { index, tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFF3F3F7)
                            )
                        )
                        if (index != dish.tags.lastIndex) Spacer(Modifier.width(4.dp))
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color(0xFFFF8A3C),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${dish.calories}",
                    fontSize = 13.sp,
                    color = Color(0xFFFF8A3C),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " cal",
                    fontSize = 11.sp,
                    color = Color(0xFFAAAAAA)
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = Color(0xFFF0F0F3),
            thickness = 1.dp
        )
    }
}
