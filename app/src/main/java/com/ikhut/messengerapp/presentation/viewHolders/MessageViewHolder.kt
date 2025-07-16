package com.ikhut.messengerapp.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.MessageReceivedLayoutBinding
import com.ikhut.messengerapp.databinding.MessageSentLayoutBinding
import com.ikhut.messengerapp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SentMessageViewHolder(
    private val binding: MessageSentLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.messageText.text = message.content
        binding.messageTime.text = message.timestamp.format()
    }
}

class ReceivedMessageViewHolder(
    private val binding: MessageReceivedLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.messageText.text = message.content
        binding.messageTime.text = message.timestamp.format()
    }
}

fun Long.format(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}