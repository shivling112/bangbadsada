package com.example.collegeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.collegeapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        // Load user name
        currentUser?.let { user ->
            val userRef = database.getReference("users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "Student"
                    binding.tvWelcome.text = "Welcome, $userName"
                }
                
                override fun onCancelled(error: DatabaseError) {
                    binding.tvWelcome.text = "Welcome, Student"
                }
            })
        }
        
        // Load today's classes
        loadTodayClasses()
        
        // Load attendance percentage
        loadAttendancePercentage()
        
        // Setup quick links
        setupQuickLinks()
    }
    
    private fun loadTodayClasses() {
        // Implementation for loading today's classes from database
        // This would be populated with real data from Firebase
        
        // For demonstration, we'll use static data
        binding.tvNoClasses.visibility = View.GONE
        
        // Example code to be replaced with actual implementation:
        // val userId = auth.currentUser?.uid
        // val classesRef = database.getReference("classes").child(userId)
        // classesRef.addValueEventListener(object : ValueEventListener {
        //     ...
        // })
    }
    
    private fun loadAttendancePercentage() {
        // Implementation for loading attendance data from database
        // This would be populated with real data from Firebase
        
        // For demonstration, we'll use static data
        binding.attendanceProgressBar.progress = 75
        binding.tvAttendancePercentage.text = "75%"
    }
    
    private fun setupQuickLinks() {
        // Setup click listeners for quick access buttons
        binding.btnTimetable.setOnClickListener {
            // Navigate to timetable
        }
        
        binding.btnAttendance.setOnClickListener {
            // Navigate to attendance
        }
        
        binding.btnNotes.setOnClickListener {
            // Navigate to notes
        }
        
        binding.btnTools.setOnClickListener {
            // Navigate to tools
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}