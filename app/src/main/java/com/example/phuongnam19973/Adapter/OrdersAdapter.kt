package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        holder.orderTotal.text = "Tổng tiền: ${order.totalAmount} VND"
        holder.orderAddress.text = "Địa chỉ: ${order.address}"
        holder.orderStatus.text = "Trạng thái: ${order.status}"
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
