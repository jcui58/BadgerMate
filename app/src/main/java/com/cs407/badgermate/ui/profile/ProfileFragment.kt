package com.cs407.badgermate.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.badgermate.LoginActivity
import com.cs407.badgermate.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        if (auth.currentUser == null) {
            navigateToLogin()
            return null
        }

        val context = requireContext()
        val dm = resources.displayMetrics

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

        fun addSpace(parent: LinearLayout, heightDp: Int) {
            val s = Space(context)
            s.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                heightDp.dp()
            )
            parent.addView(s)
        }

        // ScrollView
        val scroll = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            }
        }
        scroll.addView(root)

        // Profile Card
        val profileCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16.dp(), 20.dp(), 16.dp(), 20.dp())
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#7C4DFF"),
                    Color.parseColor("#E91E63")
                )
            ).apply { cornerRadius = 24.dp().toFloat() }
        }
        root.addView(profileCard)

        val headerRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        profileCard.addView(headerRow)

        val avatarView = TextView(context).apply {
            text = "U"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#7C4DFF"))
            gravity = Gravity.CENTER
            val size = 56.dp()
            layoutParams = LinearLayout.LayoutParams(size, size)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
        }
        headerRow.addView(avatarView)

        val headerTextCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(16.dp(), 0, 0, 0) }
        }
        headerRow.addView(headerTextCol)

        val nameText = TextView(context).apply {
            text = "User"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }
        headerTextCol.addView(nameText)

        val emailText = TextView(context).apply {
            text = auth.currentUser?.email ?: ""
            subTitle()
        }
        headerTextCol.addView(emailText)


        val majorGradeText = TextView(context).apply {
            text = ""
            subTitle()
        }
        headerTextCol.addView(majorGradeText)

        addSpace(profileCard, 16)


        // Edit Profile
        addSpace(root, 16)
        val editProfileButton = Button(context).apply {
            text = "Edit Profile"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 16.dp().toFloat()
                setColor(Color.parseColor("#7C4DFF"))
            }


            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(editProfileButton)

        // Personal Information
        addSpace(root, 16)

        val personalCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16.dp(), 16.dp(), 16.dp(), 16.dp())
            background = GradientDrawable().apply {
                cornerRadius = 20.dp().toFloat()
                setColor(Color.WHITE)
            }
        }
        root.addView(personalCard)

        val personalTitleRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        personalCard.addView(personalTitleRow)

        val personalIcon = TextView(context).apply {
            text = "\uD83D\uDD8A"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
        personalTitleRow.addView(personalIcon)

        val personalTitle = TextView(context).apply {
            text = "Personal Information"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#333366"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8.dp(), 0, 0, 0) }
        }
        personalTitleRow.addView(personalTitle)

        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dp()
            ).apply { setMargins(0, 12.dp(), 0, 12.dp()) }
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }
        personalCard.addView(divider)

        fun createInfoRow(icon: String, label: String): Pair<LinearLayout, TextView> {
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 4.dp(), 0, 4.dp()) }
            }

            val iconView = TextView(context).apply {
                text = icon
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }
            row.addView(iconView)

            val col = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(8.dp(), 0, 0, 0) }
            }
            row.addView(col)

            val labelView = TextView(context).apply {
                text = label
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(Color.parseColor("#555555"))
            }
            col.addView(labelView)

            val valueView = TextView(context).apply {
                text = "Not set"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.parseColor("#333333"))
            }
            col.addView(valueView)

            return row to valueView
        }

        val (heightRow, heightValue) = createInfoRow("\uD83D\uDCC8", "Height")
        val (weightRow, weightValue) = createInfoRow("\u26F9", "Weight")
        val (genderRow, genderValue) = createInfoRow("\uD83D\uDC64", "Gender")
        val (targetRow, targetValue) = createInfoRow("\uD83C\uDFAF", "Target Weight")

        personalCard.addView(heightRow)
        personalCard.addView(weightRow)
        personalCard.addView(genderRow)
        personalCard.addView(targetRow)

        addSpace(personalCard, 12)

        val editPersonalButton = Button(context).apply {
            text = "Edit Personal Information"
            setTextColor(Color.WHITE)

            background = GradientDrawable().apply {
                cornerRadius = 16.dp().toFloat()
                setColor(Color.parseColor("#7C4DFF"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        personalCard.addView(editPersonalButton)

        //  Log Out
        addSpace(root, 16)
        val logoutButton = Button(context).apply {
            text = "Log Out"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 16.dp().toFloat()
                setColor(Color.parseColor("#F44336"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(logoutButton)

        //
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile ?: return@observe

            val displayName = if (profile.name.isNotBlank()) profile.name else "User"
            nameText.text = displayName
            avatarView.text = getInitials(displayName)

            emailText.text =
                if (profile.email.isNotBlank()) profile.email
                else auth.currentUser?.email ?: ""

            val mg = buildString {
                if (profile.major.isNotBlank()) append(profile.major)
                if (profile.major.isNotBlank() && profile.grade.isNotBlank()) append(" • ")
                if (profile.grade.isNotBlank()) append(profile.grade)
            }
            majorGradeText.text = mg

            fun formatHeight(feet: String?, inches: String?): String {
                val f = feet?.takeIf { it.isNotBlank() }
                val i = inches?.takeIf { it.isNotBlank() }
                return if (f != null || i != null) {
                    "${f ?: "0"}' ${i ?: "0"}\""
                } else "Not set"
            }

            heightValue.text = formatHeight(profile.heightFeet, profile.heightInches)
            weightValue.text =
                if (profile.weight.isNotBlank()) "${profile.weight} lbs" else "Not set"
            genderValue.text =
                if (profile.gender.isNotBlank()) profile.gender else "Not set"
            targetValue.text =
                if (profile.targetWeight.isNotBlank()) "${profile.targetWeight} lbs" else "Not set"
        }

        // 点击事件
        editProfileButton.setOnClickListener { showEditProfileDialog() }
        editPersonalButton.setOnClickListener { showPersonalInfoDialog() }

        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out") { _, _ ->
                    auth.signOut()
                    viewModel.clearAccountInfo()
                    navigateToLogin()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return scroll
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim()
            .split(" ", "　", "\t", "\n")
            .filter { it.isNotBlank() }

        return when {
            parts.size >= 2 ->
                "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
            parts.size == 1 && parts[0].isNotEmpty() ->
                if (parts[0].length >= 2) parts[0].take(2).uppercase()
                else parts[0].first().uppercaseChar().toString()
            else -> "U"
        }
    }

    // edit Profile
    private fun showEditProfileDialog() {
        val profile = viewModel.profile.value

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 20)
        }

        val nameInput = EditText(requireContext()).apply {
            hint = "Name"
            setText(profile?.name ?: "")
        }
        val majorInput = EditText(requireContext()).apply {
            hint = "Major"
            setText(profile?.major ?: "")
        }
        val gradeInput = EditText(requireContext()).apply {
            hint = "Grade (e.g., Freshman)"
            setText(profile?.grade ?: "")
        }

        layout.addView(nameInput)
        layout.addView(majorInput)
        layout.addView(gradeInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val major = majorInput.text.toString().trim()
                val grade = gradeInput.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.updateNameGradeMajor(name, grade, major)
                Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ====== Personal Information 弹窗 ======
    private fun showPersonalInfoDialog() {
        val profile = viewModel.profile.value ?: return

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val genderLabel = TextView(requireContext()).apply {
            text = "Gender"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
        }
        layout.addView(genderLabel)

        val genderGroup = RadioGroup(requireContext()).apply {
            orientation = RadioGroup.HORIZONTAL
        }

        val maleRadio = RadioButton(requireContext()).apply {
            text = "Male"
            id = View.generateViewId()
        }
        val femaleRadio = RadioButton(requireContext()).apply {
            text = "Female"
            id = View.generateViewId()
        }
        val otherRadio = RadioButton(requireContext()).apply {
            text = "Other"
            id = View.generateViewId()
        }

        genderGroup.addView(maleRadio)
        genderGroup.addView(femaleRadio)
        genderGroup.addView(otherRadio)

        when (profile.gender) {
            "Male" -> genderGroup.check(maleRadio.id)
            "Female" -> genderGroup.check(femaleRadio.id)
            "Other" -> genderGroup.check(otherRadio.id)
        }

        layout.addView(genderGroup)

        val space1 = Space(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 20
            )
        }
        layout.addView(space1)

        val heightLabel = TextView(requireContext()).apply {
            text = "Height"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
        }
        layout.addView(heightLabel)

        val heightRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val heightFeetEdit = EditText(requireContext()).apply {
            hint = "Feet"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(profile.heightFeet)
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(0, 0, 10, 0) }
        }
        val heightInchesEdit = EditText(requireContext()).apply {
            hint = "Inches"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(profile.heightInches)
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(10, 0, 0, 0) }
        }

        heightRow.addView(heightFeetEdit)
        heightRow.addView(heightInchesEdit)
        layout.addView(heightRow)

        val weightEdit = EditText(requireContext()).apply {
            hint = "Weight (lbs)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(profile.weight)
        }
        layout.addView(weightEdit)

        val targetWeightEdit = EditText(requireContext()).apply {
            hint = "Target Weight (lbs)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(profile.targetWeight)
        }
        layout.addView(targetWeightEdit)

        AlertDialog.Builder(requireContext())
            .setTitle("Personal Information")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val gender = when (genderGroup.checkedRadioButtonId) {
                    maleRadio.id -> "Male"
                    femaleRadio.id -> "Female"
                    otherRadio.id -> "Other"
                    else -> ""
                }
                val heightFeet = heightFeetEdit.text.toString().trim()
                val heightInches = heightInchesEdit.text.toString().trim()
                val weight = weightEdit.text.toString().trim()
                val targetWeight = targetWeightEdit.text.toString().trim()

                viewModel.updatePersonalInfo(
                    heightFeet,
                    heightInches,
                    weight,
                    gender,
                    targetWeight
                )
                Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
