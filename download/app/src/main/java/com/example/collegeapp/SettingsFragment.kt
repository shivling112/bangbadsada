package com.example.collegeapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.collegeapp.databinding.FragmentSettingsBinding
import com.example.collegeapp.util.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var firebaseHelper: FirebaseHelper
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseHelper = FirebaseHelper()
        
        // Load user profile
        loadUserProfile()
        
        // Setup click listeners for settings options
        setupClickListeners()
    }
    
    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        
        if (userId != null) {
            firebaseHelper.getUserData(userId) { userData ->
                if (userData != null) {
                    // Update UI with user data
                    binding.tvUserName.text = userData.name
                    binding.tvUserEmail.text = userData.email
                    binding.tvUserDepartment.text = userData.department
                    binding.tvUserRollNo.text = userData.studentId
                    
                    // Load profile image
                    if (userData.profilePicUrl.isNotEmpty()) {
                        // Use Glide or similar library to load profile image
                        // Glide.with(this).load(userData.profilePicUrl).into(binding.ivUserProfile)
                    }
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        // Edit profile
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Notification settings
        binding.cardNotifications.setOnClickListener {
            Toast.makeText(context, "Notification Settings feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Theme settings
        binding.cardTheme.setOnClickListener {
            Toast.makeText(context, "Theme Settings feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Privacy settings
        binding.cardPrivacy.setOnClickListener {
            Toast.makeText(context, "Privacy Settings feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // About
        binding.cardAbout.setOnClickListener {
            Toast.makeText(context, "About feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Logout
        binding.btnLogout.setOnClickListener {
            firebaseHelper.signOut()
            activity?.let {
                it.startActivity(Intent(it, LoginActivity::class.java))
                it.finish()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}