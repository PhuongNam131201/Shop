package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.phuongnam19973.Adapter.BannerAdapter
import com.example.phuongnam19973.Adapter.ProductAdapter
import com.example.phuongnam19973.Adapter.SliderAdapter
import com.example.phuongnam19973.Model.Banner
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: DatabaseReference
    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var adapter: BannerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activit_product)
        //banner
        viewPagerSlider = findViewById(R.id.viewPagerSlider)
        dotsIndicator = findViewById(R.id.dotIndicatora)

        // Khởi tạo Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("banners")

        // Lấy dữ liệu từ Firebase
        fetchBannerData()
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
        val cart = findViewById<LinearLayout>(R.id.llCartHome)
        cart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        val order = findViewById<ImageView>(R.id.odersIcon)
        order.setOnClickListener{
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        val notification = findViewById<ImageView>(R.id.notificationIcon)
        notification.setOnClickListener{
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun fetchBannerData() {
        // Lấy dữ liệu từ Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banners = mutableListOf<String>()
                for (child in snapshot.children) {
                    val imageUrl = child.child("imageUrl").getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        banners.add(imageUrl)
                    }
                }

                if (banners.isNotEmpty()) {
                    // Cập nhật ViewPager2 với dữ liệu banner
                    adapter = BannerAdapter(banners)
                    viewPagerSlider.adapter = adapter
                    dotsIndicator.attachTo(viewPagerSlider)
                    dotsIndicator.visibility = View.VISIBLE

                    // Bắt đầu chuyển đổi banner tự động sau mỗi 1 giây
                    startAutoScroll(banners.size)
                } else {
                    dotsIndicator.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Lỗi tải dữ liệu banner: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Tạo một phương thức để tự động chuyển trang mỗi giây
    private fun startAutoScroll(bannerCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            val currentItem = viewPagerSlider.currentItem
            val nextItem = if (currentItem + 1 < bannerCount) currentItem + 1 else 0
            viewPagerSlider.setCurrentItem(nextItem, true)
        }

        // Lặp lại việc chuyển trang mỗi giây
        handler.postDelayed(update, 5000)
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 5000) // Tiếp tục lặp lại sau mỗi giây
                update.run()
            }
        }, 5000)
    }



}
