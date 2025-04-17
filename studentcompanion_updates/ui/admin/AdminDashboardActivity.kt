package com.example.collegeapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.firebase.FirebaseManager
import com.example.collegeapp.firebase.RoleRequest
import com.example.collegeapp.ui.admin.adapters.TeacherRequestsAdapter
import com.example.collegeapp.ui.auth.LoginActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

/**
 * Dashboard activity for admin users
 */
class AdminDashboardActivity : AppCompatActivity() {
    private val TAG = "AdminDashboardActivity"
    
    // UI Components
    private lateinit var welcomeTextView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var noRequestsTextView: TextView
    
    // Adapters
    private lateinit var teacherRequestsAdapter: TeacherRequestsAdapter
    
    // Firebase
    private val firebaseManager = FirebaseManager.getInstance()
    
    // Data
    private var teacherRequests = mutableListOf<RoleRequest>()
    private var adminRequests = mutableListOf<RoleRequest>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        
        // Check if user is authenticated and is admin
        val currentUser = firebaseManager.currentUser
        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Initialize UI components
        welcomeTextView = findViewById(R.id.welcome_text_view)
        tabLayout = findViewById(R.id.tab_layout)
        requestsRecyclerView = findViewById(R.id.requests_recycler_view)
        noRequestsTextView = findViewById(R.id.no_requests_text_view)
        
        // Setup RecyclerView
        requestsRecyclerView.layoutManager = LinearLayoutManager(this)
        teacherRequestsAdapter = TeacherRequestsAdapter(teacherRequests, this::processTeacherRequest)
        requestsRecyclerView.adapter = teacherRequestsAdapter
        
        // Set welcome message
        setWelcomeMessage()
        
        // Setup tab layout
        setupTabLayout()
        
        // Load teacher requests by default
        loadTeacherRequests()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
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
            R.id.action_manage_users -> {
                // Open manage users activity
                startActivity(Intent(this, ManageUsersActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Set welcome message with admin name
     */
    private fun setWelcomeMessage() {
        val userId = firebaseManager.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                val profileResult = firebaseManager.getUserProfile(userId)
                
                profileResult.fold(
                    onSuccess = { profile ->
                        welcomeTextView.text = "Welcome, Admin ${profile.name}"
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to get admin profile: ${e.message}")
                        welcomeTextView.text = "Welcome, Admin"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting admin profile: ${e.message}")
                welcomeTextView.text = "Welcome, Admin"
            }
        }
    }
    
    /**
     * Setup tab layout for teacher and admin requests
     */
    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Teacher Requests"))
        tabLayout.addTab(tabLayout.newTab().setText("Admin Requests"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> loadTeacherRequests()
                    1 -> loadAdminRequests()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    /**
     * Load teacher role requests
     */
    private fun loadTeacherRequests() {
        lifecycleScope.launch {
            try {
                val result = firebaseManager.getAllTeacherRequests()
                
                result.fold(
                    onSuccess = { requests ->
                        teacherRequests.clear()
                        teacherRequests.addAll(requests)
                        teacherRequestsAdapter.notifyDataSetChanged()
                        
                        if (teacherRequests.isEmpty()) {
                            noRequestsTextView.visibility = View.VISIBLE
                            requestsRecyclerView.visibility = View.GONE
                            noRequestsTextView.text = "No teacher requests found"
                        } else {
                            noRequestsTextView.visibility = View.GONE
                            requestsRecyclerView.visibility = View.VISIBLE
                        }
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to load teacher requests: ${e.message}")
                        showError("Failed to load teacher requests: ${e.message}")
                        noRequestsTextView.visibility = View.VISIBLE
                        requestsRecyclerView.visibility = View.GONE
                        noRequestsTextView.text = "Error loading teacher requests"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading teacher requests: ${e.message}")
                showError("An error occurred: ${e.message}")
            }
        }
    }
    
    /**
     * Load admin role requests
     */
    private fun loadAdminRequests() {
        lifecycleScope.launch {
            try {
                val result = firebaseManager.getAllAdminRequests()
                
                result.fold(
                    onSuccess = { requests ->
                        adminRequests.clear()
                        adminRequests.addAll(requests)
                        
                        // Use a different adapter for admin requests
                        val adminRequestsAdapter = TeacherRequestsAdapter(adminRequests, this@AdminDashboardActivity::processAdminRequest)
                        requestsRecyclerView.adapter = adminRequestsAdapter
                        
                        if (adminRequests.isEmpty()) {
                            noRequestsTextView.visibility = View.VISIBLE
                            requestsRecyclerView.visibility = View.GONE
                            noRequestsTextView.text = "No admin requests found"
                        } else {
                            noRequestsTextView.visibility = View.GONE
                            requestsRecyclerView.visibility = View.VISIBLE
                        }
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to load admin requests: ${e.message}")
                        showError("Failed to load admin requests: ${e.message}")
                        noRequestsTextView.visibility = View.VISIBLE
                        requestsRecyclerView.visibility = View.GONE
                        noRequestsTextView.text = "Error loading admin requests"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading admin requests: ${e.message}")
                showError("An error occurred: ${e.message}")
            }
        }
    }
    
    /**
     * Process teacher request (approve/reject)
     */
    private fun processTeacherRequest(request: RoleRequest, approved: Boolean) {
        lifecycleScope.launch {
            try {
                val result = firebaseManager.processTeacherRequest(request.id, approved)
                
                result.fold(
                    onSuccess = {
                        val status = if (approved) "approved" else "rejected"
                        showMessage("Teacher request ${status}")
                        loadTeacherRequests() // Refresh the list
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to process teacher request: ${e.message}")
                        showError("Failed to process request: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception processing teacher request: ${e.message}")
                showError("An error occurred: ${e.message}")
            }
        }
    }
    
    /**
     * Process admin request (approve/reject)
     */
    private fun processAdminRequest(request: RoleRequest, approved: Boolean) {
        lifecycleScope.launch {
            try {
                val result = firebaseManager.processAdminRequest(request.id, approved)
                
                result.fold(
                    onSuccess = {
                        val status = if (approved) "approved" else "rejected"
                        showMessage("Admin request ${status}")
                        loadAdminRequests() // Refresh the list
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to process admin request: ${e.message}")
                        showError("Failed to process request: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception processing admin request: ${e.message}")
                showError("An error occurred: ${e.message}")
            }
        }
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