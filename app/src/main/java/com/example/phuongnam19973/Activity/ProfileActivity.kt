package com.example.phuongnam19973.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

class ProfileActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // Khởi tạo FirebaseAuth và Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //Chuyen
        val signOutButton = findViewById<ImageView>(R.id.imgLogout)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }
        val back = findViewById<ImageView>(R.id.imgbackProfile)
        back.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        // Lấy thông tin người dùng từ Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val textView = findViewById<TextView>(R.id.name)
        val name = findViewById<TextView>(R.id.dob)
        val emaila = findViewById<TextView>(R.id.gender)
        val phone = findViewById<TextView>(R.id.contactNumber)
        val role = findViewById<TextView>(R.id.location)

        checkUserRole()

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
                        textView.text = userName
                        name.text = userName
                        emaila.text = userEmail
                        phone.text = userPhone
                        if (userRole == "user"){
                            role.text = "Người dùng"
                        }
                        else {
                            role.text = "Quản lý"
                        }

                    } else {
                        textView.text = "Người dùng"
                        name.text = ""
                        emaila.text = ""
                        phone.text = ""
                        role.text = ""
                    }
                } else {
                    // Nếu có lỗi khi lấy dữ liệu
                    textView.text = "Lỗi khi lấy thông tin người dùng"
                    name.text = "Lỗi khi lấy thông tin người dùng"
                    emaila.text = "Lỗi khi lấy thông tin người dùng"
                    phone.text = "Lỗi khi lấy thông tin người dùng"
                    role.text = "Lỗi khi lấy thông tin người dùng"
                }
            }
        } else {
            // Nếu người dùng chưa đăng nhập
            textView.text = "Bạn chưa đăng nhập"
            name.text = "Bạn chưa đăng nhập"
            emaila.text = "Bạn chưa đăng nhập"
            phone.text = "Bạn chưa đăng nhập"
            role.text = "Bạn chưa đăng nhập"
        }
    }

    // Đăng xuất người dùng
    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@ProfileActivity, SignIn::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Đăng xuất thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show()
            }
        }

    }
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
                                this@ProfileActivity,
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
        findViewById<LinearLayout>(R.id.bottomCart).visibility = View.GONE

    }

    // Vô hiệu hóa các tính năng chỉ dành cho admin
    private fun disableAdminFeatures() {
        findViewById<LinearLayout>(R.id.bottomManager).visibility = View.GONE

    }
}
