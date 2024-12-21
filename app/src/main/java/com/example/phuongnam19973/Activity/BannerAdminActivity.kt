package com.example.phuongnam19973.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.BannerAdminAdapter
import com.example.phuongnam19973.Model.Banner
import com.example.phuongnam19973.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class BannerAdminActivity : AppCompatActivity() {

    private lateinit var edtBannerId: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnAddBanner: Button
    private lateinit var btnDeleteBanner: Button
    private lateinit var recyclerBanner: RecyclerView
    private lateinit var bannerAdapter: BannerAdminAdapter
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private val banners = mutableListOf<Banner>()
    private lateinit var imgPreviewBanner: ImageView  // ImageView để hiển thị ảnh đã chọn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_banner)

        // Ánh xạ các view
        database = FirebaseDatabase.getInstance().reference.child("banners")
        storage = FirebaseStorage.getInstance()

        edtBannerId = findViewById(R.id.edtBannerIdAdmin)
        btnSelectImage = findViewById(R.id.btnSelectImageAdmin)
        btnAddBanner = findViewById(R.id.btnAddBannerAdmin)
        btnDeleteBanner = findViewById(R.id.btnDeleteBannerAdmin)
        recyclerBanner = findViewById(R.id.recyclerBannerAdmin)
        imgPreviewBanner = findViewById(R.id.imgPreviewBanner)  // Ánh xạ ImageView

        recyclerBanner.layoutManager = LinearLayoutManager(this)
        bannerAdapter = BannerAdminAdapter(banners) { id -> deleteBanner(id) }
        recyclerBanner.adapter = bannerAdapter

        loadBanners()

        // Chọn ảnh
        btnSelectImage.setOnClickListener {
            selectImage()
        }

        // Thêm banner
        btnAddBanner.setOnClickListener {
            val id = edtBannerId.text.toString()
            if (id.isNotEmpty() && selectedImageUri != null) {
                uploadImageToStorage(id, selectedImageUri!!)
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh và nhập ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Xóa banner
        btnDeleteBanner.setOnClickListener {
            val id = edtBannerId.text.toString()
            if (id.isNotEmpty()) {
                deleteBanner(id)
            } else {
                Toast.makeText(this, "Vui lòng nhập ID để xóa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load banners từ Firebase
    private fun loadBanners() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                banners.clear()
                for (data in snapshot.children) {
                    val banner = data.getValue(Banner::class.java)
                    if (banner != null) {
                        banners.add(banner)
                    }
                }
                bannerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BannerAdminActivity, "Lỗi khi tải banners", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Chọn ảnh từ bộ nhớ và cập nhật ImageView
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    // Tải ảnh lên Firebase Storage
    private fun uploadImageToStorage(id: String, imageUri: Uri) {
        val imageRef = storage.reference.child("banners/$id.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    addBannerToDatabase(id, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
            }
    }

    // Thêm banner vào Firebase
    private fun addBannerToDatabase(id: String, imageUrl: String) {
        val banner = Banner(id.toInt(), imageUrl)
        database.child(id).setValue(banner)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm banner thành công", Toast.LENGTH_SHORT).show()
                edtBannerId.text.clear()
                selectedImageUri = null
                imgPreviewBanner.setImageURI(null)  // Xóa hình ảnh đã chọn
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi thêm banner", Toast.LENGTH_SHORT).show()
            }
    }

    // Xóa banner từ Firebase
    private fun deleteBanner(id: String) {
        database.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Xóa banner thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi xóa banner", Toast.LENGTH_SHORT).show()
            }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                imgPreviewBanner.setImageURI(selectedImageUri)  // Cập nhật ImageView với ảnh đã chọn
                Toast.makeText(this, "Ảnh đã được chọn", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Không có ảnh được chọn", Toast.LENGTH_SHORT).show()
            }
        }
}

