package com.example.collegeapp.ui.student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.firebase.FirebaseManager
import com.example.collegeapp.ui.auth.LoginActivity
import com.example.collegeapp.ui.student.adapters.EnrollmentsAdapter
import com.example.collegeapp.ui.student.models.CourseEnrollment
import kotlinx.coroutines.launch

/**
 * Dashboard activity for student users
 */
class StudentDashboardActivity : AppCompatActivity() {
    private val TAG = "StudentDashboardActivity"
    
    // UI Components
    private lateinit var welcomeTextView: TextView
    private lateinit var coursesRecyclerView: RecyclerView
    
    // Firebase
    private val firebaseManager = FirebaseManager.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)
        
        // Check if user is authenticated
        val currentUser = firebaseManager.currentUser
        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Initialize UI components
        welcomeTextView = findViewById(R.id.welcome_text_view)
        coursesRecyclerView = findViewById(R.id.courses_recycler_view)
        
        // Setup RecyclerView
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        
        // Set welcome message
        setWelcomeMessage()
        
        // Load enrolled courses
        loadEnrolledCourses()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.student_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                firebaseManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_profile -> {
                // Open student profile activity
                startActivity(Intent(this, StudentProfileActivity::class.java))
                true
            }
            R.id.action_request_teacher -> {
                // Open activity to request becoming a teacher
                startActivity(Intent(this, RequestTeacherRoleActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Set welcome message with student name
     */
    private fun setWelcomeMessage() {
        val userId = firebaseManager.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                val profileResult = firebaseManager.getUserProfile(userId)
                
                profileResult.fold(
                    onSuccess = { profile ->
                        welcomeTextView.text = "Welcome, ${profile.name}"
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to get student profile: ${e.message}")
                        welcomeTextView.text = "Welcome, Student"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting student profile: ${e.message}")
                welcomeTextView.text = "Welcome, Student"
            }
        }
    }
    
    /**
     * Load student's enrolled courses
     */
    private fun loadEnrolledCourses() {
        val studentId = firebaseManager.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                // In a real app, you'd fetch this from Firestore
                // For now, we'll create dummy enrollment data
                val enrollments = listOf(
                    CourseEnrollment(
                        courseId = "course1",
                        courseName = "Computer Science 101",
                        instructorName = "Dr. Smith",
                        grade = 85
                    ),
                    CourseEnrollment(
                        courseId = "course2",
                        courseName = "Mathematics 202",
                        instructorName = "Prof. Johnson",
                        grade = 92
                    ),
                    CourseEnrollment(
                        courseId = "course3",
                        courseName = "Physics 101",
                        instructorName = "Dr. Wilson",
                        grade = 78
                    )
                )
                
                // Create and set adapter
                val adapter = EnrollmentsAdapter(enrollments) { enrollment ->
                    // Handle course selection
                    val intent = Intent(this@StudentDashboardActivity, CourseDetailActivity::class.java)
                    intent.putExtra("COURSE_ID", enrollment.courseId)
                    intent.putExtra("COURSE_NAME", enrollment.courseName)
                    startActivity(intent)
                }
                
                coursesRecyclerView.adapter = adapter
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading enrolled courses: ${e.message}")
                showError("Failed to load courses: ${e.message}")
            }
        }
    }
    
    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}