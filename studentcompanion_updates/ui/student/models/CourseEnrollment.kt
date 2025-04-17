package com.example.collegeapp.ui.student.models

/**
 * Data class representing a student's enrollment in a course
 */
data class CourseEnrollment(
    val id: String = "",
    val studentId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val instructorName: String = "",
    val enrollmentDate: Long = System.currentTimeMillis(),
    val grade: Int = 0
)