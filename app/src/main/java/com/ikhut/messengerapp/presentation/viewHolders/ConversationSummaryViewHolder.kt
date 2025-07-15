package com.ikhut.messengerapp.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.databinding.ConversationSummaryLayoutBinding
import com.ikhut.messengerapp.domain.model.ConversationSummary
import com.ikhut.messengerapp.domain.model.toLocalDateTime
import com.ikhut.messengerapp.presentation.utils.ProfilePictureLoader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class ConversationSummaryViewHolder(private val binding: ConversationSummaryLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(conversationSummary: ConversationSummary) {
        binding.addresseeName.text = conversationSummary.addresseeName

        binding.lastMessage.text = conversationSummary.lastMessage.trimWithEllipsis()

        binding.lastMessageTime.text =
            conversationSummary.lastMessageTime.toLocalDateTime().formatMessageTime()

        ProfilePictureLoader.loadProfilePicture(
            context = binding.root.context,
            imageView = binding.profilePicture,
            imageRes = conversationSummary.imageRes,
            imageUrl = conversationSummary.imageUrl,
            localImagePath = conversationSummary.localImagePath,
            placeholderName = conversationSummary.addresseeName
        )
    }

    private fun String.trimWithEllipsis(maxLength: Int = Constants.MAX_MESSAGE_LENGTH): String {
        return if (this.length > maxLength) {
            this.take(maxLength) + "..."
        } else {
            this
        }
    }

    private fun LocalDateTime.formatMessageTime(): String {
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
                this.format(
                    DateTimeFormatter.ofPattern("d MMM").withLocale(Locale.ENGLISH)
                ).uppercase()
            }
        }
    }
}