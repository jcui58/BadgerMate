package com.cs407.badgermate.ui.dining

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import androidx.fragment.app.Fragment

class DiningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val dm = resources.displayMetrics

        fun Int.dp(): Int =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                dm
            ).toInt()

        fun TextView.subtitleStyle() {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.DKGRAY)
        }

        fun addSpace(parent: LinearLayout, hDp: Int) {
            val s = Space(context)
            s.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                hDp.dp()
            )
            parent.addView(s)
        }

        val scroll = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 40.dp(), 16.dp(), 96.dp())
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(root)

        // ================= Today’s Nutrition 卡片（带进度条） =================
        val nutritionBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16.dp().toFloat()
            setColor(Color.WHITE)
            setStroke(1.dp(), Color.parseColor("#E5E5E5"))
        }

        val nutritionCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = nutritionBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dp())
            }
        }

        val nutTitle = TextView(context).apply {
            text = "Today's Nutrition"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        nutritionCard.addView(nutTitle)
        addSpace(nutritionCard, 12)

        // 创建营养项（带进度条）
        fun createNutritionItem(
            emoji: String,
            label: String,
            consumed: Int,
            total: Int,
            accentColor: Int
        ): View {
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(0, 0, 12.dp(), 0)
                }
            }

            val topRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val leftRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val iconTv = TextView(context).apply {
                text = emoji
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }

            val labelTv = TextView(context).apply {
                text = "  $label"
                subtitleStyle()
            }

            leftRow.addView(iconTv)
            leftRow.addView(labelTv)

            val valueTv = TextView(context).apply {
                text = "$consumed / $total"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(accentColor)
            }

            topRow.addView(leftRow)
            topRow.addView(valueTv)
            container.addView(topRow)

            addSpace(container, 6)

            // 下面的进度条
            val outerBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#D8D8DD"))    // 灰色背景
            }

            val outerBar = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                background = outerBg
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    6.dp()
                )
            }

            val ratio = (consumed.toFloat() / total.toFloat()).coerceIn(0f, 1f)

            val innerBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#05031A"))    // 深色已完成部分
            }

            val innerWidthWeight = if (ratio <= 0f) 0f else ratio

            val innerBar = View(context).apply {
                background = innerBg
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    innerWidthWeight
                )
            }

            val restBar = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1f - innerWidthWeight
                )
            }

            outerBar.addView(innerBar)
            outerBar.addView(restBar)

            container.addView(outerBar)

            return container
        }

        // 两列：左列 Calories / Carbs，右列 Protein / Fat
        val row1 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val caloriesItem = createNutritionItem("🔥", "Calories", 950, 2200, Color.parseColor("#FF6B3D"))
        val proteinItem  = createNutritionItem("📈", "Protein", 75, 150, Color.parseColor("#FF3B5E"))

        row1.addView(caloriesItem)
        row1.addView(proteinItem)
        nutritionCard.addView(row1)

        addSpace(nutritionCard, 12)

        val row2 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val carbsItem = createNutritionItem("🍎", "Carbs", 95, 250, Color.parseColor("#FFB000"))
        val fatItem   = createNutritionItem("💙", "Fat", 22, 65, Color.parseColor("#3B82F6"))

        row2.addView(carbsItem)
        row2.addView(fatItem)
        nutritionCard.addView(row2)

        root.addView(nutritionCard)

        // ================= AI Recommended Meals =================
        val aiTitle = TextView(context).apply {
            text = "AI Recommended Meals"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        root.addView(aiTitle)
        addSpace(root, 8)

        fun createMealCard(
            mealName: String,
            hallName: String,
            match: String,
            items: List<Pair<String, String>>,
            totalCalories: String,
            note: String
        ): View {
            val cardBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16.dp().toFloat()
                setColor(Color.parseColor("#FDF5FF"))
                setStroke(1.dp(), Color.parseColor("#E0C7FF"))
            }

            val card = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = cardBg
                setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(4.dp(), 0, 4.dp(), 12.dp())
                }
            }

            val header = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val titleCol = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val mealTitle = TextView(context).apply {
                text = mealName
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }
            val hall = TextView(context).apply {
                text = hallName
                subtitleStyle()
            }
            titleCol.addView(mealTitle)
            titleCol.addView(hall)

            val matchBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#32C27D"))
            }

            val badge = TextView(context).apply {
                text = match
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setPadding(12.dp(), 4.dp(), 12.dp(), 4.dp())
                background = matchBg
            }

            header.addView(titleCol)
            header.addView(badge)
            card.addView(header)
            addSpace(card, 8)

            for ((name, cal) in items) {
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                val n = TextView(context).apply {
                    text = name
                    subtitleStyle()
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }
                val c = TextView(context).apply {
                    text = cal
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    setTextColor(Color.BLACK)
                }
                row.addView(n)
                row.addView(c)
                card.addView(row)
                addSpace(card, 4)
            }

            val total = TextView(context).apply {
                text = "Total Calories   $totalCalories"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }
            addSpace(card, 4)
            card.addView(total)

            val noteTv = TextView(context).apply {
                text = note
                subtitleStyle()
            }
            card.addView(noteTv)

            return card
        }

        root.addView(
            createMealCard(
                "Lunch",
                "Dining Hall #1",
                "95% Match",
                listOf(
                    "Grilled Chicken Breast" to "220 cal",
                    "Brown Rice" to "220 cal",
                    "Broccoli & Mushroom Stir-fry" to "75 cal"
                ),
                "510 cal",
                "High protein, low fat - matches your fitness goals"
            )
        )

        root.addView(
            createMealCard(
                "Dinner",
                "Dining Hall #2",
                "88% Match",
                listOf(
                    "Steamed Sea Bass" to "180 cal",
                    "Quinoa Salad" to "220 cal",
                    "Spinach Soup" to "40 cal"
                ),
                "440 cal",
                "Rich in Omega-3, supports recovery"
            )
        )

        // ================= Today's Menu 卡片（Figma 风格） =================
        addSpace(root, 16)

        val menuCardBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 18.dp().toFloat()
            setColor(Color.WHITE)
            setStroke(1.dp(), Color.parseColor("#E5E5EA"))
        }

        val menuCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = menuCardBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val menuTitle = TextView(context).apply {
            text = "Today's Menu"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        menuCard.addView(menuTitle)
        addSpace(menuCard, 12)

        // 顶部 Dining Hall #1 / #2 切换条（静态外观）
        val toggleBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.parseColor("#F2F2F7"))      // 浅灰背景
        }

        val toggleContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = toggleBg
            setPadding(4.dp(), 4.dp(), 4.dp(), 4.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val selectedBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.WHITE)
        }

        val hall1 = TextView(context).apply {
            text = "Dining Hall #1"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#222222"))
            background = selectedBg
            setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        val hall2 = TextView(context).apply {
            text = "Dining Hall #2"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#555555"))
            setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        toggleContainer.addView(hall1)
        toggleContainer.addView(hall2)
        menuCard.addView(toggleContainer)

        addSpace(menuCard, 16)

        // 菜品标签 pill
        fun createTagPill(text: String): View {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#F3F3F5"))
            }

            return TextView(context).apply {
                this.text = text
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.parseColor("#444444"))
                background = bg
                setPadding(10.dp(), 4.dp(), 10.dp(), 4.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 8.dp(), 0)
                }
            }
        }

        // 单个菜品行
        fun addDishToMenu(
            name: String,
            tags: List<String>,
            calories: String
        ) {
            val dishContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 8.dp(), 0, 8.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val topRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val nameTv = TextView(context).apply {
                text = name
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val calTv = TextView(context).apply {
                text = "💧  $calories"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.parseColor("#777777"))
            }

            topRow.addView(nameTv)
            topRow.addView(calTv)
            dishContainer.addView(topRow)

            val tagRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            tags.forEach { tag ->
                tagRow.addView(createTagPill(tag))
            }

            addSpace(dishContainer, 4)
            dishContainer.addView(tagRow)

            menuCard.addView(dishContainer)
        }

        addDishToMenu(
            "Kung Pao Chicken",
            listOf("Spicy", "Protein"),
            "450"
        )
        addDishToMenu(
            "Braised Pork Belly",
            listOf("High Calorie"),
            "580"
        )
        addDishToMenu(
            "Tomato & Egg",
            listOf("Vegetarian", "Light"),
            "220"
        )
        addDishToMenu(
            "Mapo Tofu",
            listOf("Spicy", "Vegetarian"),
            "280"
        )

        root.addView(menuCard)

        return scroll
    }
}
