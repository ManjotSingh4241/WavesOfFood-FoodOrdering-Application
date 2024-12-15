package com.netlifymanjot.wavesoffood.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.netlifymanjot.wavesoffood.PayOutActivity
import com.netlifymanjot.wavesoffood.adapter.CartAdapter
import com.netlifymanjot.wavesoffood.databinding.FragmentCartBinding
import com.netlifymanjot.wavesoffood.model.CartItems

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private val foodNames = mutableListOf<String>()
    private val foodPrices = mutableListOf<String>()
    private val foodDescriptions = mutableListOf<String>()
    private val foodImagesUri = mutableListOf<String>()
    private val foodIngredients = mutableListOf<String>()
    private val quantity = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Set up RecyclerView
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            getOrderItemsDetails()
        }

        return binding.root
    }

    private fun getOrderItemsDetails() {
        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImage,
                    foodIngredient,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Order making failed...", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)

            // Add extras to the intent
            intent.putStringArrayListExtra("foodNames", ArrayList(foodName))
            intent.putStringArrayListExtra("foodPrices", ArrayList(foodPrice))
            intent.putStringArrayListExtra("foodDescriptions", ArrayList(foodDescription))
            intent.putStringArrayListExtra("foodImages", ArrayList(foodImage))
            intent.putStringArrayListExtra("foodIngredients", ArrayList(foodIngredient))
            intent.putIntegerArrayListExtra("foodQuantities", ArrayList(foodQuantities))
            intent.putExtra("totalAmount", calculateTotalAmount()) // Pass the total amount

            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        userId = auth.currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        // Clear lists to avoid duplication
        foodNames.clear()
        foodPrices.clear()
        foodDescriptions.clear()
        foodImagesUri.clear()
        foodIngredients.clear()
        quantity.clear()

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(context, "No items in cart", Toast.LENGTH_SHORT).show()
                    setAdapter() // Attach empty adapter
                    return
                }

                Log.d("CartFragment", "Snapshot: ${snapshot.value}")

                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)
                    Log.d("CartFragment", "CartItem: $cartItem")
                    foodNames.add(cartItem?.foodName ?: "Unknown")
                    foodPrices.add(cartItem?.foodPrice ?: "0.0")
                    foodDescriptions.add(cartItem?.foodDescription ?: "")
                    foodImagesUri.add(cartItem?.foodImage ?: "") // Handle missing images
                    foodIngredients.add(cartItem?.foodIngredient ?: "")
                    quantity.add(cartItem?.foodQuantity?.toIntOrNull() ?: 1)
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(
            requireContext(),
            foodNames,
            foodPrices,
            foodImagesUri,
            foodDescriptions,
            quantity,
            foodIngredients
        )
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun calculateTotalAmount(): String {
        var totalAmount = 0.0
        for (i in foodPrices.indices) {
            val price = foodPrices[i].replace("$", "").toDoubleOrNull() ?: 0.0
            totalAmount += price * quantity[i]
        }
        return "$%.2f".format(totalAmount) // Format total amount as currency
    }
}
