package com.example.phuongnam19973.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.Adapter.NotificationAdapter
import com.example.phuongnam19973.Model.Notification
import com.example.phuongnam19973.R
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: MutableList<Notification>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notificationList = mutableListOf()

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance().getReference("notifications")

        // Load notifications from Firebase
        loadNotifications()
    }

    private fun loadNotifications() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (notificationSnapshot in snapshot.children) {
                    val notification = notificationSnapshot.getValue(Notification::class.java)
                    if (notification != null) {
                        notificationList.add(notification)
                    }
                }
                notificationAdapter = NotificationAdapter(notificationList)
                recyclerView.adapter = notificationAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
