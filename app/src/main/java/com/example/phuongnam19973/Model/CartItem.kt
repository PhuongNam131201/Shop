package com.example.phuongnam19973.Model

data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Int = 0,
    val imageUrl: String = ""
)
