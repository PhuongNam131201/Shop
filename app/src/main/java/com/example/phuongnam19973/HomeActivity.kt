package com.example.phuongnam19973

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activit_product)

        // Tìm TextView để hiển thị tên người dùng
        val textView = findViewById<TextView>(R.id.txtUserProduct)

        // Lấy thông tin người dùng từ Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

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

                    // Hiển thị tên người dùng và email
                    if (userName != null) {
                        textView.text = userName
                    } else {
                        textView.text = "Người dùng"
                    }
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
