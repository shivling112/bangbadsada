package com.example.collegeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.collegeapp.databinding.FragmentToolsBinding

class ToolsFragment : Fragment() {
    
    private var _binding: FragmentToolsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup click listeners for tools
        setupToolClickListeners()
    }
    
    private fun setupToolClickListeners() {
        // Room locator
        binding.cardRoomLocator.setOnClickListener {
            Toast.makeText(context, "Room Locator feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Event calendar
        binding.cardEventCalendar.setOnClickListener {
            Toast.makeText(context, "Event Calendar feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Wifi status
        binding.cardWifiStatus.setOnClickListener {
            Toast.makeText(context, "WiFi Status feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Mess menu
        binding.cardMessMenu.setOnClickListener {
            Toast.makeText(context, "Mess Menu feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Library resources
        binding.cardLibraryResources.setOnClickListener {
            Toast.makeText(context, "Library Resources feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Bus schedule
        binding.cardBusSchedule.setOnClickListener {
            Toast.makeText(context, "Bus Schedule feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}