package com.example.collegeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.collegeapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        
        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
        
        // Setup bottom navigation
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            var fragment: Fragment? = null
            
            when (menuItem.itemId) {
                R.id.nav_home -> fragment = HomeFragment()
                R.id.nav_attendance -> fragment = AttendanceFragment()
                R.id.nav_timetable -> {
                    // Launch timetable activity instead of fragment
                    startActivity(Intent(this, TimetableActivity::class.java))
                    return@setOnItemSelectedListener false
                }
                R.id.nav_notes -> fragment = NotesFragment()
                R.id.nav_tools -> fragment = ToolsFragment()
            }
            
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
                return@setOnItemSelectedListener true
            }
            
            false
        }
        
        // Profile icon click listener
        binding.ivProfile.setOnClickListener {
            // Open user profile or settings
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    // Handle sign out
    fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}