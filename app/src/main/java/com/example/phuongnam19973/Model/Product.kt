package com.example.phuongnam19973.Model

data class Product(
    val id : String ="",
    val name: String = "",
    var price: Double = 0.0,
    val category: String = "",
    val description: String = "",
    var imageUrl: List<String> = ArrayList(), // Danh sách các URL hình ảnh,
    var numberInCart: Int =0
) {
    // Constructor mặc định yêu cầu bởi Firebase
    constructor() : this("","", 0.0, "", "", ArrayList())
}

