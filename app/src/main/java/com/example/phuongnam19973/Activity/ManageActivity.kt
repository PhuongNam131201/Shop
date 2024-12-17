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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ManageActivity: AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
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
            val intent = Intent(this,OrderAminActivity::class.java)
            startActivity(intent)
        }
    }
}