package com.example.collegeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegeapp.databinding.FragmentAttendanceBinding
import com.example.collegeapp.util.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth

class AttendanceFragment : Fragment() {
    
    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var firebaseHelper: FirebaseHelper
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseHelper = FirebaseHelper()
        
        // Get current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        
        if (userId != null) {
            // Show loading indicator
            binding.progressBar.visibility = View.VISIBLE
            
            // Load attendance data
            firebaseHelper.getAttendanceData(userId) { attendanceData ->
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
                
                if (attendanceData != null) {
                    // Update overall attendance
                    binding.overallProgressBar.progress = (attendanceData.overallPercentage * 100).toInt()
                    binding.tvOverallPercentage.text = "${(attendanceData.overallPercentage * 100).toInt()}%"
                    
                    // Setup subject attendance list
                    setupSubjectList(attendanceData.subjectsAttendance.values.toList())
                } else {
                    // Show error message
                    Toast.makeText(context, "Failed to load attendance data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupSubjectList(subjectAttendances: List<FirebaseHelper.SubjectAttendance>) {
        // Implementation for setting up RecyclerView with subject attendances
        // This would use a custom adapter to display the subject attendance items
        
        // For demonstration purposes
        binding.recyclerViewSubjects.layoutManager = LinearLayoutManager(context)
        // binding.recyclerViewSubjects.adapter = AttendanceAdapter(subjectAttendances)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}