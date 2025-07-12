package com.ikhut.messengerapp.presentation.homeFragments

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.databinding.ConversationSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ConversationSummaryViewHolder(private val binding: ConversationSummaryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val MAX_MESSAGE_LENGTH = 57
    }

    fun bind(conversationSummary: ConversationSummary) {
        binding.addresseeName.text = conversationSummary.addresseeName

        binding.lastMessage.text = conversationSummary.lastMessage.trimWithEllipsis()

        binding.lastMessageTime.text = conversationSummary.lastMessageTime.formatMessageTime()

        //TODO: fix image setting
        binding.profilePicture.setImageResource(
            conversationSummary.profileImageRes ?: conversationSummary.defaultProfileImage
        )
    }

    fun String.trimWithEllipsis(maxLength: Int = MAX_MESSAGE_LENGTH): String {
        return if (this.length > maxLength) {
            this.take(maxLength) + "..."
        } else {
            this
        }
    }

    fun LocalDateTime.formatMessageTime(): String {
        val now = LocalDateTime.now()
        val minutesAgo = ChronoUnit.MINUTES.between(this, now)

        return when {
            minutesAgo < 60 -> {
                val minutes = minutesAgo.coerceAtLeast(1) // Show at least 1 min
                "$minutes min"
            }
            minutesAgo < 1440 -> { // Less than 24 hours (1440 minutes)
                val hours = minutesAgo / 60
                "$hours hour"
            }
            else -> {
                this.format(DateTimeFormatter.ofPattern("d MMM").withLocale(java.util.Locale.ENGLISH))
                    .uppercase()
            }
        }
    }
}