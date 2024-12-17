package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.Model.OrderAdapterAdmin
import com.example.phuongnam19973.R
import com.google.firebase.database.*

class OrderAminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapterAdmin: OrderAdapterAdmin
    private lateinit var database: DatabaseReference
    private var orders: MutableList<Order> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_admin)

        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Kết nối với Realtime Database
        database = FirebaseDatabase.getInstance().reference.child("orders")

        // Lấy danh sách đơn hàng từ Firebase
        loadOrders()

        // Chức năng xác nhận đơn hàng
        val onConfirm: (Order) -> Unit = { order ->
            updateOrderStatus(order, "Đã xác nhận")
        }

        // Chức năng huỷ đơn hàng
        val onCancel: (Order) -> Unit = { order ->
            updateOrderStatus(order, "Đã huỷ")
        }

        // Cập nhật adapter
        orderAdapterAdmin = OrderAdapterAdmin(orders, onConfirm, onCancel)
        recyclerView.adapter = orderAdapterAdmin
    }

    private fun loadOrders() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (orderSnapshot in snapshot.children) {
                    // Chuyển đổi dữ liệu Firebase thành đối tượng Order
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
                orderAdapterAdmin.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderAminActivity, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderStatus(order: Order, status: String) {
        val updatedOrder = order.copy(status = status)
        database.child(order.userId).setValue(updatedOrder).addOnSuccessListener {
            Toast.makeText(this, "Đơn hàng đã được $status", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi cập nhật trạng thái", Toast.LENGTH_SHORT).show()
        }
    }
}
