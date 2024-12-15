        package com.example.phuongnam19973.Activity

        import android.net.Uri
        import android.os.Bundle
        import android.widget.*
        import androidx.activity.result.contract.ActivityResultContracts
        import androidx.appcompat.app.AppCompatActivity
        import com.example.phuongnam19973.R
        import com.example.phuongnam19973.Model.Product
        import com.google.firebase.database.FirebaseDatabase
        import com.google.firebase.database.DataSnapshot
        import com.google.firebase.database.DatabaseError
        import com.google.firebase.database.ValueEventListener
        import com.google.firebase.storage.FirebaseStorage

        class EditProductActivity : AppCompatActivity() {

            private lateinit var etProductName: EditText
            private lateinit var etProductDescription: EditText
            private lateinit var etProductPrice: EditText
            private lateinit var etProductCategory: EditText
            private lateinit var btnSelectImages: Button
            private lateinit var gvSelectedImages: GridView
            private lateinit var btnUploadProduct: Button

            private var imageUris: MutableList<Uri> = mutableListOf()
            private lateinit var imageAdapter: ImageAdapter
            private lateinit var productId: String

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_manager_product_edit_a)

                // Ánh xạ các view
                etProductName = findViewById(R.id.etProductNamea)
                etProductDescription = findViewById(R.id.etProductDescriptiona)
                etProductPrice = findViewById(R.id.etProductPricea)
                etProductCategory = findViewById(R.id.etProductCategorya)
                btnSelectImages = findViewById(R.id.btnSelectImagesa)
                gvSelectedImages = findViewById(R.id.gvSelectedImagesa)
                btnUploadProduct = findViewById(R.id.btnUploadProducta)

                imageAdapter = ImageAdapter(this, imageUris)
                gvSelectedImages.adapter = imageAdapter

                // Lấy thông tin sản phẩm từ Intent
                productId = intent.getStringExtra("productId") ?: ""
                if (productId.isNotEmpty()) {
                    loadProductData(productId)
                }

                // Chọn ảnh mới
                btnSelectImages.setOnClickListener {
                    selectImages()
                }

                // Cập nhật sản phẩm
                btnUploadProduct.setOnClickListener {
                    updateProduct()
                }
            }

            // Tải dữ liệu sản phẩm và hiển thị ảnh hiện tại
            private fun loadProductData(productId: String) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val product = snapshot.getValue(Product::class.java)
                        if (product != null) {
                            // Điền thông tin sản phẩm vào các EditText
                            etProductName.setText(product.name)
                            etProductDescription.setText(product.description)
                            etProductPrice.setText(product.price.toString())
                            etProductCategory.setText(product.category)

                            // Xóa danh sách ảnh hiện tại để tránh bị trùng
                            imageUris.clear()

                            // Thêm các ảnh hiện có từ Firebase vào danh sách
                            product.imageUrl?.forEach { imageUrl ->
                                imageUris.add(Uri.parse(imageUrl)) // Chuyển đổi từ URL sang Uri
                            }

                            // Cập nhật lại GridView để hiển thị ảnh hiện có
                            imageAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@EditProductActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (uris.isNotEmpty()) {
                    imageUris.addAll(uris)  // Thêm ảnh mới vào danh sách
                    imageAdapter.notifyDataSetChanged()  // Cập nhật GridView
                    Toast.makeText(this, "Đã chọn ${imageUris.size} hình ảnh", Toast.LENGTH_SHORT).show()
                }
            }

            private fun selectImages() {
                selectImagesLauncher.launch("image/*")
            }

            private fun updateProduct() {
                val updatedName = etProductName.text.toString()
                val updatedDescription = etProductDescription.text.toString()
                val updatedPrice = etProductPrice.text.toString().toDoubleOrNull() ?: 0.0
                val updatedCategory = etProductCategory.text.toString()

                if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedCategory.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val databaseReference = FirebaseDatabase.getInstance().getReference("products")
                val updates = mapOf(
                    "name" to updatedName,
                    "description" to updatedDescription,
                    "price" to updatedPrice,
                    "category" to updatedCategory
                )

                databaseReference.child(productId).updateChildren(updates).addOnSuccessListener {
                    uploadImages(productId)
                }.addOnFailureListener {
                    Toast.makeText(this, "Lỗi cập nhật sản phẩm: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

            private fun uploadImages(productId: String) {
                val storageReference = FirebaseStorage.getInstance().reference
                val imageUrls = mutableListOf<String>()
                var uploadCount = 0

                for ((index, uri) in imageUris.withIndex()) {
                    val imageRef = storageReference.child("products/$productId/image_$index.jpg")
                    imageRef.putFile(uri)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { url ->
                                imageUrls.add(url.toString())
                                uploadCount++

                                if (uploadCount == imageUris.size) {
                                    FirebaseDatabase.getInstance().getReference("products")
                                        .child(productId)
                                        .child("imageUrl")
                                        .setValue(imageUrls)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Lỗi tải ảnh: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

