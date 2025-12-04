package com.cs407.badgermate.ui.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.cs407.badgermate.data.AppDatabase
import com.cs407.badgermate.data.event.EventEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val context = requireContext()
        val dm = resources.displayMetrics

        // ----------- 小工具函数 -----------
        fun Int.dp(): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), dm).toInt()

        fun TextView.subTitle() {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.parseColor("#F5F5FF"))
        }

        fun TextView.bodyText() {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.DKGRAY)
        }

        fun addSpace(parent: LinearLayout, h: Int) {
            parent.addView(Space(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    h.dp()
                )
            })
        }

        // ----------- DB & 全局状态 -----------
        val db = AppDatabase.getInstance(requireContext())
        val eventDao = db.eventDao()

        lateinit var upcomingTv: TextView
        lateinit var interestedTv: TextView
        lateinit var categoriesTv: TextView

        var currentFilter = "All"
        var searchQuery = ""

        // 刷新列表的函数变量（解决函数互相依赖的问题）
        var refreshList: (() -> Unit)? = null

        // ----------- 根布局 -----------
        val scroll = ScrollView(context).apply {
            isFillViewport = true
        }
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 40.dp(), 16.dp(), 96.dp())
        }
        scroll.addView(root)

        // ----------- Header 区域 -----------
        val headerBg = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.parseColor("#FF5AA5"), Color.parseColor("#7B5CFF"))
        ).apply { cornerRadius = 24.dp().toFloat() }

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = headerBg
            setPadding(16.dp(), 16.dp(), 16.dp(), 20.dp())
        }

        header.addView(TextView(context).apply {
            text = "Campus Events ✨"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
        })

        header.addView(TextView(context).apply {
            text = "Discover and connect with campus life"
            subTitle()
        })

        addSpace(header, 16)

        // ----------- 搜索栏 -----------
        val searchRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable().apply {
                cornerRadius = 999f
                setColor(Color.WHITE)
            }
            setPadding(12.dp(), 8.dp(), 12.dp(), 8.dp())
            gravity = Gravity.CENTER_VERTICAL
        }

        val searchInput = EditText(context).apply {
            hint = "Search events..."
            setTextColor(Color.parseColor("#333344"))
            setHintTextColor(Color.parseColor("#9999AA"))
            background = null
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(8.dp(), 0, 8.dp(), 0) }
        }

        searchRow.addView(TextView(context).apply {
            text = "🔍"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        })
        searchRow.addView(searchInput)
        header.addView(searchRow)

        addSpace(header, 12)

        // ----------- 顶部统计卡片 -----------
        fun statCard(label: String, assignRef: (TextView) -> Unit): View {
            val numTv = TextView(context).apply {
                text = "0"
                setTextColor(Color.parseColor("#7B5CFF"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTypeface(typeface, Typeface.BOLD)
            }
            assignRef(numTv)

            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = GradientDrawable().apply {
                    cornerRadius = 16.dp().toFloat()
                    setColor(Color.WHITE)
                }
                gravity = Gravity.CENTER
                setPadding(12.dp(), 10.dp(), 12.dp(), 10.dp())
                layoutParams =
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

                addView(numTv)
                addView(TextView(context).apply {
                    text = label
                    setTextColor(Color.parseColor("#777799"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                })
            }
        }

        val statsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        statsRow.addView(statCard("Upcoming") { upcomingTv = it })
        statsRow.addView(statCard("Interested") { interestedTv = it })
        statsRow.addView(statCard("Categories") { categoriesTv = it })

        header.addView(statsRow)
        root.addView(header)

        // ----------- Add Event 按钮 -----------
        addSpace(root, 12)

        val addEventBtn = TextView(context).apply {
            text = "➕  Add Event"
            gravity = Gravity.CENTER
            setPadding(16.dp(), 10.dp(), 16.dp(), 10.dp())
            background = GradientDrawable().apply {
                cornerRadius = 999f
                setColor(Color.parseColor("#7B5CFF"))
            }
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
        }
        root.addView(addEventBtn)

        // ----------- Category chips -----------
        val chips = mutableMapOf<String, TextView>()

        fun updateChipStyles() {
            chips.forEach { (label, tv) ->
                val selected = (label == currentFilter)
                tv.background = GradientDrawable().apply {
                    cornerRadius = 999f
                    if (selected) {
                        setColor(Color.parseColor("#7B5CFF"))
                    } else {
                        setColor(Color.WHITE)
                        setStroke(1.dp(), Color.parseColor("#E0E0F0"))
                    }
                }
                tv.setTextColor(
                    if (selected) Color.WHITE
                    else Color.parseColor("#444466")
                )
            }
        }

        fun makeChip(label: String): TextView =
            TextView(context).apply {
                text = label
                setPadding(12.dp(), 6.dp(), 12.dp(), 6.dp())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setOnClickListener {
                    currentFilter = label
                    updateChipStyles()
                    refreshList?.invoke()
                }
            }.also { chips[label] = it }

        val chipRow = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("All", "Academic", "Arts", "Sports", "Career").forEach {
            chipRow.addView(makeChip(it))
        }
        root.addView(HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
            addView(chipRow)
        })

        addSpace(root, 8)

        // ----------- 列表容器 -----------
        val listContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        root.addView(listContainer)

        // ----------- 渐变色 -----------
        fun gradientFor(category: String): Pair<String, String> =
            when (category.lowercase()) {
                "academic" -> "#0EA5E9" to "#1D4ED8"
                "arts" -> "#F97316" to "#EC4899"
                "sports" -> "#22C55E" to "#16A34A"
                "career" -> "#6366F1" to "#8B5CF6"
                else -> "#0EA5E9" to "#7B5CFF"
            }

        // ----------- 更新顶部统计 -----------
        fun updateStats(list: List<EventEntity>) {
            val now = System.currentTimeMillis()
            upcomingTv.text = list.count { it.startTime > now }.toString()
            interestedTv.text = list.count { it.isMyEvent }.toString()
            categoriesTv.text =
                list.map { it.category.lowercase().trim() }.distinct().count().toString()
        }

        // ----------- 构建单个 Event 卡片（只依赖 refreshList 变量） -----------
        fun buildCard(e: EventEntity): View {
            val (c1, c2) = gradientFor(e.category)

            val cardBg = GradientDrawable().apply {
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
                ).apply { setMargins(0, 12.dp(), 0, 0) }
            }

            val top = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor(c1), Color.parseColor(c2))
            ).apply {
                cornerRadii = floatArrayOf(
                    20.dp().toFloat(), 20.dp().toFloat(),
                    20.dp().toFloat(), 20.dp().toFloat(),
                    0f, 0f, 0f, 0f
                )
            }

            val headerPart = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = top
                setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            }

            val titleView = TextView(context).apply {
                text = e.title
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                setTypeface(typeface, Typeface.BOLD)
            }
            val orgView = TextView(context).apply {
                text = e.organization
                setTextColor(Color.parseColor("#E6E9FF"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            }

            val tag = TextView(context).apply {
                text = e.category
                background = GradientDrawable().apply {
                    cornerRadius = 999f
                    setColor(Color.parseColor("#F3F4FF"))
                }
                setPadding(10.dp(), 4.dp(), 10.dp(), 4.dp())
            }

            val headerRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            headerRow.addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams =
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(titleView)
                addView(orgView)
            })
            headerRow.addView(tag)
            headerPart.addView(headerRow)
            card.addView(headerPart)

            // body
            val body = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16.dp(), 12.dp(), 16.dp(), 12.dp())
            }

            fun info(icon: String, text: String): View =
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    addView(TextView(context).apply { this.text = icon })
                    addView(TextView(context).apply {
                        this.text = text
                        bodyText()
                        setPadding(8.dp(), 0, 0, 0)
                    })
                }

            body.addView(info("📅", e.displayTime))
            addSpace(body, 4)
            body.addView(info("📍", e.location))
            addSpace(body, 8)

            // Interested 按钮
            fun buttonColor(isInterested: Boolean) =
                if (isInterested) "#F43F5E" else "#111827"

            fun buttonText(isInterested: Boolean) =
                if (isInterested) "Interested" else "Mark Interested"

            val btnBg = GradientDrawable().apply {
                cornerRadius = 999f
                setColor(Color.parseColor(buttonColor(e.isMyEvent)))
            }

            val btn = TextView(context).apply {
                text = "❤  ${buttonText(e.isMyEvent)}"
                background = btnBg
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(16.dp(), 10.dp(), 16.dp(), 10.dp())
            }

            btn.setOnClickListener {
                val newFlag = !e.isMyEvent
                eventDao.updateInterested(e.id, newFlag)
                e.isMyEvent = newFlag
                refreshList?.invoke()
            }

            body.addView(btn)
            card.addView(body)
            return card
        }

        // ----------- 真正的刷新逻辑（本地函数），再赋给 refreshList 变量 -----------
        fun doRefresh() {
            var list = eventDao.getAllEvents()

            // 更新统计
            updateStats(list)

            // category filter
            if (currentFilter != "All") {
                list = list.filter { it.category.equals(currentFilter, ignoreCase = true) }
            }

            // search filter
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) ||
                            it.organization.lowercase().contains(q) ||
                            it.location.lowercase().contains(q)
                }
            }

            listContainer.removeAllViews()
            list.forEach { listContainer.addView(buildCard(it)) }
        }

        // 赋值给变量，供其他地方调用
        refreshList = { doRefresh() }

        // ----------- 监听 Search -----------
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                refreshList?.invoke()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ----------- 初始状态 -----------
        currentFilter = "All"
        updateChipStyles()
        refreshList?.invoke()

        // ----------- Add Event Dialog -----------
        fun showAddDialog() {
            val dialogCtx = requireContext()
            val layout = LinearLayout(dialogCtx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20.dp(), 10.dp(), 20.dp(), 0)
            }

            fun field(label: String, placeholder: String): EditText {
                layout.addView(TextView(dialogCtx).apply {
                    text = label
                    setTextColor(Color.parseColor("#555555"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                })
                val ed = EditText(dialogCtx).apply {
                    hint = placeholder
                    inputType = InputType.TYPE_CLASS_TEXT
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
                layout.addView(ed)
                addSpace(layout, 8)
                return ed
            }

            val titleEd = field("Title", "Fall Hackathon")
            val orgEd = field("Organization", "CS Department")

            layout.addView(TextView(dialogCtx).apply {
                text = "Category"
                setTextColor(Color.parseColor("#555555"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            })

            val categories = listOf("Academic", "Arts", "Sports", "Career")
            val categorySpinner = Spinner(dialogCtx).apply {
                adapter = ArrayAdapter(
                    dialogCtx,
                    android.R.layout.simple_spinner_dropdown_item,
                    categories
                )
            }
            layout.addView(categorySpinner)
            addSpace(layout, 8)

            // 日期 & 时间
            val cal = Calendar.getInstance()
            val displayFormatter = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault())

            val dateTimeTv = TextView(dialogCtx).apply {
                text = "Select date & time"
                setTextColor(Color.parseColor("#333333"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(0, 12.dp(), 0, 12.dp())
            }
            layout.addView(dateTimeTv)
            addSpace(layout, 8)

            dateTimeTv.setOnClickListener {
                DatePickerDialog(
                    dialogCtx,
                    { _, y, m, d ->
                        cal.set(y, m, d)
                        TimePickerDialog(
                            dialogCtx,
                            { _, h, min ->
                                cal.set(Calendar.HOUR_OF_DAY, h)
                                cal.set(Calendar.MINUTE, min)
                                dateTimeTv.text = displayFormatter.format(cal.time)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            val locationEd = field("Location", "Engineering Hall 101")

            AlertDialog.Builder(dialogCtx)
                .setTitle("Add Event")
                .setView(layout)
                .setPositiveButton("Save") { _, _ ->
                    val title = titleEd.text.toString().ifBlank { "New Event" }
                    val org = orgEd.text.toString().ifBlank { "Unknown Org" }
                    val category =
                        (categorySpinner.selectedItem as? String)?.trim().orEmpty()
                            .ifBlank { "Other" }
                    val display = dateTimeTv.text.toString().ifBlank { "TBD" }
                    val location = locationEd.text.toString().ifBlank { "TBD" }

                    val start = cal.timeInMillis
                    val end = Calendar.getInstance().apply {
                        timeInMillis = start
                        add(Calendar.HOUR_OF_DAY, 1)
                    }.timeInMillis

                    val newEvent = EventEntity(
                        title = title,
                        organization = org,
                        category = category,
                        startTime = start,
                        endTime = end,
                        displayTime = display,
                        location = location,
                        isMyEvent = false
                    )

                    eventDao.insertEvent(newEvent)
                    refreshList?.invoke()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        addEventBtn.setOnClickListener { showAddDialog() }

        return scroll
    }
}
