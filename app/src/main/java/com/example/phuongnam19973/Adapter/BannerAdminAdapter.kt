package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phuongnam19973.Model.Banner
import com.example.phuongnam19973.R

class BannerAdminAdapter(
    private val banners: MutableList<Banner>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<BannerAdminAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_admin, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.bind(banner)
    }

    override fun getItemCount(): Int = banners.size

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bannerImageView: ImageView = itemView.findViewById(R.id.bannerImageAdmin)
        private val bannerIdTextView: TextView = itemView.findViewById(R.id.bannerIdAdmin)
        private val deleteButton: Button = itemView.findViewById(R.id.btnDeleteBannerAdmin)

        fun bind(banner: Banner) {
            // Set dữ liệu cho các views
            bannerIdTextView.text = banner.id.toString()
            Glide.with(itemView.context)
                .load(banner.imageUrl)
                .into(bannerImageView)

            // Set sự kiện xóa banner
            deleteButton.setOnClickListener {
                onDeleteClick(banner.id.toString())
            }
        }
    }
}

