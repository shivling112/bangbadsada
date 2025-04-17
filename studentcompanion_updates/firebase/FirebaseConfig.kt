package com.example.collegeapp.firebase

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Properties

/**
 * This class holds the Firebase configuration values.
 * Values are loaded from environment variables or BuildConfig fields.
 */
object FirebaseConfig {
    private const val TAG = "FirebaseConfig"
    
    // Firebase configuration
    var API_KEY: String = System.getenv("VITE_FIREBASE_API_KEY") ?: ""
    var PROJECT_ID: String = System.getenv("VITE_FIREBASE_PROJECT_ID") ?: ""
    var APP_ID: String = System.getenv("VITE_FIREBASE_APP_ID") ?: ""
    
    init {
        Log.d(TAG, "Loading Firebase configuration...")
        
        if (API_KEY.isEmpty() || PROJECT_ID.isEmpty() || APP_ID.isEmpty()) {
            Log.w(TAG, "Firebase configuration not found in environment variables")
            try {
                // Try to load from local.properties as fallback
                val properties = Properties()
                val localPropertiesFile = this::class.java.classLoader.getResourceAsStream("local.properties")
                if (localPropertiesFile != null) {
                    properties.load(localPropertiesFile)
                    
                    if (API_KEY.isEmpty()) {
                        API_KEY = properties.getProperty("firebase.api_key", "")
                    }
                    if (PROJECT_ID.isEmpty()) {
                        PROJECT_ID = properties.getProperty("firebase.project_id", "")
                    }
                    if (APP_ID.isEmpty()) {
                        APP_ID = properties.getProperty("firebase.app_id", "")
                    }
                    
                    Log.d(TAG, "Loaded Firebase configuration from local.properties")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading Firebase configuration: ${e.message}")
            }
        }
        
        Log.d(TAG, "Firebase configuration loaded. ProjectID: $PROJECT_ID")
    }
    
    // Firebase URLs
    val AUTH_DOMAIN get() = "$PROJECT_ID.firebaseapp.com"
    val STORAGE_BUCKET get() = "$PROJECT_ID.appspot.com"
    val DATABASE_URL get() = "https://$PROJECT_ID.firebaseio.com"
}