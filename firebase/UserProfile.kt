package com.example.collegeapp.firebase

/**
 * Data class representing a user profile in Firestore
 */
data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val userType: UserType,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)