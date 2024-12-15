package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.phuongnam19973.Adapter.ProductAdapter
import com.example.phuongnam19973.Model.Banner
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activit_product)
        //banner
        // Khởi tạo RecyclerView và layoutManager
        recyclerView = findViewById(R.id.recyclerProductHome)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 cột

        productList = mutableListOf()

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }

                // Kiểm tra danh sách sản phẩm
                if (productList.isNotEmpty()) {
                    // Nếu có sản phẩm, cập nhật adapter
                    productAdapter = ProductAdapter(productList)
                    recyclerView.adapter = productAdapter
                } else {
                    // Nếu không có sản phẩm, ẩn RecyclerView hoặc hiển thị thông báo khác
                    recyclerView.visibility = View.GONE
                    Toast.makeText(this@HomeActivity, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(this@HomeActivity, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })

        // Tìm TextView để hiển thị tên người dùng
        val textView = findViewById<TextView>(R.id.txtUserProduct)

        // Lấy thông tin người dùng từ Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Kiểm tra nếu người dùng đã đăng nhập
        if (user != null) {
            val userId = user.uid
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userRef = database.child("users").child(userId)

            // Lấy dữ liệu người dùng từ Realtime Database
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userData = task.result
                    val userName = userData?.child("name")?.getValue(String::class.java)
                    val userEmail = userData?.child("email")?.getValue(String::class.java)

                    // Hiển thị tên người dùng và email
                    textView.text = userName ?: "Người dùng"
                } else {
                    // Nếu có lỗi khi lấy dữ liệu
                    textView.text = "Lỗi khi lấy thông tin người dùng"
                }
            }
        } else {
            // Nếu người dùng chưa đăng nhập
            textView.text = "Bạn chưa đăng nhập"
        }
    }
}
