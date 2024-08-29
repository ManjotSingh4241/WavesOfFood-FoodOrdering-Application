package com.netlifymanjot.wavesoffood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netlifymanjot.wavesoffood.adapter.MenuAdapter
import com.netlifymanjot.wavesoffood.databinding.FragmentMenuBootomSheetBinding

class MenuBootomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBootomSheetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBootomSheetBinding.inflate(inflater, container, false)
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        val menuFoodName = listOf(
            "Burger",
            "sandwich",
            "momo",
            "item",
            "noodles",
            "Burger",
            "sandwich",
            "momo",
            "item",
            "noodles"
        )
        val menuItemPrice = listOf("$5", "$6", "$7", "$8", "$10", "$5", "$6", "$7", "$8", "$10")
        val menuImage = listOf(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,

            )
        val adapter =
            MenuAdapter(ArrayList(menuFoodName), ArrayList(menuItemPrice), ArrayList(menuImage))
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {
    }
}