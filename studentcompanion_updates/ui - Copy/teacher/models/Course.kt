package com.example.collegeapp.ui.teacher.models

/**
 * Data class representing a course created by a teacher
 */
data class Course(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val maxStudents: Int = 40,
    val currentStudents: Int = 0
)