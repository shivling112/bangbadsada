package com.example.collegeapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collegeapp.R
import com.example.collegeapp.firebase.FirebaseManager
import com.example.collegeapp.firebase.UserType
import com.example.collegeapp.ui.student.StudentDashboardActivity
import kotlinx.coroutines.launch

/**
 * Activity for user registration
 */
class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"
    
    // UI Components
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var progressBar: ProgressBar
    
    // Firebase
    private val firebaseManager = FirebaseManager.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Initialize UI components
        nameEditText = findViewById(R.id.name_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        registerButton = findViewById(R.id.register_button)
        loginTextView = findViewById(R.id.login_text_view)
        progressBar = findViewById(R.id.progress_bar)
        
        // Set click listeners
        registerButton.setOnClickListener {
            register()
        }
        
        loginTextView.setOnClickListener {
            // Navigate back to login activity
            finish()
        }
    }
    
    /**
     * Handle registration process
     */
    private fun register() {
        // Validate input
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            nameEditText.requestFocus()
            return
        }
        
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            passwordEditText.requestFocus()
            return
        }
        
        if (password.length < 6) {
            passwordEditText.error = "Password should be at least 6 characters"
            passwordEditText.requestFocus()
            return
        }
        
        if (confirmPassword.isEmpty() || confirmPassword != password) {
            confirmPasswordEditText.error = "Passwords do not match"
            confirmPasswordEditText.requestFocus()
            return
        }
        
        // Show progress
        showProgress(true)
        
        // Register with Firebase
        lifecycleScope.launch {
            try {
                val result = firebaseManager.registerUser(email, password)
                
                result.fold(
                    onSuccess = { user ->
                        Log.d(TAG, "Registration successful for user: ${user.uid}")
                        
                        // Create user profile in Firestore
                        val profileResult = firebaseManager.createUserProfile(
                            userId = user.uid,
                            name = name,
                            email = email,
                            userType = UserType.STUDENT // Default role is student
                        )
                        
                        profileResult.fold(
                            onSuccess = {
                                Log.d(TAG, "User profile created for: ${user.uid}")
                                // Navigate to student dashboard
                                val intent = Intent(this@RegisterActivity, StudentDashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            },
                            onFailure = { e ->
                                Log.e(TAG, "Failed to create user profile: ${e.message}")
                                showProgress(false)
                                showError("Registration successful but failed to create profile. Please log in.")
                                // Navigate back to login
                                finish()
                            }
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Registration failed: ${e.message}")
                        showProgress(false)
                        showError("Registration failed: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during registration: ${e.message}")
                showProgress(false)
                showError("An error occurred: ${e.message}")
            }
        }
    }
    
    /**
     * Show or hide progress bar
     */
    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        registerButton.isEnabled = !show
    }
    
    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}