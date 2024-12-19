package com.example.phuongnam19973.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phuongnam19973.Adapter.MessagingAdapter
import com.example.phuongnam19973.R
import com.example.phuongnam19973.Model.Message
import com.example.phuongnam19973.utils.Constants.RECEIVE_ID
import com.example.phuongnam19973.utils.Constants.SEND_ID
import com.example.phuongnam19973.utils.BotResponse
import com.example.phuongnam19973.utils.Constants.OPEN_GOOGLE
import com.example.phuongnam19973.utils.Constants.OPEN_SEARCH
import com.example.phuongnam19973.utils.Time
import kotlinx.coroutines.*
import com.example.phuongnam19973.databinding.ActivityChatBinding
import com.example.phuongnam19973.utils.SolveMath

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding // Khai báo biến binding
    private lateinit var adapter: MessagingAdapter


    // Khai báo danh sách tin nhắn
    private val messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sử dụng View Binding để thay thế setContentView
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo RecyclerView
        recyclerView()

        // Xử lý các sự kiện click
        clickEvents()

        // Gửi tin nhắn chào mừng

        customBotMessage("Chào bạn! Mình là  NamShopAI, tôi có thể giúp gì cho bạn?")
    }

    private fun clickEvents() {
        // Gửi tin nhắn khi người dùng nhấn nút gửi
        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        // Cuộn xuống khi người dùng nhấn vào ô nhập tin nhắn
        binding.etMessage.setOnClickListener {
            lifecycleScope.launch {
                delay(100)
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun recyclerView() {
        // Khởi tạo adapter cho RecyclerView
        adapter = MessagingAdapter()
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        // Cuộn xuống dưới khi hoạt động được mở lại
        lifecycleScope.launch {
            delay(100)
            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            // Thêm tin nhắn vào danh sách tin nhắn
            messagesList.add(Message(message, SEND_ID, timeStamp))
            binding.etMessage.setText("")

            // Thêm tin nhắn vào adapter và cuộn xuống dưới
            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

            // Tạo phản hồi từ bot
            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        lifecycleScope.launch {
            // Giả lập độ trễ của phản hồi
            delay(1000)

            // Kiểm tra nếu tin nhắn chứa phép toán
            if (message.contains("+") || message.contains("-") || message.contains("*") || message.contains("/")) {
                // Lấy biểu thức toán học từ câu hỏi
                val equation = message.trim()
                val result = SolveMath.solveMath(equation)

                // Gửi kết quả tính toán về cho người dùng
                val response = "Kết quả của phép toán $equation là: $result"
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            } else {
                // Nếu không phải phép toán, sử dụng phản hồi mặc định của bot
                val response = BotResponse.basicResponses(message)
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

                // Xử lý các phản hồi đặc biệt từ bot
                handleSpecialResponses(response, message)
            }
        }
    }
    private fun handleSpecialResponses(response: String, message: String) {
        when (response) {
            OPEN_GOOGLE -> {
                val site = Intent(Intent.ACTION_VIEW)
                site.data = Uri.parse("https://www.google.com/")
                startActivity(site)
            }
            OPEN_SEARCH -> {
                val searchTerm = message.substringAfter("tìm cho tôi", "").trim()
                if (searchTerm.isNotEmpty()) {
                    val site = Intent(Intent.ACTION_VIEW)
                    site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                    startActivity(site)
                }
            }
        }
    }

    private fun customBotMessage(message: String) {
        lifecycleScope.launch {
            delay(1000)
            val timeStamp = Time.timeStamp()
            messagesList.add(Message(message, RECEIVE_ID, timeStamp))
            adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

            // Cuộn xuống dưới sau khi thêm tin nhắn
            binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
        }
    }
}
