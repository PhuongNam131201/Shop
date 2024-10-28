package com.example.phuongnam19973

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Biến để quản lý Google Sign-In
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    // Biến để quản lý xác thực Firebase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // Thiết lập cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ID client để nhận token
            .requestEmail() // Yêu cầu email của người dùng
            .build()

        // Khởi tạo GoogleSignInClient với cấu hình trên
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Tìm TextView để hiển thị tên người dùng
        val textView = findViewById<TextView>(R.id.name)

        // Lấy thông tin người dùng từ Firebase Authentication
        val auth = Firebase.auth
        val user = auth.currentUser

        // Kiểm tra nếu người dùng đã đăng nhập
        if (user != null) {
            val userName = user.displayName // Lấy tên người dùng
            textView.text = "Xin chào, " + userName // Hiển thị tên người dùng
        } else {
            // Trường hợp người dùng chưa đăng nhập
            // Có thể xử lý logic khác nếu cần
        }

        // Tìm Button để đăng xuất và thêm sự kiện cho nó
        val sign_out_button = findViewById<Button>(R.id.logout_button)
        sign_out_button.setOnClickListener {
            signOutAndStartSignInActivity() // Gọi hàm đăng xuất và chuyển đến màn hình đăng nhập
        }
        // Lấy thông tin người dùng từ Firebase Authentication
        val usera = FirebaseAuth.getInstance().currentUser

        // Kiểm tra nếu người dùng đã đăng nhập
        if (usera != null) {
            val userName = usera.displayName ?: "Người dùng"
            val userEmail = usera.email ?: "Email không xác định"
            textView.text = "Xin chào, $userName\nEmail: $userEmail" // Hiển thị tên và email người dùng
        } else {
            textView.text = "Bạn chưa đăng nhập"
        }

    }

    // Hàm để đăng xuất người dùng và chuyển đến màn hình SignIn
    private fun signOutAndStartSignInActivity() {
        mAuth.signOut() // Đăng xuất khỏi Firebase

        // Đăng xuất khỏi Google Sign-In
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Sau khi đăng xuất thành công, chuyển đến màn hình SignIn
            val intent = Intent(this@MainActivity, SignIn::class.java)
            startActivity(intent)
            finish() // Kết thúc MainActivity
        }
    }
}
