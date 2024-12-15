package com.netlifymanjot.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netlifymanjot.wavesoffood.DetailsActivity
import com.netlifymanjot.wavesoffood.databinding.MenuItemBinding

class MenuAdapter(
    private val foodNames: List<String>,    // List of food names
    private val foodPrices: List<String>,   // List of food prices
    private val foodImages: List<String>,   // List of food image URLs
    private val context: Context            // Context for Glide and Intent
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Open DetailsActivity on item click
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", foodNames[position])
                putExtra("MenuItemPrice", foodPrices[position])
                putExtra("MenuItemImage", foodImages[position])
            }
            context.startActivity(intent)
        }

        fun bind(position: Int) {
            binding.apply {
                menuFoodName.text = foodNames[position]    // Set food name
                menuPrice.text = foodPrices[position]      // Set food price

                // Load image using Glide
                Glide.with(context)
                    .load(foodImages[position])           // URL for image
                    .placeholder(com.netlifymanjot.wavesoffood.R.drawable.placeholder) // Placeholder image
                    .error(com.netlifymanjot.wavesoffood.R.drawable.error)             // Error fallback image
                    .into(menuImage)
            }
        }
    }
}
