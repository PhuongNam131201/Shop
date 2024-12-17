package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.phuongnam19973.Adapter.ImageDetailAdapter
import com.example.phuongnam19973.Adapter.SliderAdapter
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.text.DecimalFormat

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var imageDetailAdapter: ImageDetailAdapter
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        // Khởi tạo FirebaseAuth và Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        checkUserRole()

        // Nhận dữ liệu từ Intent
        val productId = intent.getStringExtra("productId")?: return
        val productName = intent.getStringExtra("productName") ?: ""
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription")?: ""
        val productImages = intent.getStringArrayListExtra("productImages")?: arrayListOf()
        val productCategory = intent.getStringArrayListExtra("productCategory")

        // Ánh xạ các view
        val txtNameDetail: TextView = findViewById(R.id.txtNameDetail)
        val txtPriceDetail: TextView = findViewById(R.id.txtPriceDetail)
        val txtDescriptionDetail: TextView = findViewById(R.id.txtDescriptionDetail)
        val slider: ViewPager2 = findViewById(R.id.slider)
        val dotIndicator: DotsIndicator = findViewById(R.id.dotIndicator)
        val colorList: RecyclerView = findViewById(R.id.colorList)
        val btnDelete: ImageView = findViewById(R.id.btnDelete)
        val btnEdit: ImageView = findViewById(R.id.btnEdit)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)
        val btnBuyNow: Button = findViewById(R.id.btnBuyNow)
        // Khi nhấn "Thêm vào giỏ hàng"
        btnAddToCart.setOnClickListener {
            val userId = mAuth.currentUser?.uid ?: return@setOnClickListener
            val product = Product(
                id = productId,
                name = productName,
                price = productPrice,
                description = productDescription,
                imageUrl = productImages
            )
            addToCart(userId, product, 1) // Thêm 1 sản phẩm
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
        }

        // Khi nhấn "Mua hàng"
        btnBuyNow.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        // Gán dữ liệu vào TextView
        txtNameDetail.text = productName
        Log.d("ProductDetail", "Product Price: $productPrice")
        // Định dạng số tiền với dấu phân cách hàng nghìn
        val formattedPrice = DecimalFormat("###,###").format(productPrice)

// Gán giá đã được định dạng và thêm "VND" vào cuối
        txtPriceDetail.text = "$formattedPrice VND"
        txtDescriptionDetail.text = productDescription

        // Thiết lập slider nếu có hình ảnh
        if (!productImages.isNullOrEmpty()) {
            sliderAdapter = SliderAdapter(productImages)
            slider.adapter = sliderAdapter

            // Kết nối DotsIndicator với ViewPager2
            dotIndicator.setViewPager2(slider)
            dotIndicator.visibility = View.VISIBLE
        }

        // Hiển thị danh sách màu sắc hoặc các lựa chọn liên quan nếu cần
        if (!productImages.isNullOrEmpty()) {
            imageDetailAdapter = ImageDetailAdapter(productImages) { position ->
                isUserScrolling = true
                slider.setCurrentItem(position, true)
            }

            colorList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            colorList.adapter = imageDetailAdapter
        }

        // Liên kết sự kiện thay đổi trên Slider
        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (!isUserScrolling) {
                    imageDetailAdapter.setSelectedPosition(position)
                }
                isUserScrolling = false
            }
        })

        // Xử lý sự kiện khi nhấn nút xóa
        btnDelete.setOnClickListener {
            productId?.let { id ->
                showDeleteConfirmationDialog(id)
            } ?: run {
                Toast.makeText(this, "Không tìm thấy ID sản phẩm!", Toast.LENGTH_SHORT).show()
            }
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java).apply {
                putExtra("productId", productId)
                putExtra("productName", productName)
                putExtra("productDescription", productDescription)
                putExtra("productPrice", productPrice)
                putExtra("productCategory", productCategory)
            }
            startActivity(intent)
        }

    }
    fun addToCart(userId: String, product: Product, quantity: Int) {
        val cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        cartRef.child(product.id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentQuantity = snapshot.child("quantity").getValue(Int::class.java) ?: 0
                cartRef.child(product.id).child("quantity").setValue(currentQuantity + quantity)
            } else {
                val cartItem = CartItem(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = quantity,
                    imageUrl = product.imageUrl.firstOrNull() ?: ""
                )
                cartRef.child(product.id).setValue(cartItem)
            }
        }.addOnFailureListener {
            Log.e("CartError", "Failed to add product: ${it.message}")
        }
    }

    private fun showDeleteConfirmationDialog(productId: String) {
        // Tạo và hiển thị hộp thoại xác nhận
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteProductFromFirebase(productId)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss() // Đóng hộp thoại nếu nhấn Hủy
            }
            .create()

        alertDialog.show()
    }
    private fun deleteProductFromFirebase(productId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")

        databaseReference.child(productId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                finish() // Đóng màn hình chi tiết sau khi xóa
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Xóa sản phẩm thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Hàm kiểm tra quyền của người dùng từ Firebase Realtime Database
    private fun checkUserRole() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            database.child("users").child(uid).child("role").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.getValue(String::class.java)
                    when (role) {
                        "admin" -> {
                            enableAdminFeatures()
                        }
                        "user" -> {
                            disableAdminFeatures()
                        }
                        else -> {
                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Không xác định được quyền của bạn!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Lỗi khi lấy dữ liệu role: ${error.message}")
                }
            })
        } else {
            Log.e("FirebaseAuth", "Người dùng chưa đăng nhập!")
        }
    }

    // Kích hoạt các tính năng cho admin
    private fun enableAdminFeatures() {
        findViewById<LinearLayout>(R.id.llMP).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.llUP).visibility = View.GONE
    }

    // Vô hiệu hóa các tính năng chỉ dành cho admin
    private fun disableAdminFeatures() {
        findViewById<LinearLayout>(R.id.llUP).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.llMP).visibility = View.GONE

    }

}
