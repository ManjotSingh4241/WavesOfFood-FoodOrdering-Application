package com.netlifymanjot.wavesoffood

import android.net.Uri
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.netlifymanjot.wavesoffood.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodPrice: String? = null
    private var foodIngredients: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding){
            datailFoodName.text = foodName
            detailDescription.text = foodDescription
            detailIngredients.text = foodIngredients
            foodImage?.let {
                Glide.with(this@DetailsActivity).load(Uri.parse(it)).into(detailFoodImage)
            } ?: run {
                // Handle the case when foodImage is null, such as by loading a placeholder image
                detailFoodImage.setImageResource(com.denzcoskun.imageslider.R.drawable.default_loading) // Use a default image
            }
        }

        binding.imageButton.setOnClickListener {
            finish()
        }
    }
}