package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.util.*

class OrderItemsAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImageOrderDetail)
        val itemName: TextView = itemView.findViewById(R.id.itemNameOrderDetail)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPriceOrderDetail)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantityOrderDetail)
        val itemTotalPrice: TextView = itemView.findViewById(R.id.itemTotalPriceOrderDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]

        // Tải hình ảnh từ URL sử dụng Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.itemImage)

        // Hiển thị tên sản phẩm
        holder.itemName.text = item.name

        // Định dạng giá tiền theo kiểu Việt Nam
        val formattedPrice = formatPrice(item.price)
        holder.itemPrice.text = "Giá: $formattedPrice VND"

        // Hiển thị số lượng
        holder.itemQuantity.text = "x${item.quantity} VND"

        // Tính tổng giá và định dạng
        val totalPrice = item.price * item.quantity
        val formattedTotalPrice = formatPrice(totalPrice)
        holder.itemTotalPrice.text = formattedTotalPrice
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Hàm định dạng giá tiền
    private fun formatPrice(price: Double): String {
        val locale = Locale("vi", "VN")  // Cài đặt locale cho Việt Nam
        val numberFormat = NumberFormat.getInstance(locale)
        return numberFormat.format(price)
    }
}

