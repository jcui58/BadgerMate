package com.cs407.badgermate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cs407.badgermate.data.AppDatabase
import com.cs407.badgermate.data.User
import com.cs407.badgermate.data.profile.ProfileEntity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var roomDb: AppDatabase

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        roomDb = AppDatabase.getInstance(this)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // User have account, navigating to home page
        if (auth.currentUser != null) {
            syncProfileFromFirestore()
            navigateToMain()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (validateInput(email, password)) {
                loginOrRegister(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        val emailPattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
        if (!emailPattern.matches(email)) {
            emailEditText.error = "Please enter a valid email address."
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty."
            return false
        }
        if (password.length < 5) {
            passwordEditText.error = "Password length must be at least 5 characters."
            return false
        }
        if (!Regex("\\d+").containsMatchIn(password) ||
            !Regex("[a-z]+").containsMatchIn(password) ||
            !Regex("[A-Z]+").containsMatchIn(password)
        ) {
            passwordEditText.error = "Passwords must contain numbers, lowercase letters, and uppercase letters."
            return false
        }
        return true
    }

    // if user don't have account, auto sign in
    private fun loginOrRegister(email: String, password: String) {
        loginButton.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    syncProfileFromFirestore()
                    navigateToMain()
                } else {
                    registerUser(email, password)
                }
            }
    }

    // sign up
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loginButton.isEnabled = true

                if (task.isSuccessful) {
                    showNameInputDialog(email)
                } else {
                    val errorMessage = task.exception?.message ?: "Authentication Failed"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showNameInputDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_name_input, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)

        AlertDialog.Builder(this)
            .setTitle("Welcome to BadgerMate！")
            .setMessage("Please Enter Your Name")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Confirm") { _, _ ->
                val name = nameEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveUserProfile(email, name)
                } else {
                    Toast.makeText(this, "Name can not be empty", Toast.LENGTH_SHORT).show()
                    showNameInputDialog(email)
                }
            }
            .show()
    }


    private fun saveUserProfile(email: String, name: String) {
        val firebaseUser = auth.currentUser ?: return

        // Firestore user information
        val userData = hashMapOf(
            "id" to firebaseUser.uid,
            "name" to name,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                val profileEntity = ProfileEntity(
                    uid = firebaseUser.uid,
                    name = name,
                    email = email
                )
                roomDb.profileDao().saveProfile(profileEntity)

                Toast.makeText(this, "Registration successful. Welcome $name！", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user information:${e.message}", Toast.LENGTH_LONG).show()
                navigateToMain()
            }
    }

    // after login, loading data from firebase to local room
    private fun syncProfileFromFirestore() {
        val currentUser = auth.currentUser ?: return

        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val existingProfile =
                    roomDb.profileDao().getProfileForUid(currentUser.uid)

                val name = document.getString("name") ?: existingProfile?.name ?: ""
                val email = document.getString("email")
                    ?: existingProfile?.email
                    ?: currentUser.email
                    ?: ""

                val grade = document.getString("grade") ?: existingProfile?.grade ?: ""
                val major = document.getString("major") ?: existingProfile?.major ?: ""

                val heightFeet =
                    document.getString("heightFeet") ?: existingProfile?.heightFeet ?: ""
                val heightInches =
                    document.getString("heightInches") ?: existingProfile?.heightInches ?: ""
                val weight =
                    document.getString("weight") ?: existingProfile?.weight ?: ""
                val gender =
                    document.getString("gender") ?: existingProfile?.gender ?: ""
                val targetWeight =
                    document.getString("targetWeight") ?: existingProfile?.targetWeight ?: ""

                val profileEntity = ProfileEntity(
                    uid = currentUser.uid,
                    name = name,
                    email = email,
                    grade = grade,
                    major = major,
                    profileImage = existingProfile?.profileImage,
                    heightFeet = heightFeet,
                    heightInches = heightInches,
                    weight = weight,
                    gender = gender,
                    targetWeight = targetWeight
                )

                roomDb.profileDao().saveProfile(profileEntity)
            }
    }

    private fun navigateToMain() {
        loginButton.isEnabled = true
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
