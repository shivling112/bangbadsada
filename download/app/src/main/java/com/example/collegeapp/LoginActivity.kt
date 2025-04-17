package com.example.collegeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.collegeapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        
        // Login button click listener
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Show progress
            binding.progressBar.visibility = android.view.View.VISIBLE
            
            // Authenticate with Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = android.view.View.GONE
                    
                    if (task.isSuccessful) {
                        // Login successful
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Login failed
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        
        // Register text click listener
        binding.tvRegister.setOnClickListener {
            // Open registration activity or show registration dialog
            // Implementation for registration would go here
            Toast.makeText(this, "Registration feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Forgot password text click listener
        binding.tvForgotPassword.setOnClickListener {
            // Open forgot password activity or show reset dialog
            // Implementation for password reset would go here
            Toast.makeText(this, "Password reset feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}