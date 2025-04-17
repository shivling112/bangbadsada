package com.example.studentcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected String getUserRole() {
        // This should be replaced with your actual user role retrieval logic
        // For example, getting it from SharedPreferences or your authentication system
        return "student"; // Default role
    }

    @Override
    protected void openProfile() {
        // Implement profile opening logic
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    @Override
    protected void logout() {
        // Implement logout logic
        // Clear user session
        // Navigate to login screen
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
} 