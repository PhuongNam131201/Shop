package com.example.phuongnam19973.Model

data class Order(
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val address: String = "",
    val content: String ="",
    val phoneNumber: String = "",
    val status: String = "Chờ xác nhận" // Trạng thái đơn hàng
)
