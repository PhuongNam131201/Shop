package com.example.phuongnam19973.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.phuongnam19973.R
import com.example.phuongnam19973.Model.Message
import com.example.phuongnam19973.databinding.MessageItemBinding
import com.example.phuongnam19973.utils.Constants.RECEIVE_ID
import com.example.phuongnam19973.utils.Constants.SEND_ID

class MessagingAdapter : RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(private val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                // Remove message on item clicked
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(currentMessage: Message) {
            when (currentMessage.id) {
                SEND_ID -> {
                    binding.tvMessage.apply {
                        text = currentMessage.message
                        visibility = View.VISIBLE
                    }
                    binding.tvBotMessage.visibility = View.GONE
                }
                RECEIVE_ID -> {
                    binding.tvBotMessage.apply {
                        text = currentMessage.message
                        visibility = View.VISIBLE
                    }
                    binding.tvMessage.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]
        holder.bind(currentMessage)
    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }
}