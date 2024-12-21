package com.example.phuongnam19973.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.CartAdapter
import com.example.phuongnam19973.Model.CartItem
import com.example.phuongnam19973.Model.Notification
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.R
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalAmountTextView: TextView
    private lateinit var checkoutButton: Button

    private val cartItems = mutableListOf<CartItem>()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        cartAdapter = CartAdapter(cartItems, ::updateCartItem)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        loadCartItems()

        checkoutButton.setOnClickListener {
            showPaymentDialog()

        }
    }

    private fun loadCartItems() {
        if (userId == null) return
        database.child("carts").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                var totalAmount = 0.0

                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        cartItems.add(cartItem)
                        totalAmount += cartItem.price * cartItem.quantity
                    }
                }
                cartAdapter.notifyDataSetChanged()
                val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalAmount)
                totalAmountTextView.text = "Tổng tiền: ${formattedPrice} VND"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun updateCartItem(cartItem: CartItem, newQuantity: Int) {
        if (userId == null) return
        if (newQuantity <= 0) {
            database.child("carts").child(userId).child(cartItem.id).removeValue()
        } else {
            database.child("carts").child(userId).child(cartItem.id).child("quantity").setValue(newQuantity)
        }
    }

    private fun clearCart() {
        if (userId == null) return
        database.child("carts").child(userId).removeValue()
    }
    private fun showPaymentDialog() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn trống! Vui lòng thêm sản phẩm vào giỏ hàng.", Toast.LENGTH_SHORT).show()
            return
        }
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null)
        val addressInput = dialogView.findViewById<EditText>(R.id.addressInputPay)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phoneInputPay)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButtonPay)
        val contentInput = dialogView.findViewById<EditText>(R.id.contentInputPay)
        val imgAddress = dialogView.findViewById<ImageView>(R.id.imgAddressInput) // Nút lấy vị trí hiện tại

        val databaseRef = FirebaseDatabase.getInstance().reference
        val userRef = databaseRef.child("users").child(userId ?: "")

        // Lấy dữ liệu phone từ Firebase
        userRef.get().addOnSuccessListener { snapshot ->
            val phone = snapshot.child("phone").getValue(String::class.java)
            if (!phone.isNullOrEmpty()) {
                phoneInput.setText(phone)
                phoneInput.isEnabled = false // Không cho chỉnh sửa
            }
        }

        // Xử lý khi chọn nút "Lấy vị trí hiện tại"
        imgAddress.setOnClickListener {
            fetchCurrentLocation { address ->
                addressInput.setText(address) // Gán địa chỉ vào EditText
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        confirmButton.setOnClickListener {
            val address = addressInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val content = contentInput.text.toString().trim()

            if (address.isNotEmpty() && phone.isNotEmpty()) {
                saveOrderToDatabase(address, phone, content)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    // Hàm lấy vị trí hiện tại và chuyển thành địa chỉ
    private fun fetchCurrentLocation(callback: (String) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // Sử dụng requestLocationUpdates để lấy vị trí mới
        fusedLocationClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.create().apply {
                priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
                interval = 5000 // 5 giây cập nhật một lần
                fastestInterval = 2000 // Tốc độ nhanh nhất là 2 giây
            },
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val geocoder = Geocoder(this@CartActivity, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val fullAddress = addresses[0].getAddressLine(0) // Địa chỉ đầy đủ
                            callback(fullAddress)
                        } else {
                            Toast.makeText(this@CartActivity, "Không tìm thấy địa chỉ!", Toast.LENGTH_SHORT).show()
                        }
                        fusedLocationClient.removeLocationUpdates(this) // Hủy cập nhật sau khi lấy xong
                    }
                }
            },
            mainLooper
        )
    }
    // Lưu đơn hàng vào Firebase với ID tự động
    private fun saveOrderToDatabase(address: String, phone: String, content: String) {
        if (userId == null) return

        val totalAmount = cartItems.sumOf { it.price * it.quantity }
        val order = Order(
            userId = userId,
            items = cartItems,
            content = content,
            totalAmount = totalAmount,
            address = address,
            phoneNumber = phone,
            status = "Chờ xác nhận"  // Bạn có thể thay đổi status nếu cần
        )

        // Tạo ID tự động cho đơn hàng bằng phương thức push()
        val orderRef = database.child("orders").push()  // Tạo ID tự động
        val orderId = orderRef.key  // Lấy ID tự động được tạo ra

        // Cập nhật ID vào đối tượng Order
        val orderWithId = order.copy(id = orderId ?: "")

        // Lưu đơn hàng vào Firebase
        orderRef.setValue(orderWithId).addOnSuccessListener {
            // Tạo thông báo với timestamp và userId
            val notification = Notification(
                message = "Đơn hàng của bạn đã được đặt thành công với tổng tiền: ${formatPrice(totalAmount)}",
                userId = userId,
                timestamp = System.currentTimeMillis()  // Gán timestamp là thời gian hiện tại
            )

            // Lưu thông báo vào Firebase dưới nhánh "notifications"
            // Lưu trực tiếp vào "notifications" mà không phân nhánh theo userId
            database.child("notifications")
                .push()  // Tạo ID tự động cho thông báo
                .setValue(notification)

            // Hiển thị Toast cho người dùng
            Toast.makeText(this, "Đặt hàng thành công, vui lòng kiểm tra đơn mua và thông báo!", Toast.LENGTH_SHORT).show()

            // Chuyển sang Activity khác
            val intent = Intent(this, OrderWaitingActivity::class.java)
            intent.putExtra("totalAmount", totalAmount)
            startActivity(intent)

            // Xóa giỏ hàng
            clearCart()

        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatPrice(price: Double): String {
        val locale = Locale("vi", "VN")  // Cài đặt locale cho Việt Nam
        val numberFormat = NumberFormat.getInstance(locale)
        return numberFormat.format(price)
    }


}