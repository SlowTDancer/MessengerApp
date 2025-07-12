package com.ikhut.messengerapp.presentation.homeFragments

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.ConversationSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary

class ConversationSummaryViewHolder(private val binding: ConversationSummaryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(conversationSummary: ConversationSummary) {
        binding.addresseeName.text = conversationSummary.addresseeName

        //TODO: trim last message with ...'s
        binding.lastMessage.text = conversationSummary.lastMessage

        //TODO: format lastMessageTime
        binding.lastMessageTime.text = conversationSummary.lastMessageTime.toString()

        //TODO: fix image setting
        binding.profilePicture.setImageResource(
            conversationSummary.profileImageRes ?: conversationSummary.defaultProfileImage
        )
    }
}