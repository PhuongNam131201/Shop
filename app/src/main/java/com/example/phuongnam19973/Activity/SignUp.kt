package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.util.Patterns
import com.example.phuongnam19973.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnSignUp: AppCompatButton

    private lateinit var errorEmail: TextView
    private lateinit var errorPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Khởi tạo các thành phần
        edtName = findViewById(R.id.editProfile)
        edtPhone = findViewById(R.id.editPhone)
        edtEmail = findViewById(R.id.editTdn)
        edtPassword = findViewById(R.id.editMk)
        edtConfirmPassword = findViewById(R.id.editMk2)
        btnSignUp = findViewById(R.id.btnSignUp)

        errorEmail = findViewById(R.id.errorEmail)
        errorPassword = findViewById(R.id.errorPassword)

        // Thêm TextWatcher cho các EditText
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail() // Kiểm tra email ngay khi người dùng nhập
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword() // Kiểm tra mật khẩu ngay khi người dùng nhập
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateConfirmPassword(edtPassword.text.toString(), edtConfirmPassword.text.toString()) // Kiểm tra mật khẩu xác nhận
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            // Gọi hàm đăng ký
            if (validateEmail() && validatePassword() && validateConfirmPassword(password, confirmPassword)) {
                signUpWithEmail(email, password, name, phone)
            }
        }

        val btnSignIn = findViewById<TextView>(R.id.txtSignIn)
        btnSignIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }

    private fun validateEmail(): Boolean {
        val email = edtEmail.text.toString()
        return if (!isEmailValid(email)) {
            errorEmail.text = "Email không hợp lệ"
            errorEmail.visibility = TextView.VISIBLE
            false
        } else {
            errorEmail.visibility = TextView.GONE
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = edtPassword.text.toString()
        return if (!isPasswordValid(password)) {
            errorPassword.text = "Mật khẩu phải có ít nhất 6 ký tự"
            errorPassword.visibility = TextView.VISIBLE
            false
        } else {
            errorPassword.visibility = TextView.GONE
            true
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            errorPassword.text = "Mật khẩu không khớp"
            errorPassword.visibility = TextView.VISIBLE
            false
        } else {
            errorPassword.visibility = TextView.GONE
            true
        }
    }



    // Bổ sung biến Firestore
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference // Khởi tạo database reference

    private fun signUpWithEmail(email: String, password: String, name: String, phone: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đăng ký thành công
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    // Gửi email xác nhận
                    currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                showMessage("Đăng ký thành công! Vui lòng kiểm tra email để xác nhận.")
                            } else {
                                showMessage("Không thể gửi email xác nhận: ${emailTask.exception?.message}")
                            }
                        }

                    // Tạo đối tượng người dùng
                    val user = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to email,
                        "role" to "user"  // Vai trò mặc định là "user"
                    )

                    // Lưu thông tin vào Realtime Database
                    userId?.let {
                        database.child("users").child(it).setValue(user)
                            .addOnSuccessListener {
                                showMessage("Thông tin đã được lưu vào Realtime Database")
                            }
                            .addOnFailureListener { e ->
                                showMessage("Lưu thông tin thất bại: ${e.message}")
                            }
                    }
                } else {
                    // Thông báo lỗi
                    showMessage("Đăng ký không thành công: ${task.exception?.message}")
                }
            }
    }




    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6 // Độ dài tối thiểu
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
