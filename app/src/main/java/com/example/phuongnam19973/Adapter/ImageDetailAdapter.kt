package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.R
import com.squareup.picasso.Picasso

class ImageDetailAdapter(
    private val images: List<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageDetailAdapter.ImageViewHolder>() {

    private var selectedPosition = -1

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Picasso.get().load(images[position]).into(holder.imageView)
        holder.itemView.setOnClickListener { onClick(position) }

        // Làm nổi bật item được chọn
        if (position == selectedPosition) {
            holder.imageView.setBackgroundResource(R.drawable.selected_border)
        } else {
            holder.imageView.setBackgroundResource(0)
        }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageDetail)
    }
}
