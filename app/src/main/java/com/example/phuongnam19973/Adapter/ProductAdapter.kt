package com.example.phuongnam19973.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.google.android.material.imageview.ShapeableImageView
import java.text.DecimalFormat

class ProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ShapeableImageView = itemView.findViewById(R.id.imgProduct)
        val productName: TextView = itemView.findViewById(R.id.txtProductName)
        val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Giới hạn ký tự của tên sản phẩm và thêm dấu ba chấm nếu cần
        val maxLength = 20 // Số ký tự tối đa cho tên sản phẩm
        val truncatedName = if (product.name.length > maxLength) {
            product.name.substring(0, maxLength) + "..."  // Cắt tên sản phẩm và thêm "..."
        } else {
            product.name
        }
        holder.productName.text = truncatedName

        // Đảm bảo price là kiểu Double, sau đó định dạng giá trị
        val formattedPrice = DecimalFormat("###,###").format(product.price)
        holder.productPrice.text = "$formattedPrice ₫"  // Hiển thị giá với dấu phân cách hàng nghìn và đơn vị tiền tệ VND

        // Kiểm tra nếu danh sách imageUrl không trống
        if (product.imageUrl.isNotEmpty()) {
            // Sử dụng Glide để tải hình ảnh từ URL Firebase Storage (ví dụ tải hình ảnh đầu tiên)
            Glide.with(holder.productImage.context)
                .load(product.imageUrl[0])  // Lấy URL đầu tiên từ danh sách
                .apply(RequestOptions.centerCropTransform())  // Cắt hình ảnh theo tỷ lệ thích hợp
                .into(holder.productImage)
        } else {
            // Nếu không có URL, tải hình ảnh mặc định
            Glide.with(holder.productImage.context)
                .load(R.drawable.box)  // placeholder_image là một hình ảnh mặc định
                .into(holder.productImage)
        }

        // Cài đặt sự kiện cho các nút sửa và xóa


    }



    override fun getItemCount(): Int {
        return productList.size
    }
}
