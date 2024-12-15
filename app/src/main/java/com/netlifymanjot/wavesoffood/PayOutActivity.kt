package com.netlifymanjot.wavesoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.netlifymanjot.wavesoffood.databinding.ActivityPayOutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String

    private lateinit var foodNames: ArrayList<String>
    private lateinit var foodPrices: ArrayList<String>
    private lateinit var foodImages: ArrayList<String>
    private lateinit var foodIngredients: ArrayList<String>
    private lateinit var foodDescriptions: ArrayList<String>
    private lateinit var foodQuantities: ArrayList<Int>
    private var totalAmount: String = ""

    private var userName: String = ""
    private var userAddress: String = ""
    private var userPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        // Get cart data from the intent
        foodNames = intent.getStringArrayListExtra("foodNames") ?: ArrayList()
        foodPrices = intent.getStringArrayListExtra("foodPrices") ?: ArrayList()
        foodImages = intent.getStringArrayListExtra("foodImages") ?: ArrayList()
        foodIngredients = intent.getStringArrayListExtra("foodIngredients") ?: ArrayList()
        foodDescriptions = intent.getStringArrayListExtra("foodDescriptions") ?: ArrayList()
        foodQuantities = intent.getIntegerArrayListExtra("foodQuantities") ?: ArrayList()
        totalAmount = intent.getStringExtra("totalAmount") ?: "$0.00"

        // Display the total amount
        binding.totalAmount.text = totalAmount

        // Fetch user profile details
        fetchUserProfile()

        // Place Order Button
        binding.PlaceMyOrder.setOnClickListener {
            storeOrderDetailsInFirebase()
            moveCartItemsToHistory()
        }
    }

    private fun fetchUserProfile() {
        val userReference = database.reference.child("user").child(userId)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    userAddress = snapshot.child("address").getValue(String::class.java) ?: "N/A"
                    userPhone = snapshot.child("phone").getValue(String::class.java) ?: "N/A"

                    // Update UI with user details
                    binding.name.setText(userName)
                    binding.address.setText(userAddress)
                    binding.phone.setText(userPhone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun storeOrderDetailsInFirebase() {
        val orderDetailsReference = database.reference.child("OrderDetails").push()
        val currentTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val orderData = hashMapOf<String, Any>(
            "userId" to userId,
            "userName" to userName,
            "userAddress" to userAddress,
            "userPhone" to userPhone,
            "timestamp" to currentTimestamp,
            "totalAmount" to totalAmount,
            "orderItems" to foodNames.mapIndexed { index, name ->
                mapOf(
                    "foodName" to name,
                    "foodPrice" to foodPrices[index],
                    "foodQuantity" to foodQuantities[index],
                    "foodImage" to foodImages[index]
                )
            }
        )

        orderDetailsReference.setValue(orderData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Optionally, show a success message or perform further actions
            }
        }
    }

    private fun moveCartItemsToHistory() {
        val cartReference = database.reference.child("user").child(userId).child("CartItems")
        val historyReference = database.reference.child("user").child(userId).child("History")

        cartReference.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Move each item from Cart to History
                for (itemSnapshot in snapshot.children) {
                    val itemData = itemSnapshot.value
                    historyReference.push().setValue(itemData) // Add to History
                }

                // Clear the cart after moving items
                cartReference.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Show success message
                        val bottomSheetDialog = CongratsBottomSheet()
                        bottomSheetDialog.show(supportFragmentManager, "CongratsDialog")
                    }
                }
            }
        }
    }
}
