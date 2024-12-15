package com.netlifymanjot.wavesoffood.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.netlifymanjot.wavesoffood.adapter.BuyAgainAdapter
import com.netlifymanjot.wavesoffood.databinding.FragmentHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var historyReference: DatabaseReference
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var recentBuyAdapter: BuyAgainAdapter

    private val foodNames = arrayListOf<String>()
    private val foodPrices = arrayListOf<String>()
    private val foodImages = arrayListOf<String>()

    private val recentFoodNames = arrayListOf<String>()
    private val recentFoodPrices = arrayListOf<String>()
    private val recentFoodImages = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        historyReference = database.reference.child("user").child(auth.currentUser?.uid ?: "").child("History")

        setupRecyclerView()
        fetchHistoryData()

        return binding.root
    }

    private fun setupRecyclerView() {
        // Regular order history
        buyAgainAdapter = BuyAgainAdapter(foodNames, foodPrices, foodImages)
        binding.BuyAgainRecyclerView.adapter = buyAgainAdapter
        binding.BuyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Recent buys
        recentBuyAdapter = BuyAgainAdapter(recentFoodNames, recentFoodPrices, recentFoodImages)
        binding.recentBuyRecyclerView.adapter = recentBuyAdapter
        binding.recentBuyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchHistoryData() {
        historyReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodNames.clear()
                foodPrices.clear()
                foodImages.clear()

                recentFoodNames.clear()
                recentFoodPrices.clear()
                recentFoodImages.clear()

                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val foodName = itemSnapshot.child("foodName").getValue(String::class.java) ?: "Unknown"
                        val foodPrice = itemSnapshot.child("foodPrice").getValue(String::class.java) ?: "$0.00"
                        val foodImage = itemSnapshot.child("foodImage").getValue(String::class.java) ?: ""
                        val timestamp = itemSnapshot.child("timestamp").getValue(String::class.java) ?: ""

                        // Logic to separate recent buys (last 24 hours)
                        if (isRecentBuy(timestamp)) {
                            recentFoodNames.add(foodName)
                            recentFoodPrices.add(foodPrice)
                            recentFoodImages.add(foodImage)
                        } else {
                            foodNames.add(foodName)
                            foodPrices.add(foodPrice)
                            foodImages.add(foodImage)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No history found!", Toast.LENGTH_SHORT).show()
                }

                recentBuyAdapter.notifyDataSetChanged()
                buyAgainAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch history: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Check if the order is from the last 24 hours
    private fun isRecentBuy(timestamp: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val orderTime = sdf.parse(timestamp)
            val currentTime = Date()

            val difference = currentTime.time - (orderTime?.time ?: 0)
            difference <= 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        } catch (e: Exception) {
            false
        }
    }
}
