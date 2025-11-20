package com.cs407.badgermate.ui.bus

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

class BusFragment : Fragment() {

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

        // 顶部多留一点空隙，底部多一点避免被 bottom bar 遮住
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 40.dp(), 16.dp(), 96.dp())
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(root)

        // ================== Time to Leave 卡片 ==================
        val timeBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 18.dp().toFloat()
            setColor(Color.parseColor("#FFF9EE"))
            setStroke(2.dp(), Color.parseColor("#FFA44A"))
        }

        val timeCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = timeBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dp())
            }
        }

        // 第一行：铃铛 + “Time to Leave!”
        val titleRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val bell = TextView(context).apply {
            text = "🔔"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }

        val timeTitle = TextView(context).apply {
            text = "  Time to Leave!"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        titleRow.addView(bell)
        titleRow.addView(timeTitle)
        timeCard.addView(titleRow)

        addSpace(timeCard, 8)

        // 第二行：Your class / 课程名
        val classRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val classLeft = TextView(context).apply {
            text = "Your class"
            subtitleStyle()
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val classRight = TextView(context).apply {
            text = "Data Structures & Algorithms"
            subtitleStyle()
            textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        classRow.addView(classLeft)
        classRow.addView(classRight)
        timeCard.addView(classRow)

        val startsText = TextView(context).apply {
            text = "starts in 15 minutes"
            subtitleStyle()
        }
        addSpace(timeCard, 4)
        timeCard.addView(startsText)

        addSpace(timeCard, 12)

        // 推荐路线 + 按钮
        val bottomRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val leftCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val recommended = TextView(context).apply {
            text = "🚌  Recommended: Route 80"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.BLACK)
        }
        val depart = TextView(context).apply {
            text = "⏰  Depart: 9:30"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.BLACK)
        }

        leftCol.addView(recommended)
        leftCol.addView(depart)

        val buttonBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.BLACK)
        }

        val viewButton = TextView(context).apply {
            text = "View Route"
            setPadding(20.dp(), 10.dp(), 20.dp(), 10.dp())
            background = buttonBg
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }

        bottomRow.addView(leftCol)
        bottomRow.addView(viewButton)
        timeCard.addView(bottomRow)

        root.addView(timeCard)

        // ================== Real-Time Arrivals 标题 ==================
        val arrivalTitle = TextView(context).apply {
            text = "Real-Time Arrivals"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        root.addView(arrivalTitle)
        addSpace(root, 8)

        // ================== Route 卡片 ==================
        fun createRouteCard(
            routeName: String,
            destination: String,
            eta: String,
            next: String,
            status: String,
            iconColor: Int
        ): View {

            // 卡片背景：白色 + 浅灰描边 + 圆角
            val cardBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 18.dp().toFloat()
                setColor(Color.WHITE)
                setStroke(1.dp(), Color.parseColor("#E5E5EA"))
            }

            val card = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = cardBg
                setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 12.dp())
                }
            }

            // 顶部：图标 + 文本 + 状态
            val topRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // 左侧彩色 icon 区
            val iconBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12.dp().toFloat()
                setColor(iconColor)
            }

            val iconContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = iconBg
                setPadding(12.dp(), 10.dp(), 12.dp(), 10.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 12.dp(), 0)
                }
            }

            val iconText = TextView(context).apply {
                text = "🚌"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTextColor(Color.WHITE)
            }
            iconContainer.addView(iconText)

            // 中间文字
            val left = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val nameTv = TextView(context).apply {
                text = routeName
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }
            val destTv = TextView(context).apply {
                text = destination
                subtitleStyle()
            }
            left.addView(nameTv)
            left.addView(destTv)

            // 右侧状态 pill
            val statusBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.BLACK)
            }

            val statusTv = TextView(context).apply {
                text = status
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.WHITE)
                setPadding(12.dp(), 4.dp(), 12.dp(), 4.dp())
                background = statusBg
            }

            topRow.addView(iconContainer)
            topRow.addView(left)
            topRow.addView(statusTv)
            card.addView(topRow)

            addSpace(card, 8)

            // 底部：时间
            val bottomRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            val etaTv = TextView(context).apply {
                text = "⏱  $eta"
                subtitleStyle()
            }
            val nextTv = TextView(context).apply {
                text = "   Next: $next"
                subtitleStyle()
            }
            bottomRow.addView(etaTv)
            bottomRow.addView(nextTv)

            card.addView(bottomRow)
            return card
        }

        root.apply {
            addView(
                createRouteCard(
                    "Route 80",
                    "CS Building",
                    "3 min",
                    "15 min",
                    "Arriving",
                    Color.parseColor("#2864FF")   // 蓝色
                )
            )
            addView(
                createRouteCard(
                    "Route 81",
                    "Library",
                    "7 min",
                    "22 min",
                    "On Time",
                    Color.parseColor("#00C853")   // 绿色
                )
            )
            addView(
                createRouteCard(
                    "Route 82",
                    "Sports Center",
                    "12 min",
                    "27 min",
                    "On Time",
                    Color.parseColor("#8E24AA")   // 紫色
                )
            )
        }

        // ================== Saved Routes 卡片 ==================
        addSpace(root, 16)

        val savedCardBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 18.dp().toFloat()
            setColor(Color.WHITE)
            setStroke(1.dp(), Color.parseColor("#E5E5EA"))
        }

        val savedCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = savedCardBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dp())
            }
        }

        val savedTitle = TextView(context).apply {
            text = "Saved Routes"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        savedCard.addView(savedTitle)
        addSpace(savedCard, 12)

        // 单条 Saved Route 行
        fun createSavedRouteRow(
            title: String,
            fromTo: String,
            routeTag: String,
            timeInfo: String
        ): View {
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // 第一行：纸飞机 + 标题
            val top = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val icon = TextView(context).apply {
                text = "✈️"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 8.dp(), 0) }
            }
            val titleTv = TextView(context).apply {
                text = title
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }
            top.addView(icon)
            top.addView(titleTv)
            container.addView(top)

            // 第二行：起止地点
            val pathTv = TextView(context).apply {
                text = fromTo
                subtitleStyle()
            }
            addSpace(container, 4)
            container.addView(pathTv)

            // 第三行：Route pill + 时间
            val bottom = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val tagBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#F3F3F5"))
            }

            val tagTv = TextView(context).apply {
                text = routeTag
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.BLACK)
                setPadding(10.dp(), 4.dp(), 10.dp(), 4.dp())
                background = tagBg
            }

            val timeTv = TextView(context).apply {
                text = "   $timeInfo"
                subtitleStyle()
            }

            bottom.addView(tagTv)
            bottom.addView(timeTv)

            addSpace(container, 6)
            container.addView(bottom)

            return container
        }

        // 第一条
        savedCard.addView(
            createSavedRouteRow(
                "To Class",
                "Dorm Building A  →  CS Building",
                "Route 80",
                "8 min"
            )
        )

        // 分割线
        val divider = View(context).apply {
            setBackgroundColor(Color.parseColor("#E5E5EA"))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.dp()
            ).apply {
                setMargins(0, 12.dp(), 0, 12.dp())
            }
        }
        savedCard.addView(divider)

        // 第二条
        savedCard.addView(
            createSavedRouteRow(
                "Back to Dorm",
                "Library  →  Dorm Building A",
                "Route 81",
                "12 min"
            )
        )

        root.addView(savedCard)

        // ================== Campus Bus Map 卡片 ==================
        val mapCardBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 18.dp().toFloat()
            setColor(Color.WHITE)
            setStroke(1.dp(), Color.parseColor("#E5E5EA"))
        }

        val mapCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = mapCardBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val mapTitle = TextView(context).apply {
            text = "Campus Bus Map"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        mapCard.addView(mapTitle)
        addSpace(mapCard, 12)

        // 灰色 map preview 区域
        val mapPreviewBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16.dp().toFloat()
            setColor(Color.parseColor("#F3F3F6"))
        }

        val mapPreview = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = mapPreviewBg
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                140.dp()
            )
            gravity = android.view.Gravity.CENTER
        }

        val mapText = TextView(context).apply {
            text = "📍\nReal-time Map View\nShows all bus current locations"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#666666"))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        mapPreview.addView(mapText)
        mapCard.addView(mapPreview)

        addSpace(mapCard, 12)

        // “Open Full Map” 按钮
        val openBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.WHITE)
            setStroke(1.dp(), Color.parseColor("#E5E5EA"))
        }

        val openBtn = TextView(context).apply {
            text = "Open Full Map"
            background = openBg
            setPadding(16.dp(), 10.dp(), 16.dp(), 10.dp())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#333333"))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mapCard.addView(openBtn)

        root.addView(mapCard)

        return scroll
    }
}
