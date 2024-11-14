package com.netlifymanjot.wavesoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.netlifymanjot.wavesoffood.CongratsBottomSheet
import com.netlifymanjot.wavesoffood.PayOutActivity
import com.netlifymanjot.wavesoffood.R
import com.netlifymanjot.wavesoffood.adapter.CartAdapter
import com.netlifymanjot.wavesoffood.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val cartFoodName = listOf("Burger", "sandwich", "item", "noodles")
        val cartItemPrice = listOf("$5", "$6", "$7", "$10")
        val cartImage = listOf(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,

            )
        val adapter =
            CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter

        binding.proceedButton.setOnClickListener {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    companion object {

    }
}