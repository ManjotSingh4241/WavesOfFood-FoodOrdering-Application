package com.netlifymanjot.wavesoffood.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.netlifymanjot.wavesoffood.MenuBootomSheetFragment
import com.netlifymanjot.wavesoffood.R
import com.netlifymanjot.wavesoffood.adapter.MenuAdapter
import com.netlifymanjot.wavesoffood.adapter.PopularAdapter
import com.netlifymanjot.wavesoffood.databinding.ActivityLoginBinding
import com.netlifymanjot.wavesoffood.databinding.FragmentHomeBinding
import com.netlifymanjot.wavesoffood.model.MenuItem

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.ViewAllMenu.setOnClickListener {
            val BottomSheetDialog = MenuBootomSheetFragment()
            BottomSheetDialog.show(parentFragmentManager, "TEST")
        }


        retrieveAndDisplayPopularItems()

        return binding.root


    }

    private fun retrieveAndDisplayPopularItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let{menuItems.add(it)}
                }
                randomPopularItems()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun randomPopularItems() {
    val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItems = index.take(numItemToShow).map{menuItems[it]}

        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
        // Extract foodNames, foodPrices, and foodImages from MenuItem list
        val foodNames = subsetMenuItems.map { it.foodName ?: "Unknown" }
        val foodPrices = subsetMenuItems.map { it.foodPrice ?: "N/A" }
        val foodImages = subsetMenuItems.map { it.foodImage ?: "" }

        // Pass extracted lists to MenuAdapter
        val adapter = MenuAdapter(foodNames, foodPrices, foodImages, requireContext())
        binding.PopulerRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopulerRecycleView.adapter = adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }

}