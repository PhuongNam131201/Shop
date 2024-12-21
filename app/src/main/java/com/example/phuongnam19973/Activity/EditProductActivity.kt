package com.example.phuongnam19973.Activity

import android.net.Uri
import android.os.Bundle
import android.view.View
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
    private lateinit var btnSelectImages: ImageView
    private lateinit var btnUploadProduct: Button
    private lateinit var gvSelectedImages: GridView
    private lateinit var spinnerProductCategory: Spinner
    private lateinit var iconTypeAdd: ImageView

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
        spinnerProductCategory = findViewById(R.id.spinnerProductCategorya)
        btnSelectImages = findViewById(R.id.btnSelectImagesa)
        gvSelectedImages = findViewById(R.id.gvSelectedImagesa)
        btnUploadProduct = findViewById(R.id.btnUploadProducta)
        iconTypeAdd = findViewById(R.id.iconTypeAdda)

        imageAdapter = ImageAdapter(this, imageUris)
        gvSelectedImages.adapter = imageAdapter

        productId = intent.getStringExtra("productId") ?: ""
        if (productId.isNotEmpty()) {
            loadProductData(productId)
        }

        btnSelectImages.setOnClickListener {
            selectImages()
        }

        btnUploadProduct.setOnClickListener {
            updateProduct()
        }

        val categories = arrayOf("Samsung", "Iphone", "Huawei", "Oppo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductCategory.adapter = adapter

        spinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> iconTypeAdd.setImageResource(R.drawable.image3)
                    1 -> iconTypeAdd.setImageResource(R.drawable.image2)
                    2 -> iconTypeAdd.setImageResource(R.drawable.image1)
                    3 -> iconTypeAdd.setImageResource(R.drawable.op)
                    else -> iconTypeAdd.setImageResource(R.drawable.info)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadProductData(productId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                if (product != null) {
                    // Điền thông tin vào EditText
                    etProductName.setText(product.name)
                    etProductDescription.setText(product.description)
                    etProductPrice.setText(product.price.toString())

                    // Cập nhật Spinner với category
                    val categoryPosition = (spinnerProductCategory.adapter as ArrayAdapter<String>)
                        .getPosition(product.category)
                    if (categoryPosition >= 0) {
                        spinnerProductCategory.setSelection(categoryPosition)
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            imageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Đã chọn ${imageUris.size} hình ảnh", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Chưa chọn ảnh nào", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImages() {
        selectImagesLauncher.launch("image/*")
    }

    private fun updateProduct() {
        val updatedName = etProductName.text.toString()
        val updatedDescription = etProductDescription.text.toString()
        val updatedPrice = etProductPrice.text.toString().toDoubleOrNull()
        val updatedCategory = spinnerProductCategory.selectedItem.toString().trim()

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedCategory.isEmpty() || updatedPrice == null) {
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

        }
    }

    private fun uploadImages(productId: String) {
        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Không có ảnh nào được chọn để tải lên", Toast.LENGTH_SHORT).show()
            return
        }

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
