package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.R
import com.google.firebase.auth.FirebaseAuth

class ForgotAcivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvBackToLogin: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpass)

        // Khởi tạo các thành phần giao diện
        edtEmail = findViewById(R.id.edtEmail)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        // Khởi tạo FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Sự kiện nhấn nút "Gửi yêu cầu"
        btnSubmit.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            }
        }

        // Sự kiện nhấn "Quay lại đăng nhập"
        tvBackToLogin.setOnClickListener {
            finish() // Quay lại màn hình đăng nhập
        }
    }

    // Hàm gửi yêu cầu đặt lại mật khẩu qua email
    private fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đã gửi yêu cầu đặt lại mật khẩu tới email của bạn", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, SignIn::class.java)
                    startActivity(intent)
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(this, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }
}
