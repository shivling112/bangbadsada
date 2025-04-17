package com.example.collegeapp.ui.teacher.models

/**
 * Data class representing a student in a teacher's course
 */
data class StudentData(
    val id: String = "",
    val studentId: String = "",
    val courseId: String = "",
    val name: String = "",
    val email: String = "",
    val enrollmentDate: Long = System.currentTimeMillis(),
    val grade: Int = 0
)