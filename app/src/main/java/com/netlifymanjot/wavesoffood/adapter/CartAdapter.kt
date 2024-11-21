package com.netlifymanjot.wavesoffood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.netlifymanjot.wavesoffood.R
import com.netlifymanjot.wavesoffood.databinding.CartItemBinding

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartImages: MutableList<String>,
    private val cartDescriptions: MutableList<String>,
    private val cartQuantities: MutableList<Int>,
    private val cartIngredients: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantities)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                cartFoodName.text = cartItems[position]
                cartItemPrice.text = cartItemPrices[position]
                cartItemQuantity.text = cartQuantities[position].toString()

                // Load image with error handling
                val imageUri = cartImages[position]
                Glide.with(context)
                    .load(imageUri.takeIf { !it.isNullOrEmpty() } ?: R.drawable.banner1)
                    .placeholder(R.drawable.banner1) // Fallback during loading
                    .error(R.drawable.banner2) // Fallback for error
                    .into(cartImage)

                plusButton.setOnClickListener { increaseQuantity(position) }
                minusButton.setOnClickListener { decreaseQuantity(position) }
                deleteButton.setOnClickListener { deleteItem(position) }
            }
        }

        private fun increaseQuantity(position: Int) {
            if (cartQuantities[position] < 10) {
                cartQuantities[position]++
                updateQuantityInFirebase(position, cartQuantities[position])
                binding.cartItemQuantity.text = cartQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (cartQuantities[position] > 1) {
                cartQuantities[position]--
                updateQuantityInFirebase(position, cartQuantities[position])
                binding.cartItemQuantity.text = cartQuantities[position].toString()
            }
        }

        private fun updateQuantityInFirebase(position: Int, newQuantity: Int) {
            val database = FirebaseDatabase.getInstance().reference
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Reference to the CartItems in Firebase
            val cartItemsReference = database.child("user").child(userId).child("CartItems")

            // Query the specific item to update based on a unique key (e.g., foodName)
            cartItemsReference.orderByChild("foodName").equalTo(cartItems[position])
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (itemSnapshot in snapshot.children) {
                            // Update only the quantity in Firebase
                            itemSnapshot.ref.child("foodQuantity").setValue(newQuantity.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Failed to update quantity: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        private fun deleteItem(position: Int) {
            val database = FirebaseDatabase.getInstance().reference
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Reference to the CartItems in Firebase
            val cartItemsReference = database.child("user").child(userId).child("CartItems")

            // Query the specific item to delete based on unique key (or foodName)
            cartItemsReference.orderByChild("foodName").equalTo(cartItems[position])
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (itemSnapshot in snapshot.children) {
                            itemSnapshot.ref.removeValue() // Remove from Firebase
                        }

                        // Remove from RecyclerView
                        cartItems.removeAt(position)
                        cartItemPrices.removeAt(position)
                        cartImages.removeAt(position)
                        cartDescriptions.removeAt(position)
                        cartQuantities.removeAt(position)
                        cartIngredients.removeAt(position)

                        // Notify RecyclerView of the changes
                        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Failed to delete item: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

    }
}
