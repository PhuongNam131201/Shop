package com.example.phuongnam19973.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phuongnam19973.Activity.OrderDetailsActivity
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.util.Locale
import java.util.*
class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameOrder: TextView = itemView.findViewById(R.id.productNameOrder)
        val productImageOrder: ImageView = itemView.findViewById(R.id.productImageOrder)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
        val orderAddress: TextView = itemView.findViewById(R.id.orderAddress)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Lấy sản phẩm đầu tiên trong đơn hàng
        val firstItem = order.items.firstOrNull()

        // Cập nhật thông tin sản phẩm
        if (firstItem != null) {
            holder.productNameOrder.text = firstItem.name
            holder.productPrice.text = formatCurrency(firstItem.price) // Định dạng tiền cho sản phẩm

            // Sử dụng Glide để tải hình ảnh sản phẩm
            Glide.with(holder.productImageOrder.context)
                .load(firstItem.imageUrl) // Đường dẫn URL của hình ảnh
                .placeholder(R.drawable.box) // Hình ảnh thay thế khi tải
                .into(holder.productImageOrder)
        }

        // Định dạng tiền cho tổng đơn hàng
        holder.orderTotal.text = "Tổng tiền: ${formatCurrency(order.totalAmount)}"

        holder.orderAddress.text = "Địa chỉ: ${order.address}"
        holder.orderStatus.text = "Trạng thái: ${order.status}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orders", order) // Truyền toàn bộ đối tượng Order
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return orders.size
    }
    private fun formatCurrency(amount: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale("vi", "VN")) // Sử dụng Locale Việt Nam
        return numberFormat.format(amount) + " VND" // Thêm "VND" vào sau số tiền
    }

}
