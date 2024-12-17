package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.CartAdapter
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalAmountTextView: TextView
    private lateinit var checkoutButton: Button

    private val cartItems = mutableListOf<CartItem>()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        cartAdapter = CartAdapter(cartItems, ::updateCartItem)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        loadCartItems()

        checkoutButton.setOnClickListener {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
            clearCart()
        }
    }

    private fun loadCartItems() {
        if (userId == null) return
        database.child("carts").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                var totalAmount = 0.0

                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        cartItems.add(cartItem)
                        totalAmount += cartItem.price * cartItem.quantity
                    }
                }
                cartAdapter.notifyDataSetChanged()
                val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalAmount)
                totalAmountTextView.text = "Tổng tiền: ${formattedPrice} VND"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Không thể tải giỏ hàng!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateCartItem(cartItem: CartItem, newQuantity: Int) {
        if (userId == null) return
        if (newQuantity <= 0) {
            database.child("carts").child(userId).child(cartItem.id).removeValue()
        } else {
            database.child("carts").child(userId).child(cartItem.id).child("quantity").setValue(newQuantity)
        }
    }

    private fun clearCart() {
        if (userId == null) return
        database.child("carts").child(userId).removeValue()
    }
}
