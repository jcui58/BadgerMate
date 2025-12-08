package com.cs407.badgermate.ui.profile

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import android.content.Intent
import com.cs407.badgermate.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val dm = resources.displayMetrics

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fun Int.dp(): Int =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                dm
            ).toInt()

        fun TextView.subTitle(color: String = "#F5F5FF") {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.parseColor(color))
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

        val headerGrad = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.parseColor("#7B5CFF"),
                Color.parseColor("#FF5AA5")
            )
        ).apply { cornerRadius = 24.dp().toFloat() }

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = headerGrad
            setPadding(16.dp(), 24.dp(), 16.dp(), 20.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // 头像圆圈
        val avatarBg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        val avatar = TextView(context).apply {
            text = "..."  // Will be updated after loading
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#7B5CFF"))
            background = avatarBg
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                72.dp(),
                72.dp()
            )
        }
        header.addView(avatar)

        addSpace(header, 8)

        val name = TextView(context).apply {
            text = "Loading..."  // Will be updated after loading
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }
        header.addView(name)

        val userEmail = TextView(context).apply {
            text = auth.currentUser?.email ?: "Logout"
            subTitle()
        }
        header.addView(userEmail)


        // Load user data from Firestore
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name") ?: "User"

                        // Update name TextView
                        name.text = userName

                        // Update avatar with initials
                        val initials = getInitials(userName)
                        avatar.text = initials
                    } else {
                        // Fallback to email
                        name.text = currentUser.email ?: "User"
                        avatar.text = getInitials(currentUser.email ?: "U")
                    }
                }
                .addOnFailureListener {
                    // Fallback to email
                    name.text = currentUser.email ?: "User"
                    avatar.text = getInitials(currentUser.email ?: "U")
                }
        }

        addSpace(header, 16)

        fun statCard(icon: String, value: String, label: String): View {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 18.dp().toFloat()
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

                val topRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }
                val i = TextView(context).apply {
                    text = icon
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                }
                topRow.addView(i)
                addView(topRow)

                val v = TextView(context).apply {
                    text = value
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(Color.parseColor("#333366"))
                }
                addView(v)

                val l = TextView(context).apply {
                    text = label
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(Color.parseColor("#777799"))
                }
                addView(l)
            }
        }

        val statsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        statsRow.addView(statCard("📚", "92%", "Attendance"))
        statsRow.addView(statCard("⚡", "45.2K", "Steps"))
        statsRow.addView(statCard("🏆", "12", "Events"))

        header.addView(statsRow)
        root.addView(header)

        addSpace(root, 16)

        val editBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.parseColor("#8B5CFF"))
        }

        val editBtn = TextView(context).apply {
            text = "👤  Edit Profile"
            background = editBg
            setPadding(16.dp(), 10.dp(), 16.dp(), 10.dp())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(editBtn)

        fun whiteCard(): LinearLayout {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 18.dp().toFloat()
                setColor(Color.WHITE)
                setStroke(1.dp(), Color.parseColor("#E5E5EA"))
            }
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = bg
                setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 12.dp(), 0, 0) }
            }
        }

        fun chevron(): TextView = TextView(context).apply {
            text = "›"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.parseColor("#999999"))
        }

        val notificationCard = whiteCard()

        val notifTitle = TextView(context).apply {
            text = "Notifications"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#333366"))
        }
        notificationCard.addView(notifTitle)
        addSpace(notificationCard, 12)

        fun notifRow(icon: String, title: String, desc: String, enabled: Boolean): View {
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 6.dp(), 0, 6.dp()) }
                gravity = Gravity.CENTER_VERTICAL
            }

            val iconBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12.dp().toFloat()
                setColor(Color.parseColor("#F3F4FF"))
            }
            val iconView = TextView(context).apply {
                text = icon
                background = iconBg
                setPadding(10.dp(), 8.dp(), 10.dp(), 8.dp())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val textCol = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(12.dp(), 0, 0, 0) }
            }

            val t = TextView(context).apply {
                text = title
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.parseColor("#222244"))
            }
            val d = TextView(context).apply {
                text = desc
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTextColor(Color.parseColor("#777799"))
            }
            textCol.addView(t)
            textCol.addView(d)

            val sw = Switch(context).apply {
                isChecked = enabled
            }

            row.addView(iconView)
            row.addView(textCol)
            row.addView(sw)
            return row
        }

        notificationCard.addView(
            notifRow(
                "📚",
                "Class Reminders",
                "Get notified before class",
                true
            )
        )
        notificationCard.addView(
            notifRow(
                "🍱",
                "Meal Suggestions",
                "AI meal recommendations",
                false
            )
        )
        notificationCard.addView(
            notifRow(
                "🎫",
                "Event Updates",
                "Updates for followed events",
                true
            )
        )

        root.addView(notificationCard)

        val prefCard = whiteCard()

        val prefTitle = TextView(context).apply {
            text = "Preferences"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#333366"))
        }
        prefCard.addView(prefTitle)
        addSpace(prefCard, 8)

        fun simpleRow(icon: String, title: String): View {
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8.dp(), 0, 8.dp()) }
                gravity = Gravity.CENTER_VERTICAL
            }

            val iconBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12.dp().toFloat()
                setColor(Color.parseColor("#F3F4FF"))
            }
            val iconView = TextView(context).apply {
                text = icon
                background = iconBg
                setPadding(10.dp(), 8.dp(), 10.dp(), 8.dp())
            }

            val t = TextView(context).apply {
                text = title
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.parseColor("#222244"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(12.dp(), 0, 0, 0) }
            }

            row.addView(iconView)
            row.addView(t)
            row.addView(chevron())
            return row
        }

        prefCard.addView(simpleRow("🔔", "Notifications"))
        prefCard.addView(simpleRow("👤", "Personal Information"))
        prefCard.addView(simpleRow("🎯", "Health Goals"))

        root.addView(prefCard)

        val accountCard = whiteCard()

        val accountTitle = TextView(context).apply {
            text = "Account"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#333366"))
        }
        accountCard.addView(accountTitle)
        addSpace(accountCard, 8)

        accountCard.addView(simpleRow("🛡️", "Privacy & Security"))
        accountCard.addView(simpleRow("❓", "Help & Feedback"))

        root.addView(accountCard)

        addSpace(root, 16)

        val logoutBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(Color.parseColor("#FFF6F6"))
            setStroke(1.dp(), Color.parseColor("#F97373"))
        }

        val logout = TextView(context).apply {
            text = "↩  Log Out"
            background = logoutBg
            setTextColor(Color.parseColor("#EF4444"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(16.dp(), 10.dp(), 16.dp(), 10.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Sign out and return to login
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        root.addView(logout)

        addSpace(root, 12)

        val footer = TextView(context).apply {
            text = "BadgerMate v1.0.0\nMade with ❤ for students"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#9CA3AF"))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        root.addView(footer)

        return scroll
    }

    // Helper function to get initials from name
    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> {
                // First and last name
                "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
            }
            parts.size == 1 && parts[0].isNotEmpty() -> {
                // Single name, take first two characters or just one
                if (parts[0].length >= 2) {
                    parts[0].take(2).uppercase()
                } else {
                    parts[0].first().uppercaseChar().toString()
                }
            }
            else -> "U"  // Default
        }
    }
}