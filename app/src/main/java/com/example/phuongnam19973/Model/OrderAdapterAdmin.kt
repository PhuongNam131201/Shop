package com.example.phuongnam19973.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.R

class OrderAdapterAdmin(
    private val orders: List<Order>,
    private val onConfirm: (Order) -> Unit,
    private val onCancel: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapterAdmin.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_admin, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUserName: TextView = itemView.findViewById(R.id.txtUserNameAdmin)
        private val txtTotalAmount: TextView = itemView.findViewById(R.id.txtTotalAmountAdmin)
        private val txtStatus: TextView = itemView.findViewById(R.id.txtStatusAdmin)
        private val btnConfirm: Button = itemView.findViewById(R.id.btnConfirmadmin)
        private val btnCancel: Button = itemView.findViewById(R.id.btnCanceladmin)

        fun bind(order: Order) {
            txtUserName.text = order.userId
            txtTotalAmount.text = order.totalAmount.toString()
            txtStatus.text = order.status

            // Hiển thị nút xác nhận hoặc huỷ dựa trên trạng thái
            if (order.status == "Chờ xác nhận") {
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            } else {
                btnConfirm.visibility = View.GONE
                btnCancel.visibility = View.GONE
            }

            btnConfirm.setOnClickListener {
                onConfirm(order)
            }

            btnCancel.setOnClickListener {
                onCancel(order)
            }
        }
    }
}
