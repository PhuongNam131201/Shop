package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.OrderAdminAdapter
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R
import com.google.firebase.database.*

class OrderAdminActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderAdminAdapter: OrderAdminAdapter
    private lateinit var orderList: MutableList<Order>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_admin)

        // Initialize RecyclerView and Firebase database
        ordersRecyclerView = findViewById(R.id.ordersRecyclerViewAdmin)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        orderList = mutableListOf()

        database = FirebaseDatabase.getInstance().getReference("orders")

        // Load orders from Firebase
        loadOrdersFromFirebase()
    }
    private fun loadOrdersFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear() // Clear the current list to avoid duplication
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }
                setupAdapter() // Refresh the adapter with new data
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@OrderAdminActivity,
                    "Failed to load orders: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun setupAdapter() {
        orderAdminAdapter = OrderAdminAdapter(
            context = this,
            orderList = orderList,
            onConfirmClick = { order ->
                // Handle confirm click
                Toast.makeText(this, "Đã xác nhận: ${order.userId}", Toast.LENGTH_SHORT).show()
            },
            onCancelClick = { order ->
                // Handle cancel click
                Toast.makeText(this, "Đã Huỷ: ${order.userId}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { order ->
                // Handle delete click
                database.child(order.userId).removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Đã xoá: ${order.userId}", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {

                }
            }
        )
        ordersRecyclerView.adapter = orderAdminAdapter
    }
}
