package com.netlifymanjot.wavesoffood.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.netlifymanjot.wavesoffood.adapter.MenuAdapter
import com.netlifymanjot.wavesoffood.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var menuReference: DatabaseReference

    // Original Menu Lists fetched from Firebase
    private val originalMenuFoodName = mutableListOf<String>()
    private val originalMenuItemPrice = mutableListOf<String>()
    private val originalMenuImage = mutableListOf<String>()

    // Filtered Lists for Search Results
    private val filteredMenuFoodName = mutableListOf<String>()
    private val filteredMenuItemPrice = mutableListOf<String>()
    private val filteredMenuImage = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding initialization
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        menuReference = database.reference.child("menu")

        // Initialize RecyclerView and Adapter
        adapter = MenuAdapter(filteredMenuFoodName, filteredMenuItemPrice, filteredMenuImage, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        // Fetch menu data and initialize search functionality
        fetchMenuData()
        setupSearchView()

        return binding.root
    }

    // Fetch menu data from Firebase
    private fun fetchMenuData() {
        menuReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalMenuFoodName.clear()
                originalMenuItemPrice.clear()
                originalMenuImage.clear()

                if (snapshot.exists()) {
                    for (menuItemSnapshot in snapshot.children) {
                        val foodName = menuItemSnapshot.child("foodName").getValue(String::class.java) ?: "Unknown"
                        val foodPrice = menuItemSnapshot.child("foodPrice").getValue(String::class.java) ?: "$0"
                        val foodImage = menuItemSnapshot.child("foodImage").getValue(String::class.java) ?: ""

                        originalMenuFoodName.add(foodName)
                        originalMenuItemPrice.add(foodPrice)
                        originalMenuImage.add(foodImage)
                    }
                    // Initially show all menu items
                    showAllMenu()
                } else {
                    Toast.makeText(requireContext(), "No menu items found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch menu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to display all menu items
    private fun showAllMenu() {
        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuImage.clear()

        filteredMenuFoodName.addAll(originalMenuFoodName)
        filteredMenuItemPrice.addAll(originalMenuItemPrice)
        filteredMenuImage.addAll(originalMenuImage)

        adapter.notifyDataSetChanged()
    }

    // Setup search view functionality
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    // Filter menu items based on the search query
    private fun filterMenuItems(query: String) {
        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuImage.clear()

        originalMenuFoodName.forEachIndexed { index, foodName ->
            if (foodName.contains(query, ignoreCase = true)) {
                filteredMenuFoodName.add(foodName)
                filteredMenuItemPrice.add(originalMenuItemPrice[index])
                filteredMenuImage.add(originalMenuImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }
}
