package com.example.phuongnam19973.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.ProductAdapter
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ManageProductActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_product)

        recyclerView = findViewById(R.id.recyclerViewProduct)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 cột
        productList = mutableListOf()

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        // Lấy dữ liệu từ Firebase Realtime Database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }

                // Kiểm tra xem danh sách sản phẩm có trống hay không
                if (productList.isNotEmpty()) {
                    // Nếu có sản phẩm, cập nhật adapter
                    productAdapter = ProductAdapter(productList)
                    recyclerView.adapter = productAdapter
                } else {
                    // Nếu không có sản phẩm, ẩn RecyclerView hoặc hiển thị thông báo khác
                    recyclerView.visibility = View.GONE
                    // Hoặc bạn có thể hiển thị một thông báo cho người dùng
                    Toast.makeText(this@ManageProductActivity, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(this@ManageProductActivity, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })

        // Chuyển đến màn hình thêm sản phẩm
        val add = findViewById<TextView>(R.id.txtAdd)
        add.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}

