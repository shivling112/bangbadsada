package com.example.collegeapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    
    private val SPLASH_DELAY = 2000L
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        auth = FirebaseAuth.getInstance()
        
        // Wait for the splash screen delay
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is already logged in
            if (auth.currentUser != null) {
                // User is logged in, go to main activity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, go to login activity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, SPLASH_DELAY)
    }
}