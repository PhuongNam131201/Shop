package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phuongnam19973.R

class BannerAdapter(private val banners: List<String>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.bannerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val imageUrl = banners[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.b) // Ảnh chờ
            .error(R.drawable.b) // Ảnh lỗi
            .into(holder.imageView)
        holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    override fun getItemCount(): Int = banners.size
}
