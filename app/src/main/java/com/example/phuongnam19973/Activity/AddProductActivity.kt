package com.example.phuongnam19973.Activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var btnSelectImages: ImageView
    private lateinit var btnUploadProduct: Button
    private lateinit var gvSelectedImages: GridView
    private lateinit var spinnerProductCategory: Spinner
    private lateinit var iconTypeAdd: ImageView

    private var imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_product_edit)

        // Khởi tạo các view
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductPrice = findViewById(R.id.etProductPrice)
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory)
        btnSelectImages = findViewById(R.id.btnSelectImages)
        btnUploadProduct = findViewById(R.id.btnUploadProduct)
        gvSelectedImages = findViewById(R.id.gvSelectedImages)
        iconTypeAdd = findViewById(R.id.iconTypeAdd)

        imageAdapter = ImageAdapter(this, imageUris)
        gvSelectedImages.adapter = imageAdapter

        btnSelectImages.setOnClickListener {
            selectImages()
        }

        btnUploadProduct.setOnClickListener {
            uploadProduct()
        }

        // Cài đặt danh sách các danh mục sản phẩm
        val categories = arrayOf("Samsung", "Iphone", "Huewai", "Oppo")

        // Tạo ArrayAdapter và gán vào Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCategory.adapter = adapter

        // Thiết lập sự kiện khi người dùng chọn mục trong Spinner
        spinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Thay đổi hình ảnh dựa trên mục đã chọn
                when (position) {
                    0 -> iconTypeAdd.setImageResource(R.drawable.image3)
                    1 -> iconTypeAdd.setImageResource(R.drawable.image2)
                    2 -> iconTypeAdd.setImageResource(R.drawable.image1)
                    3 -> iconTypeAdd.setImageResource(R.drawable.op)
                    else -> iconTypeAdd.setImageResource(R.drawable.info)  // Khác
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nếu không có lựa chọn nào
            }
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
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
        val category = spinnerProductCategory.selectedItem.toString().trim()

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

        // Tạo ID duy nhất cho sản phẩm
        val productId = database.child("products").push().key

        if (productId == null) {
            Toast.makeText(this, "Lỗi tạo ID sản phẩm", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        // Tải ảnh lên Firebase Storage
        for ((index, imageUri) in imageUris.withIndex()) {
            val imageRef = storage.child("products/$productId/image_$index.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                        uploadCount++

                        if (uploadCount == imageUris.size) {
                            // Lưu sản phẩm vào Firebase sau khi tất cả hình ảnh đã được tải lên
                            val product = Product(
                                id = productId,
                                name = name,
                                price = price,
                                category = category,
                                description = description,
                                imageUrl = imageUrls
                            )

                            // Lưu vào database
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
