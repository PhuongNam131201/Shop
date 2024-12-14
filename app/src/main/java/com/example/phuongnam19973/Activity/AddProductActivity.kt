package com.example.phuongnam19973.Activity

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductCategory: EditText
    private lateinit var btnSelectImages: Button
    private lateinit var btnUploadProduct: Button
    private lateinit var gvSelectedImages: GridView

    private var imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_product_edit)

        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductCategory = findViewById(R.id.etProductCategory)
        btnSelectImages = findViewById(R.id.btnSelectImages)
        btnUploadProduct = findViewById(R.id.btnUploadProduct)
        gvSelectedImages = findViewById(R.id.gvSelectedImages)

        imageAdapter = ImageAdapter(this, imageUris)
        gvSelectedImages.adapter = imageAdapter

        btnSelectImages.setOnClickListener {
            selectImages()
        }

        btnUploadProduct.setOnClickListener {
            uploadProduct()
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            imageUris.clear()
            imageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Đã chọn ${imageUris.size} hình ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImages() {
        selectImagesLauncher.launch("image/*")
    }

    private fun uploadProduct() {
        val name = etProductName.text.toString().trim()
        val description = etProductDescription.text.toString().trim()
        val priceText = etProductPrice.text.toString().trim()
        val category = etProductCategory.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "Giá phải là số dương hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một hình ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference
        val storage = FirebaseStorage.getInstance().reference

        // Sử dụng push().key để tạo ID duy nhất
        val productId = database.child("products").push().key

        if (productId == null) {
            Toast.makeText(this, "Lỗi tạo ID sản phẩm", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        for ((index, imageUri) in imageUris.withIndex()) {
            val imageRef = storage.child("products/$productId/image_$index.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                        uploadCount++

                        if (uploadCount == imageUris.size) {
                            // Lưu sản phẩm vào database sau khi tất cả hình ảnh đã được tải lên
                            val product = Product(
                                id = productId,
                                name = name,
                                price = price,
                                category = category,
                                description = description,
                                imageUrl = imageUrls // Chắc chắn rằng đây là danh sách các URL
                            )

                            // Lưu sản phẩm vào Firebase
                            database.child("products").child(productId).setValue(product)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(this@AddProductActivity, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                                        imageUris.clear()
                                        imageAdapter.notifyDataSetChanged()
                                        finish()
                                    } else {
                                        Toast.makeText(this@AddProductActivity, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@AddProductActivity, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
                }
        }
    }



}
