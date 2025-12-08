package com.cs407.badgermate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cs407.badgermate.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Check if user is already logged in
        if (auth.currentUser != null) {
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
        // Email validation
        val emailPattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
        if (!emailPattern.matches(email)) {
            emailEditText.error = "Please enter a valid email address."
            return false
        }

        // Password validation
        if (password.isEmpty()) {
            passwordEditText.error = "The password cannot be left blank."
            return false
        }

        if (password.length < 5) {
            passwordEditText.error = "The password must be at least 5 characters long."
            return false
        }

        // Check if password contains digit, lowercase, and uppercase
        if (!Regex("\\d+").containsMatchIn(password) ||
            !Regex("[a-z]+").containsMatchIn(password) ||
            !Regex("[A-Z]+").containsMatchIn(password)) {
            passwordEditText.error = "The password must contain numbers, lowercase letters and uppercase letters."
            return false
        }

        return true
    }

    private fun loginOrRegister(email: String, password: String) {
        // Disable button during operation
        loginButton.isEnabled = false

        // Try to sign in first
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    // If sign in fails, try to create a new account
                    registerUser(email, password)
                }
            }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loginButton.isEnabled = true

                if (task.isSuccessful) {
                    // Registration success - show name input dialog
                    showNameInputDialog(email)
                } else {
                    // If registration fails, show error
                    val errorMessage = task.exception?.message ?: "Authentication Failure"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showNameInputDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_name_input, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Welcome to BadgerMate！")
            .setMessage("Please Enter Your Name")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Confirm") { _, _ ->
                val name = nameEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveUserToFirestore(email, name)
                } else {
                    Toast.makeText(this, "Name can not be empty", Toast.LENGTH_SHORT).show()
                    showNameInputDialog(email) // Show dialog again
                }
            }
            .create()

        dialog.show()
    }

    private fun saveUserToFirestore(email: String, name: String) {
        val firebaseUser = auth.currentUser ?: return

        val user = User(
            id = firebaseUser.uid,
            name = name,
            email = email,
            profileImage = null,
            createdAt = System.currentTimeMillis()
        )

        db.collection("users")
            .document(firebaseUser.uid)
            .set(user.toMap())
            .addOnSuccessListener {
                Toast.makeText(this, "Registration successful. Welcome $name！", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user information：${e.message}", Toast.LENGTH_LONG).show()
                navigateToMain() // Still navigate even if Firestore fails
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}