package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.util.*
class CartAdapter(
    private val cartItems: List<CartItem>,
    private val updateQuantity: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(cartItem.price)
        Glide.with(holder.productImage.context).load(cartItem.imageUrl).into(holder.productImage)
        holder.productName.text = cartItem.name
        holder.productPrice.text = "$formattedPrice VND"
        holder.productQuantity.text = cartItem.quantity.toString()

        holder.btnIncrease.setOnClickListener {
            updateQuantity(cartItem, cartItem.quantity + 1)
        }
        holder.btnDecrease.setOnClickListener {
            updateQuantity(cartItem, cartItem.quantity - 1)
        }
    }

    override fun getItemCount() = cartItems.size
}
