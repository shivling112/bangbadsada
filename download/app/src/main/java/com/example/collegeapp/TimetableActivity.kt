package com.example.collegeapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegeapp.databinding.ActivityTimetableBinding
import com.example.collegeapp.util.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth

class TimetableActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTimetableBinding
    private lateinit var firebaseHelper: FirebaseHelper
    
    private val daysList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        firebaseHelper = FirebaseHelper()
        
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Timetable"
        
        // Setup day spinner
        setupDaySpinner()
        
        // Load timetable for current day (default)
        loadTimetableForDay("Monday")
        
        // Back button click listener
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupDaySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDay.adapter = adapter
        
        binding.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDay = daysList[position]
                loadTimetableForDay(selectedDay)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun loadTimetableForDay(day: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        
        if (userId != null) {
            // Show loading indicator
            binding.progressBar.visibility = View.VISIBLE
            
            firebaseHelper.getTimetableData(userId) { timetableData ->
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
                
                if (timetableData != null) {
                    // Get classes for selected day
                    val classes = timetableData.days[day] ?: emptyList()
                    
                    if (classes.isEmpty()) {
                        // Show empty message
                        binding.tvNoClasses.visibility = View.VISIBLE
                        binding.recyclerViewClasses.visibility = View.GONE
                    } else {
                        // Setup RecyclerView with classes
                        binding.tvNoClasses.visibility = View.GONE
                        binding.recyclerViewClasses.visibility = View.VISIBLE
                        
                        binding.recyclerViewClasses.layoutManager = LinearLayoutManager(this)
                        // binding.recyclerViewClasses.adapter = ClassAdapter(classes)
                    }
                } else {
                    // Show error
                    Toast.makeText(this, "Failed to load timetable data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}