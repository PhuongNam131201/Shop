package com.example.phuongnam19973.Model

data class Notification(
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),  // Lấy thời gian hiện tại
    val userId: String = ""  // ID người dùng sẽ được gán vào thông báo
)
