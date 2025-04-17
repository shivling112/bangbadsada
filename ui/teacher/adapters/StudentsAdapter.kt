package com.example.collegeapp.ui.teacher.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.ui.teacher.models.StudentData

/**
 * Adapter for displaying students in a teacher's course in a RecyclerView
 */
class StudentsAdapter(
    private var students: List<StudentData>,
    private val onItemClick: (StudentData) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    // View holder for student items
    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.student_name_text_view)
        val emailTextView: TextView = itemView.findViewById(R.id.student_email_text_view)
        val gradeTextView: TextView = itemView.findViewById(R.id.student_grade_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        
        // Set text for TextViews
        holder.nameTextView.text = student.name
        holder.emailTextView.text = student.email
        holder.gradeTextView.text = "Grade: ${student.grade}%"
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(student)
        }
    }

    override fun getItemCount(): Int = students.size

    /**
     * Update the adapter data
     */
    fun updateData(newStudents: List<StudentData>) {
        students = newStudents
        notifyDataSetChanged()
    }
}