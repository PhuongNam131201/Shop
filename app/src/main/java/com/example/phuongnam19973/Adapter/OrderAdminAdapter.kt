package com.example.phuongnam19973.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Activity.OrderDetailsActivity
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

@Suppress("DEPRECATION")
class OrderAdminAdapter(
    private val context: Context,
    private val orderList: List<Order>,
    private val onConfirmClick: (Order) -> Unit,
    private val onCancelClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdminAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameOrderAdmin: TextView = itemView.findViewById(R.id.userNameOrderAdmin)
        val orderTotalAdmin: TextView = itemView.findViewById(R.id.orderTotalAdmin)
        val orderAddressAdmin: TextView = itemView.findViewById(R.id.orderAddressAdmin)
        val orderStatusAdmin: TextView = itemView.findViewById(R.id.orderStatusAdmin)
        val btnConfirmAdmin: ImageView = itemView.findViewById(R.id.btnConfirmadmin)
        val btnCancelAdmin: ImageView = itemView.findViewById(R.id.btnCanceladmin)
        val btnDeleteAdmin: ImageView = itemView.findViewById(R.id.btnDeleteAdmin)
        val llHuy: LinearLayout = itemView.findViewById(R.id.llHuy)
        val llXacNhan: LinearLayout = itemView.findViewById(R.id.llXacNhan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_admin, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        // Lấy tên người dùng từ Firebase Realtime Database
        val userId = order.userId
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java)
                holder.userNameOrderAdmin.text = "Người dùng: ${userName ?: "Không xác định"}"
            }

            override fun onCancelled(error: DatabaseError) {
                holder.userNameOrderAdmin.text = "Người dùng: Lỗi tải tên"
            }
        })

        holder.orderTotalAdmin.text = "Tổng tiền: ${formatCurrency(order.totalAmount)}"
        holder.orderAddressAdmin.text = "Địa chỉ: ${order.address}"
        holder.orderStatusAdmin.text = "${order.status}"
        when (order.status) {
            "Chờ xác nhận" -> {
                holder.orderStatusAdmin.setTextColor(context.resources.getColor(R.color.blue))
            } // Màu cho "Chờ xác nhận"
            "Đã xác nhận" -> {
                holder.orderStatusAdmin.setTextColor(context.resources.getColor(R.color.green))
                holder.llXacNhan.visibility = View.GONE

            } // Màu cho "Đã xác nhận"
            "Đã huỷ" -> {
                holder.orderStatusAdmin.setTextColor(context.resources.getColor(R.color.orange))
                holder.llHuy.visibility = View.GONE
            } // Màu cho "Đã huỷ"
            else -> holder.orderStatusAdmin.setTextColor(context.resources.getColor(R.color.blue)) // Màu mặc định
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orders", order) // Truyền toàn bộ đối tượng Order
            context.startActivity(intent)
        }
        // Thay đổi trạng thái đơn hàng khi bấm nút Confirm
        holder.btnConfirmAdmin.setOnClickListener {
            if (order.status == "Chờ xác nhận" ||order.status == "Đã huỷ"  ) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("orders/${order.id}")
                databaseReference.child("status").setValue("Đã xác nhận")
                    .addOnSuccessListener {
                        Toast.makeText(context, "Đã xác nhận đơn hàng!", Toast.LENGTH_SHORT).show()
                        onConfirmClick(order) // Gọi callback nếu cần
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi xác nhận đơn hàng!", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Thay đổi trạng thái đơn hàng khi bấm nút Cancel
        holder.btnCancelAdmin.setOnClickListener {
            if (order.status == "Chờ xác nhận"||order.status == "Đã xác nhận") {
                val databaseReference = FirebaseDatabase.getInstance().getReference("orders/${order.id}")
                databaseReference.child("status").setValue("Đã huỷ")
                    .addOnSuccessListener {
                        Toast.makeText(context, "Đã hủy đơn hàng!", Toast.LENGTH_SHORT).show()
                        onCancelClick(order) // Gọi callback nếu cần
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi huỷ đơn hàng!", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Xóa đơn hàng khi bấm nút Delete
        holder.btnDeleteAdmin.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().getReference("orders/${order.id}")
            databaseReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Đã xoá đơn hàng!", Toast.LENGTH_SHORT).show()
                    onDeleteClick(order) // Gọi callback nếu cần
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Lỗi xoá đơn hàng!", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun formatCurrency(amount: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale("vi", "VN")) // Sử dụng Locale Việt Nam
        return numberFormat.format(amount) + " VND" // Thêm "VND" vào sau số tiền
    }

    override fun getItemCount(): Int = orderList.size
}
