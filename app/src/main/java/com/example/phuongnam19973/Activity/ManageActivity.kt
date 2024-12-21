package com.example.phuongnam19973.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import java.text.NumberFormat
import java.util.Locale

class ManageActivity: AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var txtTongSP: TextView
    private lateinit var txtTongNguoiDung: TextView
    private lateinit var txtTongDoanhThu: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        // Khởi tạo FirebaseAuth và Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // Lấy thông tin người dùng từ Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val name = findViewById<TextView>(R.id.txtNamManage)
        val role = findViewById<TextView>(R.id.txtRoleManage)
        txtTongSP = findViewById(R.id.txtTongSP)  // Giả sử bạn đã thêm ID txtTongSP vào layout
        txtTongNguoiDung = findViewById(R.id.txtTongNguoiDung)
        txtTongDoanhThu = findViewById(R.id.txtTongDoanhThu)

        getProductCount()

        // Lấy tổng số người dùng từ Firebase Realtime Database
        getUserCount()
        getOrderRevenue()

        // Kiểm tra nếu người dùng đã đăng nhập
        if (user != null) {
            // Lấy userId từ FirebaseAuth
            val userId = user.uid

            // Lấy thông tin người dùng từ Realtime Database
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userRef = database.child("users").child(userId)

            // Lấy dữ liệu người dùng từ Realtime Database
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Nếu lấy dữ liệu thành công
                    val userData = task.result
                    val userName = userData?.child("name")?.getValue(String::class.java)
                    val userEmail = userData?.child("email")?.getValue(String::class.java)
                    val userPhone = userData?.child("phone")?.getValue(String::class.java)
                    val userRole = userData?.child("role")?.getValue(String::class.java)
                    // Hiển thị tên người dùng và email
                    if (userName != null) {
                        name.text = userName
                        if (userRole == "user"){
                            role.text = "Người dùng"
                        }
                        else {
                            role.text = "Quản lý"
                        }

                    } else {
                        name.text = ""
                        role.text = ""
                    }
                } else {
                    // Nếu có lỗi khi lấy dữ liệu
                    name.text = "Lỗi khi lấy thông tin người dùng"
                    role.text = "Lỗi khi lấy thông tin người dùng"
                }
            }
        } else {
            // Nếu người dùng chưa đăng nhập
            name.text = "Bạn chưa đăng nhập"
            role.text = "Bạn chưa đăng nhập"
        }
        //Chuyen
        val back = findViewById<ImageView>(R.id.imgbackmanage)
        back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val profile = findViewById<Button>(R.id.btnProfileManager)
        profile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val product = findViewById<LinearLayout>(R.id.llProductManage)
        product.setOnClickListener{
            val intent = Intent(this,ManageProductActivity::class.java)
            startActivity(intent)
        }
        val cart = findViewById<LinearLayout>(R.id.llCartManage)
        cart.setOnClickListener{
            val intent = Intent(this,OrderAdminActivity::class.java)
            startActivity(intent)
        }
        val dv = findViewById<LinearLayout>(R.id.llServiceManager)
        dv.setOnClickListener{
            val intent = Intent(this,BannerAdminActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getProductCount() {
        val productsRef = database.child("products")
        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Đếm số lượng sản phẩm
                val productCount = snapshot.childrenCount
                txtTongSP.text = "$productCount"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Lấy tổng số người dùng trong Firebase
    private fun getUserCount() {
        val usersRef = database.child("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Đếm số lượng người dùng
                val userCount = snapshot.childrenCount
                txtTongNguoiDung.text = "$userCount"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    private fun getOrderRevenue() {
        val ordersRef = database.child("orders")
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRevenue = 0.0
                for (orderSnapshot in snapshot.children) {
                    val itemsSnapshot = orderSnapshot.child("items")
                    for (itemSnapshot in itemsSnapshot.children) {
                        val itemPrice = itemSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                        val itemQuantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                        totalRevenue += itemPrice * itemQuantity
                    }
                }
                txtTongDoanhThu.text = "${formatPrice(totalRevenue)} VND"
            }

            override fun onCancelled(error: DatabaseError) {
                txtTongDoanhThu.text = "Không thể lấy tổng doanh thu"
            }
        })
    }
    private fun formatPrice(price: Double): String {
        val locale = Locale("vi", "VN")  // Cài đặt locale cho Việt Nam
        val numberFormat = NumberFormat.getInstance(locale)
        return numberFormat.format(price)
    }

}