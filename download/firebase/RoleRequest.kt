package com.example.collegeapp.firebase

/**
 * Data class representing a request for role change (teacher or admin)
 */
data class RoleRequest(
    val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val details: String,
    val requestedRole: UserType,
    val status: String, // pending, approved, rejected
    val timestamp: Long
)