package com.example.phuongnam19973

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001 // Mã yêu cầu cho đăng nhập Google
    }

    private lateinit var auth: FirebaseAuth // Khai báo biến FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin) // Thiết lập layout cho activity

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Kiểm tra người dùng hiện tại
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Nếu người dùng đã đăng nhập, chuyển đến MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Đóng activity hiện tại
        }

        // Thiết lập nút đăng nhập Google
        val signInButton = findViewById<LinearLayout>(R.id.GG)
        signInButton.setOnClickListener {
            // Khi nhấn vào nút, gọi hàm đăng nhập bằng Google
            signInWithGoogle()
        }

        // Thiết lập nút đăng nhập bằng email
        val btnSignInEmail = findViewById<Button>(R.id.btnSignIn)
        val edtEmail = findViewById<EditText>(R.id.editSignInEmail)
        val edtPassword = findViewById<EditText>(R.id.editSignInPass)
        val forgotpass = findViewById<TextView>(R.id.txtForgotPass)
        btnSignInEmail.setOnClickListener {
            // Lấy email và mật khẩu từ các ô nhập
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Kiểm tra xem email và mật khẩu có được nhập không
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Dừng thực hiện nếu không có email
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Dừng thực hiện nếu không có mật khẩu
            }

            // Gọi hàm đăng nhập bằng email
            signInWithEmail(email, password)
        }

        // Thiết lập nút đăng ký mới
        val btnSignUp = findViewById<TextView>(R.id.txtSignUp)
        btnSignUp.setOnClickListener {
            // Khi nhấn vào nút, chuyển đến activity đăng ký
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        forgotpass.setOnClickListener{
            val intent = Intent(this, ForgotAcivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        // Cấu hình tùy chọn đăng nhập Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Lấy ID token từ cấu hình
            .requestEmail() // Yêu cầu email
            .build()

        // Tạo đối tượng GoogleSignInClient
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Bắt đầu đăng nhập Google
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // Xử lý kết quả từ đăng nhập Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Lấy thông tin tài khoản từ kết quả
                val account = task.getResult(ApiException::class.java)
                // Gọi hàm xác thực Firebase với ID token
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Thông báo lỗi nếu đăng nhập không thành công
                Toast.makeText(this, "Đăng nhập bằng Google thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        // Lấy credential từ ID token
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // Xác thực người dùng với Firebase
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Nếu xác thực thành công
                    val user = auth.currentUser
                    Toast.makeText(this, "Đăng nhập với ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    // Lưu thông tin người dùng vào Firebase Realtime Database
                    saveUserDataToDatabase(user)

                    // Chuyển đến MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Thông báo lỗi nếu xác thực không thành công
                    Toast.makeText(this, "Xác thực không thành công", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserDataToDatabase(user: FirebaseUser?) {
        if (user != null) {
            // Lấy thông tin người dùng
            val userId = user.uid
            val userName = user.displayName
            val userEmail = user.email

            // Khởi tạo đối tượng Firebase Realtime Database
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userRef = database.child("users").child(userId)

            // Lưu thông tin người dùng vào Firebase Realtime Database
            val userMap = HashMap<String, Any>()
            userMap["name"] = userName ?: ""
            userMap["email"] = userEmail ?: ""
            userMap["role"] = "user" // Thêm role và mặc định là "user"

            userRef.setValue(userMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Thông tin người dùng đã được lưu", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Lỗi khi lưu thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun signInWithEmail(email: String, password: String) {
        // Gọi hàm đăng nhập bằng email và mật khẩu
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Nếu đăng nhập thành công
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    // Chuyển đến MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Thông báo lỗi nếu đăng nhập không thành công
                    Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUpWithEmail(email: String, password: String) {
        // Gọi hàm đăng ký với email và mật khẩu
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Nếu đăng ký thành công
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    // Chuyển đến MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Thông báo lỗi nếu đăng ký không thành công
                    Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
