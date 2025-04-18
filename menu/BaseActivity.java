package com.example.collegeapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected String userRole; // "student", "teacher", or "admin"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize user role (you should get this from your authentication system)
        userRole = getUserRole();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the appropriate menu based on user role
        switch (userRole.toLowerCase()) {
            case "student":
                getMenuInflater().inflate(R.menu.student_menu, menu);
                break;
            case "teacher":
                getMenuInflater().inflate(R.menu.teacher_menu, menu);
                break;
            case "admin":
                getMenuInflater().inflate(R.menu.admin_menu, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.student_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                openProfile();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            // Add other menu item handlers here
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Abstract methods to be implemented by child activities
    protected abstract String getUserRole();
    protected abstract void openProfile();
    protected abstract void logout();
}