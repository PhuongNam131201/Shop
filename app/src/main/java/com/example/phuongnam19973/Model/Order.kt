package com.example.phuongnam19973.Model

data class Order(
    val id: String = "",
    val items: Map<String, CartItem> = emptyMap(),
    val totalPrice: Double = 0.0,
    val status: String = "Chờ",
    val createdAt: Long = System.currentTimeMillis()
)
