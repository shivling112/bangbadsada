package com.example.collegeapp.ui.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegeapp.R
import com.example.collegeapp.firebase.RoleRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying teacher role requests in a RecyclerView
 */
class TeacherRequestsAdapter(
    private var requests: List<RoleRequest>,
    private val onApprove: (RoleRequest) -> Unit,
    private val onReject: (RoleRequest) -> Unit
) : RecyclerView.Adapter<TeacherRequestsAdapter.RequestViewHolder>() {

    // View holder for request items
    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.request_name_text_view)
        val emailTextView: TextView = itemView.findViewById(R.id.request_email_text_view)
        val detailsTextView: TextView = itemView.findViewById(R.id.request_details_text_view)
        val dateTextView: TextView = itemView.findViewById(R.id.request_date_text_view)
        val statusTextView: TextView = itemView.findViewById(R.id.request_status_text_view)
        val approveButton: Button = itemView.findViewById(R.id.approve_button)
        val rejectButton: Button = itemView.findViewById(R.id.reject_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_role_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        
        // Set text for TextViews
        holder.nameTextView.text = request.name
        holder.emailTextView.text = request.email
        holder.detailsTextView.text = request.details
        
        // Format the date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date(request.timestamp))
        holder.dateTextView.text = "Requested: $dateString"
        
        // Set status
        holder.statusTextView.text = "Status: ${request.status.capitalize()}"
        
        // Show/hide buttons based on status
        if (request.status == "pending") {
            holder.approveButton.visibility = View.VISIBLE
            holder.rejectButton.visibility = View.VISIBLE
            
            // Set button click listeners
            holder.approveButton.setOnClickListener {
                onApprove(request)
            }
            
            holder.rejectButton.setOnClickListener {
                onReject(request)
            }
        } else {
            holder.approveButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = requests.size

    /**
     * Update the adapter data
     */
    fun updateData(newRequests: List<RoleRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }
    
    // Extension function to capitalize the first letter of a string
    private fun String.capitalize(): String {
        return if (this.isEmpty()) this else this.substring(0, 1).uppercase() + this.substring(1)
    }
}