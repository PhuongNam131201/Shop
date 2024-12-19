package com.example.phuongnam19973.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.phuongnam19973.R
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo FirebaseAuth và Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Kiểm tra quyền của người dùng
        checkUserRole()

        // Đăng xuất
        val signOutButton = findViewById<Button>(R.id.btnThongTinCuaHang)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        // Chuyển đến các màn hình khác
        val buy = findViewById<LinearLayout>(R.id.llBuy)
        buy.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val profile = findViewById<LinearLayout>(R.id.llprofile)
        profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val manage = findViewById<LinearLayout>(R.id.llManger)
        manage.setOnClickListener{
            val intent = Intent(this, ManageActivity::class.java)
            startActivity(intent)
        }
        val cart = findViewById<LinearLayout>(R.id.llCart)
        cart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        val bottomcart = findViewById<LinearLayout>(R.id.bottomCart)
        bottomcart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        val chat = findViewById<LinearLayout>(R.id.llChat)
        chat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        val static = findViewById<LinearLayout>(R.id.llStatistics)
        static.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
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
                                this@MainActivity,
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
        findViewById<LinearLayout>(R.id.llBuy).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llCart).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llReport).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llCardUser).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llUserProduct).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llVoucherUser).visibility = View.GONE
        findViewById<LinearLayout>(R.id.bottomCart).visibility = View.GONE

    }

    // Vô hiệu hóa các tính năng chỉ dành cho admin
    private fun disableAdminFeatures() {
        findViewById<LinearLayout>(R.id.llManger).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llSquare).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llStatistics).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llVoucher).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llCard).visibility = View.GONE
        findViewById<LinearLayout>(R.id.llReview).visibility = View.GONE
        findViewById<LinearLayout>(R.id.bottomManager).visibility = View.GONE

    }

    // Đăng xuất người dùng
    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this@MainActivity, SignIn::class.java)
            startActivity(intent)
            finish()
        }
    }
}
