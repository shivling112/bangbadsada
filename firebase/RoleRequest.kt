package com.example.collegeapp.firebase

/**
 * Data class representing a role request in Firestore
 */
data class RoleRequest(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val details: String = "",
    val requestedRole: UserType = UserType.STUDENT,
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis()
)