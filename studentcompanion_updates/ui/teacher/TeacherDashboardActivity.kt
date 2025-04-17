package com.example.collegeapp.ui.teacher

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
import com.example.collegeapp.ui.teacher.adapters.StudentsAdapter
import com.example.collegeapp.ui.teacher.models.Course
import com.example.collegeapp.ui.teacher.models.StudentData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

/**
 * Dashboard activity for teacher users
 */
class TeacherDashboardActivity : AppCompatActivity() {
    private val TAG = "TeacherDashboardActivity"
    
    // UI Components
    private lateinit var welcomeTextView: TextView
    private lateinit var courseTitleTextView: TextView
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var addAssignmentFab: FloatingActionButton
    
    // Adapters
    private lateinit var studentsAdapter: StudentsAdapter
    
    // Firebase
    private val firebaseManager = FirebaseManager.getInstance()
    
    // Data
    private var students = mutableListOf<StudentData>()
    private var currentCourse: Course? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)
        
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
        courseTitleTextView = findViewById(R.id.course_title_text_view)
        studentsRecyclerView = findViewById(R.id.students_recycler_view)
        addAssignmentFab = findViewById(R.id.add_assignment_fab)
        
        // Setup RecyclerView
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        studentsAdapter = StudentsAdapter(students, this::onStudentSelected)
        studentsRecyclerView.adapter = studentsAdapter
        
        // Set welcome message
        setWelcomeMessage()
        
        // Load teacher's courses
        loadTeacherCourses()
        
        // Set click listeners
        addAssignmentFab.setOnClickListener {
            currentCourse?.let {
                // Navigate to add assignment activity
                val intent = Intent(this, AddAssignmentActivity::class.java)
                intent.putExtra("COURSE_ID", it.id)
                intent.putExtra("COURSE_NAME", it.name)
                startActivity(intent)
            } ?: run {
                showMessage("Please select a course first")
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.teacher_menu, menu)
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
            R.id.action_course_management -> {
                // Open course management activity
                startActivity(Intent(this, CourseManagementActivity::class.java))
                true
            }
            R.id.action_view_grades -> {
                // Open grades overview activity
                startActivity(Intent(this, GradesOverviewActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Set welcome message with teacher name
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
                        Log.e(TAG, "Failed to get teacher profile: ${e.message}")
                        welcomeTextView.text = "Welcome, Teacher"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting teacher profile: ${e.message}")
                welcomeTextView.text = "Welcome, Teacher"
            }
        }
    }
    
    /**
     * Load teacher's courses from Firestore
     */
    private fun loadTeacherCourses() {
        val teacherId = firebaseManager.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                // In a real app, you'd fetch this from Firestore
                // For now, we'll create a dummy course
                currentCourse = Course(
                    id = "course1",
                    name = "Computer Science 101",
                    description = "Introduction to Computer Science",
                    teacherId = teacherId
                )
                
                courseTitleTextView.text = currentCourse?.name ?: "No Course Selected"
                
                // Load students for this course
                loadStudentsForCourse(currentCourse?.id ?: "")
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading teacher courses: ${e.message}")
                showError("Failed to load courses: ${e.message}")
            }
        }
    }
    
    /**
     * Load students enrolled in a specific course
     */
    private fun loadStudentsForCourse(courseId: String) {
        lifecycleScope.launch {
            try {
                // In a real app, you'd fetch this from Firestore
                // For now, we'll create dummy student data
                students.clear()
                students.addAll(listOf(
                    StudentData("student1", "John Doe", "john.doe@email.com", 85),
                    StudentData("student2", "Jane Smith", "jane.smith@email.com", 92),
                    StudentData("student3", "Bob Johnson", "bob.johnson@email.com", 78)
                ))
                
                studentsAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading students: ${e.message}")
                showError("Failed to load students: ${e.message}")
            }
        }
    }
    
    /**
     * Handle student selection
     */
    private fun onStudentSelected(student: StudentData) {
        // Navigate to student detail activity
        val intent = Intent(this, StudentDetailActivity::class.java)
        intent.putExtra("STUDENT_ID", student.id)
        intent.putExtra("STUDENT_NAME", student.name)
        intent.putExtra("COURSE_ID", currentCourse?.id)
        intent.putExtra("COURSE_NAME", currentCourse?.name)
        startActivity(intent)
    }
    
    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Show success message
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}