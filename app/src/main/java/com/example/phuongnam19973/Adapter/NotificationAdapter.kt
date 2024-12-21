package com.example.phuongnam19973.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Model.Notification
import com.example.phuongnam19973.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.message.text = notification.message

        // Format timestamp to a readable date string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(notification.timestamp))
        holder.timestamp.text = formattedDate
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.notificationMessage)
        val timestamp: TextView = view.findViewById(R.id.notificationTimestamp)
    }

}
