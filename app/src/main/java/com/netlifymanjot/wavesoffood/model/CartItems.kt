package com.netlifymanjot.wavesoffood.model

data class CartItems(
    var foodName: String = "",
    var foodPrice: String = "",
    var foodDescription: String = "",
    var foodQuantity: String = "1",
    var foodImage: String = "",
    var foodIngredient: String = ""
)
