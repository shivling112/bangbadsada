package com.example.collegeapp.ui.teacher.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.ui.teacher.models.Course

/**
 * Adapter for displaying courses in a RecyclerView for teacher dashboard
 */
class CoursesAdapter(
    private var courses: List<Course>,
    private val onItemClick: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    // View holder for course items
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.course_name_text_view)
        val studentsTextView: TextView = itemView.findViewById(R.id.course_students_text_view)
        val descriptionTextView: TextView = itemView.findViewById(R.id.course_description_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        
        // Set text for TextViews
        holder.nameTextView.text = course.name
        holder.studentsTextView.text = "Students: ${course.currentStudents}/${course.maxStudents}"
        holder.descriptionTextView.text = course.description
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    override fun getItemCount(): Int = courses.size

    /**
     * Update the adapter data
     */
    fun updateData(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}