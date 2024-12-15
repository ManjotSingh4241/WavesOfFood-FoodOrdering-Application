package com.netlifymanjot.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.netlifymanjot.wavesoffood.databinding.BuyAgainItemBinding
import java.util.ArrayList

class BuyAgainAdapter(
    private val buyAgainFoodName: ArrayList<String>,
    private val buyAgainFoodPrice: ArrayList<String>,
    private val buyAgainFoodImage: ArrayList<String> // Updated from Int to String
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(
            buyAgainFoodName[position],
            buyAgainFoodPrice[position],
            buyAgainFoodImage[position]
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding =
            BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

    class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice

            // Use Glide to load image from URL
            Glide.with(binding.root.context)
                .load(foodImage)
                .into(binding.buyAgainFoodImage)
        }
    }
}
