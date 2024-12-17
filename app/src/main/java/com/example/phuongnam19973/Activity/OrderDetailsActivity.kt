package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.OrderItemsAdapter
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        // Nhận thông tin từ intent
        val order = intent.getParcelableExtra<Order>("orders")

        // Hiển thị thông tin đơn hàng
        findViewById<TextView>(R.id.tvOrderDetailAddress).text = " ${order?.address}"
        findViewById<TextView>(R.id.tvOrderDetailPhoneNumber).text = "${order?.phoneNumber}"
        findViewById<TextView>(R.id.tvOrderDetailStatus).text = "${order?.status}"
        val formattedTotalAmount = formatPrice(order?.totalAmount ?: 0.0)
        findViewById<TextView>(R.id.tvOrderDetailTotalAmount).text = "$formattedTotalAmount VND"

        // Hiển thị danh sách các món
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewOrderItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OrderItemsAdapter(order?.items ?: emptyList())
    }
    private fun formatPrice(price: Double): String {
        val locale = Locale("vi", "VN")  // Cài đặt locale cho Việt Nam
        val numberFormat = NumberFormat.getInstance(locale)
        return numberFormat.format(price)
    }
}
