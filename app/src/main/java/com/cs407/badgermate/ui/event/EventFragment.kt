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
import androidx.fragment.app.viewModels
import com.cs407.badgermate.data.event.EventEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventFragment : Fragment() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val context = requireContext()
        val dm = resources.displayMetrics

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

        lateinit var upcomingTv: TextView
        lateinit var interestedTv: TextView

        var searchQuery = ""

        var refreshList: (() -> Unit)? = null

        // 根布局
        val scroll = ScrollView(context).apply { isFillViewport = true }
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp(), 40.dp(), 16.dp(), 96.dp())
        }
        scroll.addView(root)

        // Header 渐变背景
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

        // 搜索栏
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

        // 顶部统计：只剩 Upcoming & Interested 两个卡片
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
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(6.dp(), 0, 6.dp(), 0)
                    }

                addView(numTv)
                addView(TextView(context).apply {
                    text = label
                    setTextColor(Color.parseColor("#777799"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                })
            }
        }

        val statsRow = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
        statsRow.addView(statCard("Upcoming") { upcomingTv = it })
        statsRow.addView(statCard("Interested") { interestedTv = it })
        header.addView(statsRow)
        root.addView(header)

        // Add Event / Sync 按钮
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

        addSpace(root, 12)

        // 列表容器
        val listContainer = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        root.addView(listContainer)

        // 更新统计
        fun updateStats(allEvents: List<EventEntity>) {
            val (up, interested) = viewModel.calcStats(allEvents)
            upcomingTv.text = up.toString()
            interestedTv.text = interested.toString()
        }

        // 构建卡片（不再显示 category tag）
        fun buildCard(e: EventEntity): View {
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

            // 统一用一个渐变色 header
            val top = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#0EA5E9"),
                    Color.parseColor("#7B5CFF")
                )
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

            headerPart.addView(titleView)
            headerPart.addView(orgView)
            card.addView(headerPart)

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
                viewModel.toggleInterested(e)
            }

            body.addView(btn)
            card.addView(body)
            return card
        }

        // 刷新列表：只按搜索过滤
        fun doRefresh() {
            val all = viewModel.events.value ?: emptyList()
            updateStats(all)

            var list = all

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

        refreshList = { doRefresh() }

        // 搜索监听
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                refreshList?.invoke()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 监听 ViewModel 列表变化
        viewModel.events.observe(viewLifecycleOwner) {
            refreshList?.invoke()
        }

        refreshList?.invoke()

        // Add Event 对话框（不再选择 category）
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
                        category = "General",   // 固定一个占位分类，不再展示
                        startTime = start,
                        endTime = end,
                        displayTime = display,
                        location = location,
                        isMyEvent = false
                    )

                    viewModel.addEvent(newEvent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 点击：手动添加
        addEventBtn.setOnClickListener { showAddDialog() }
        // 长按：从官网同步 .ics
        addEventBtn.setOnLongClickListener {
            viewModel.syncFromIcs()
            Toast.makeText(context, "Syncing events from UW calendar…", Toast.LENGTH_SHORT).show()
            true
        }

        // Fragment 首次创建时自动同步一次
        viewModel.syncFromIcs()

        return scroll
    }
}
