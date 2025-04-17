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
import com.example.collegeapp.ui.admin.AdminDashboardActivity
import com.example.collegeapp.ui.student.StudentDashboardActivity
import com.example.collegeapp.ui.teacher.TeacherDashboardActivity
import kotlinx.coroutines.launch

/**
 * Activity for user login
 */
class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    
    // UI Components
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var progressBar: ProgressBar
    
    // Firebase
    private val firebaseManager = FirebaseManager.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Initialize UI components
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerTextView = findViewById(R.id.register_text_view)
        progressBar = findViewById(R.id.progress_bar)
        
        // Check if user is already logged in
        val currentUser = firebaseManager.currentUser
        if (currentUser != null) {
            // User is already logged in, redirect to appropriate dashboard
            redirectToAppropriateScreen()
            return
        }
        
        // Set click listeners
        loginButton.setOnClickListener {
            login()
        }
        
        registerTextView.setOnClickListener {
            // Navigate to register activity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    /**
     * Handle login process
     */
    private fun login() {
        // Validate input
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
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
        
        // Show progress
        showProgress(true)
        
        // Login with Firebase
        lifecycleScope.launch {
            try {
                val result = firebaseManager.loginUser(email, password)
                
                result.fold(
                    onSuccess = {
                        Log.d(TAG, "Login successful for user: ${it.uid}")
                        // Redirect to appropriate dashboard based on user role
                        redirectToAppropriateScreen()
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Login failed: ${e.message}")
                        showProgress(false)
                        showError("Login failed: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login: ${e.message}")
                showProgress(false)
                showError("An error occurred: ${e.message}")
            }
        }
    }
    
    /**
     * Redirect to appropriate screen based on user role
     */
    private fun redirectToAppropriateScreen() {
        val userId = firebaseManager.currentUser?.uid ?: return
        
        showProgress(true)
        
        lifecycleScope.launch {
            try {
                val result = firebaseManager.getUserProfile(userId)
                
                result.fold(
                    onSuccess = { profile ->
                        val intent = when (profile.userType) {
                            UserType.ADMIN -> Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                            UserType.TEACHER -> Intent(this@LoginActivity, TeacherDashboardActivity::class.java)
                            UserType.STUDENT -> Intent(this@LoginActivity, StudentDashboardActivity::class.java)
                        }
                        
                        startActivity(intent)
                        finish() // Close LoginActivity
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to get user profile: ${e.message}")
                        // If we can't get the user profile, default to student dashboard
                        startActivity(Intent(this@LoginActivity, StudentDashboardActivity::class.java))
                        finish() // Close LoginActivity
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting user profile: ${e.message}")
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
        loginButton.isEnabled = !show
    }
    
    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}