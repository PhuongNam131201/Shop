package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.util.Locale

class OrderWaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_waiting)

        // Nhận tổng tiền từ Intent
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        // Lấy TextView để hiển thị tổng tiền
        val txtSumPrice = findViewById<TextView>(R.id.txtSumPrice)

        // Định dạng và hiển thị tổng tiền
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalAmount)
        txtSumPrice.text = "${formattedPrice} VND"

        val order = findViewById<Button>(R.id.ordersButtonWaiting)
        order.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val home = findViewById<Button>(R.id.homeButtonWaiting)
        home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
