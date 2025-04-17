package com.example.collegeapp.ui.student.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.ui.student.models.CourseEnrollment

/**
 * Adapter for displaying a student's course enrollments in a RecyclerView
 */
class EnrollmentsAdapter(
    private var enrollments: List<CourseEnrollment>,
    private val onItemClick: (CourseEnrollment) -> Unit
) : RecyclerView.Adapter<EnrollmentsAdapter.EnrollmentViewHolder>() {

    // View holder for enrollment items
    class EnrollmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_text_view)
        val instructorNameTextView: TextView = itemView.findViewById(R.id.instructor_name_text_view)
        val gradeTextView: TextView = itemView.findViewById(R.id.grade_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_enrollment, parent, false)
        return EnrollmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val enrollment = enrollments[position]
        
        // Set text for TextViews
        holder.courseNameTextView.text = enrollment.courseName
        holder.instructorNameTextView.text = "Instructor: ${enrollment.instructorName}"
        holder.gradeTextView.text = "Grade: ${enrollment.grade}%"
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(enrollment)
        }
    }

    override fun getItemCount(): Int = enrollments.size

    /**
     * Update the adapter data
     */
    fun updateData(newEnrollments: List<CourseEnrollment>) {
        enrollments = newEnrollments
        notifyDataSetChanged()
    }
}