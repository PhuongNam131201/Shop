package com.example.phuongnam19973.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProductActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_product_edit_a)

        productId = intent.getStringExtra("productId") ?: ""
        databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId)

        loadProductDetails()

        val btnSave = findViewById<Button>(R.id.btnUploadProducta)
        btnSave.setOnClickListener {
            saveProductChanges()
        }
    }

    private fun loadProductDetails() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                product?.let {
                    // Hiển thị thông tin sản phẩm trên giao diện
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProductActivity, "Lỗi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProductChanges() {
        val updatedProduct = Product(
            id = productId,
            name = findViewById<EditText>(R.id.etProductNamea).text.toString(),
            price = findViewById<EditText>(R.id.etProductPricea).text.toString().toDouble(),
            imageUrl = listOf() // Cập nhật URL hình ảnh nếu cần
        )

        databaseReference.setValue(updatedProduct)
            .addOnSuccessListener {
                Toast.makeText(this, "Sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
    }
}

