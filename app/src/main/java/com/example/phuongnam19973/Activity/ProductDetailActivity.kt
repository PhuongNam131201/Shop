package com.example.phuongnam19973.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.phuongnam19973.Adapter.ImageDetailAdapter
import com.example.phuongnam19973.Adapter.SliderAdapter
import com.example.phuongnam19973.R
import com.google.firebase.database.FirebaseDatabase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.text.DecimalFormat

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var imageDetailAdapter: ImageDetailAdapter
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Nhận dữ liệu từ Intent
        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription")
        val productImages = intent.getStringArrayListExtra("productImages")
        val productCategory = intent.getStringArrayListExtra("productCategory")

        // Ánh xạ các view
        val txtNameDetail: TextView = findViewById(R.id.txtNameDetail)
        val txtPriceDetail: TextView = findViewById(R.id.txtPriceDetail)
        val txtDescriptionDetail: TextView = findViewById(R.id.txtDescriptionDetail)
        val slider: ViewPager2 = findViewById(R.id.slider)
        val dotIndicator: DotsIndicator = findViewById(R.id.dotIndicator)
        val colorList: RecyclerView = findViewById(R.id.colorList)
        val btnDelete: ImageView = findViewById(R.id.btnDelete)
        val btnEdit: ImageView = findViewById(R.id.btnEdit)
        // Gán dữ liệu vào TextView
        txtNameDetail.text = productName
        Log.d("ProductDetail", "Product Price: $productPrice")
        // Định dạng số tiền với dấu phân cách hàng nghìn
        val formattedPrice = DecimalFormat("###,###").format(productPrice)

// Gán giá đã được định dạng và thêm "VND" vào cuối
        txtPriceDetail.text = "$formattedPrice VND"
        txtDescriptionDetail.text = productDescription

        // Thiết lập slider nếu có hình ảnh
        if (!productImages.isNullOrEmpty()) {
            sliderAdapter = SliderAdapter(productImages)
            slider.adapter = sliderAdapter

            // Kết nối DotsIndicator với ViewPager2
            dotIndicator.setViewPager2(slider)
            dotIndicator.visibility = View.VISIBLE
        }

        // Hiển thị danh sách màu sắc hoặc các lựa chọn liên quan nếu cần
        if (!productImages.isNullOrEmpty()) {
            imageDetailAdapter = ImageDetailAdapter(productImages) { position ->
                isUserScrolling = true
                slider.setCurrentItem(position, true)
            }

            colorList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            colorList.adapter = imageDetailAdapter
        }

        // Liên kết sự kiện thay đổi trên Slider
        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (!isUserScrolling) {
                    imageDetailAdapter.setSelectedPosition(position)
                }
                isUserScrolling = false
            }
        })

        // Xử lý sự kiện khi nhấn nút xóa
        btnDelete.setOnClickListener {
            productId?.let { id ->
                showDeleteConfirmationDialog(id)
            } ?: run {
                Toast.makeText(this, "Không tìm thấy ID sản phẩm!", Toast.LENGTH_SHORT).show()
            }
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java).apply {
                putExtra("productId", productId)
                putExtra("productName", productName)
                putExtra("productDescription", productDescription)
                putExtra("productPrice", productPrice)
                putExtra("productCategory", productCategory)
            }
            startActivity(intent)
        }

    }
    private fun showDeleteConfirmationDialog(productId: String) {
        // Tạo và hiển thị hộp thoại xác nhận
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteProductFromFirebase(productId)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss() // Đóng hộp thoại nếu nhấn Hủy
            }
            .create()

        alertDialog.show()
    }
    private fun deleteProductFromFirebase(productId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")

        databaseReference.child(productId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                finish() // Đóng màn hình chi tiết sau khi xóa
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Xóa sản phẩm thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
