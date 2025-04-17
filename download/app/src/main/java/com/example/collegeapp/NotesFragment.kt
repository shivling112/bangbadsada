package com.example.collegeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegeapp.databinding.FragmentNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotesFragment : Fragment() {
    
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    
    private val database = FirebaseDatabase.getInstance().reference
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup subjects spinner
        setupSubjectsSpinner()
        
        // Setup notes list
        setupNotesList()
        
        // Search functionality
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            searchNotes(binding.etSearch.text.toString())
            true
        }
        
        // Upload button click listener
        binding.fabUpload.setOnClickListener {
            // Show upload dialog or navigate to upload screen
            Toast.makeText(context, "Upload functionality coming soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSubjectsSpinner() {
        // Implementation for setting up subjects spinner
        // This would load subject list from Firebase and populate the spinner
        
        // For demonstration purposes:
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("subjects").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Parse subjects and set up spinner
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load subjects", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
    
    private fun setupNotesList() {
        // Implementation for setting up notes list
        // This would use a RecyclerView with custom adapter
        
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(context)
        // binding.recyclerViewNotes.adapter = NotesAdapter(notesList)
    }
    
    private fun searchNotes(query: String) {
        // Implementation for searching notes
        // This would filter the notes list based on the query
        
        if (query.isEmpty()) {
            // Load all notes
            setupNotesList()
        } else {
            // Load filtered notes
            // For demonstration purposes
            Toast.makeText(context, "Searching for: $query", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}