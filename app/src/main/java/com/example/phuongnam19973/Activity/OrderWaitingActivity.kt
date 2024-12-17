package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.R

class OrderWaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_waiting)
        val order = findViewById<Button>(R.id.ordersButtonWaiting)
        order.setOnClickListener{
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        val home = findViewById<Button>(R.id.homeButtonWaiting)
        home.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        }

    }
}
