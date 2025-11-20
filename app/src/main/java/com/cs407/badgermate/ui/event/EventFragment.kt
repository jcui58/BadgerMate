package com.cs407.badgermate.ui.event

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class EventFragment : Fragment() {

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

        fun TextView.subTitle() {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.parseColor("#F5F5FF"))
        }

        fun TextView.bodyText() {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.DKGRAY)
        }

        fun addSpace(parent: LinearLayout, h: Int) {
            val s = Space(context)
            s.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                h.dp()
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

        // =============== 顶部渐变区域（标题 + 搜索 + 统计卡片） ===============
        val headerGradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.parseColor("#FF5AA5"),
                Color.parseColor("#7B5CFF")
            )
        ).apply {
            cornerRadius = 24.dp().toFloat()
        }

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = headerGradient
            setPadding(16.dp(), 16.dp(), 16.dp(), 20.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val title = TextView(context).apply {
            text = "Campus Events ✨"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }
        header.addView(title)

        val subtitle = TextView(context).apply {
            text = "Discover and connect with campus life"
            subTitle()
        }
        addSpace(header, 4)
        header.addView(subtitle)

        addSpace(header, 16)

        // 搜索栏
        val searchBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.WHITE)
        }

        val searchRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = searchBg
            setPadding(12.dp(), 8.dp(), 12.dp(), 8.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        val searchIcon = TextView(context).apply {
            text = "🔍"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }

        val searchText = TextView(context).apply {
            text = "Search events..."
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#9999AA"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(8.dp(), 0, 8.dp(), 0) }
        }

        val filterBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.parseColor("#F5F0FF"))
        }

        val filterBtn = TextView(context).apply {
            text = "⚙️"
            background = filterBg
            setPadding(10.dp(), 6.dp(), 10.dp(), 6.dp())
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        searchRow.addView(searchIcon)
        searchRow.addView(searchText)
        searchRow.addView(filterBtn)
        header.addView(searchRow)

        addSpace(header, 14)

        // 三个统计卡片
        fun createStatCard(number: String, labelStr: String): View {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16.dp().toFloat()
                setColor(Color.WHITE)
            }
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = bg
                setPadding(12.dp(), 10.dp(), 12.dp(), 10.dp())
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(4.dp(), 0, 4.dp(), 0) }
                gravity = Gravity.CENTER_HORIZONTAL

                val num = TextView(context).apply {
                    text = number
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(Color.parseColor("#7B5CFF"))
                }
                val label = TextView(context).apply {
                    text = labelStr
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(Color.parseColor("#777799"))
                }
                addView(num)
                addView(label)
            }
        }

        val statsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        statsRow.addView(createStatCard("5", "Upcoming"))
        statsRow.addView(createStatCard("2", "Interested"))
        statsRow.addView(createStatCard("5", "Categories"))
        addSpace(header, 10)
        header.addView(statsRow)

        root.addView(header)

        // =============== 分段选择 All Events / My Events ===============
        addSpace(root, 16)

        val segmentBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.parseColor("#F3F3F7"))
        }

        val segment = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = segmentBg
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

        val allEventsTab = TextView(context).apply {
            text = "All Events"
            background = selectedBg
            setTypeface(typeface, Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#222244"))
            setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        val myEventsTab = TextView(context).apply {
            text = "My Events"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#666688"))
            setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        segment.addView(allEventsTab)
        segment.addView(myEventsTab)
        root.addView(segment)

        // =============== Category Chips ===============
        addSpace(root, 12)

        fun chip(text: String, selected: Boolean = false): View {
            val chipBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                if (selected) {
                    setColor(Color.parseColor("#7B5CFF"))
                } else {
                    setColor(Color.WHITE)
                    setStroke(1.dp(), Color.parseColor("#E0E0F0"))
                }
            }

            return TextView(context).apply {
                this.text = text
                setPadding(12.dp(), 6.dp(), 12.dp(), 6.dp())
                background = chipBg
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(
                    if (selected) Color.WHITE
                    else Color.parseColor("#444466")
                )
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(4.dp(), 0, 4.dp(), 0) }
            }
        }

        val chipScroll = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isHorizontalScrollBarEnabled = false
        }

        val chipRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        chipRow.addView(chip("All (5)", true))
        chipRow.addView(chip("Academic (1)"))
        chipRow.addView(chip("Arts (1)"))
        chipRow.addView(chip("Sports (1)"))
        chipRow.addView(chip("Career (2)"))

        chipScroll.addView(chipRow)
        root.addView(chipScroll)

        // =============== Event Card 工具函数 ===============
        fun createEventCard(
            gradientStart: String,
            gradientEnd: String,
            titleText: String,
            orgText: String,
            tagText: String,
            dateTime: String,
            location: String,
            attending: String,
            attendingRatio: Float,
            buttonText: String,
            buttonColor: String
        ): View {
            val cardBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20.dp().toFloat()
                setColor(Color.WHITE)
                setStroke(1.dp(), Color.parseColor("#E5E5EA"))
            }

            val card = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = cardBg
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 12.dp(), 0, 0)
                }
            }

            // 顶部渐变条
            val topGradient = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor(gradientStart),
                    Color.parseColor(gradientEnd)
                )
            ).apply {
                cornerRadii = floatArrayOf(
                    20.dp().toFloat(), 20.dp().toFloat(), // 左上右上
                    20.dp().toFloat(), 20.dp().toFloat(),
                    0f, 0f,
                    0f, 0f
                )
            }

            val headerPart = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = topGradient
                setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            }

            val headerRow = LinearLayout(context).apply {
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

            val title = TextView(context).apply {
                text = titleText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.WHITE)
            }
            val org = TextView(context).apply {
                text = orgText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#E6E9FF"))
            }
            titleCol.addView(title)
            titleCol.addView(org)

            val tagBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#F3F4FF"))
            }
            val tag = TextView(context).apply {
                text = tagText
                background = tagBg
                setPadding(10.dp(), 4.dp(), 10.dp(), 4.dp())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.parseColor("#3C3F88"))
            }

            headerRow.addView(titleCol)
            headerRow.addView(tag)
            headerPart.addView(headerRow)

            // 下面的白色详情部分
            val body = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            }

            fun infoRow(icon: String, text: String): View {
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                val i = TextView(context).apply {
                    this.text = icon
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
                val t = TextView(context).apply {
                    this.text = text
                    bodyText()
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply { setMargins(8.dp(), 0, 0, 0) }
                }
                row.addView(i)
                row.addView(t)
                return row
            }

            body.addView(infoRow("📅", dateTime))
            addSpace(body, 4)
            body.addView(infoRow("📍", location))
            addSpace(body, 4)

            // 参与人数 + 进度条
            val attendRow = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            val attendText = TextView(context).apply {
                text = attending
                bodyText()
            }
            attendRow.addView(attendText)
            addSpace(attendRow, 4)

            val outerBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#E5E5EA"))
            }
            val outer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                background = outerBg
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    6.dp()
                )
            }

            val ratio = attendingRatio.coerceIn(0f, 1f)

            val innerBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor("#22C55E"))
            }
            val inner = View(context).apply {
                background = innerBg
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ratio
                )
            }
            val rest = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1f - ratio
                )
            }
            outer.addView(inner)
            outer.addView(rest)
            attendRow.addView(outer)

            body.addView(attendRow)

            addSpace(body, 10)

            // 底部按钮行
            val buttonRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val mainBtnBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.parseColor(buttonColor))
            }
            val mainBtn = TextView(context).apply {
                text = "❤  $buttonText"
                background = mainBtnBg
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            val arrowBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 999f
                setColor(Color.WHITE)
                setStroke(1.dp(), Color.parseColor("#E5E5EA"))
            }
            val arrowBtn = TextView(context).apply {
                text = "›"
                background = arrowBg
                setPadding(14.dp(), 8.dp(), 14.dp(), 8.dp())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTextColor(Color.parseColor("#333333"))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8.dp(), 0, 0, 0) }
            }

            buttonRow.addView(mainBtn)
            buttonRow.addView(arrowBtn)
            body.addView(buttonRow)

            card.addView(headerPart)
            card.addView(body)
            return card
        }

        root.addView(
            createEventCard(
                gradientStart = "#0EA5E9",
                gradientEnd = "#1D4ED8",
                titleText = "Python Programming Workshop",
                orgText = "Computer Science Club",
                tagText = "Academic",
                dateTime = "Oct 15 • 6:00 PM - 8:00 PM",
                location = "CS Building 401",
                attending = "45/60 attending",
                attendingRatio = 45f / 60f,
                buttonText = "Interested",
                buttonColor = "#F43F5E"
            )
        )

        root.addView(
            createEventCard(
                gradientStart = "#F97316",
                gradientEnd = "#EC4899",
                titleText = "Fall Concert",
                orgText = "Music Society",
                tagText = "Arts",
                dateTime = "Oct 18 • 7:00 PM - 9:00 PM",
                location = "Student Center",
                attending = "230/300 attending",
                attendingRatio = 230f / 300f,
                buttonText = "Mark Interested",
                buttonColor = "#111827"
            )
        )

        return scroll
    }
}
