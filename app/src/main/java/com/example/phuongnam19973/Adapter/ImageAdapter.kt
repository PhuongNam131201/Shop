package com.example.phuongnam19973.Activity

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.gridlayout.widget.GridLayout
import com.bumptech.glide.Glide
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.Toast
import com.example.phuongnam19973.R

class ImageAdapter(private val context: Context, private val imageUris: MutableList<Uri>) : BaseAdapter() {

    override fun getCount(): Int {
        return imageUris.size
    }

    override fun getItem(position: Int): Any {
        return imageUris[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridItemView = inflater.inflate(R.layout.grid_item_image, parent, false)

        val imageView: ImageView = gridItemView.findViewById(R.id.imageView)
        val deleteButton: ImageButton = gridItemView.findViewById(R.id.btnDeleteImage)

        // Hiển thị ảnh
        imageView.setImageURI(imageUris[position])

        // Sự kiện xóa ảnh
        deleteButton.setOnClickListener {
            // Xóa ảnh khỏi danh sách
            imageUris.removeAt(position)
            // Thông báo adapter về sự thay đổi dữ liệu
            notifyDataSetChanged()

            // Hiển thị thông báo
            Toast.makeText(context, "Hình ảnh đã được xóa", Toast.LENGTH_SHORT).show()
        }

        return gridItemView
    }
}

