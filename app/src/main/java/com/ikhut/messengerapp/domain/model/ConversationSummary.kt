package com.ikhut.messengerapp.domain.model

import com.ikhut.messengerapp.R
import java.time.LocalDateTime

data class ConversationSummary(
    val addresseeName: String,
    val lastMessageTime: LocalDateTime,
    val lastMessage: String,

    /** I don't know yet how we are going to fetch images. */
    val profileImageUrl: String? = null,
    val profileImageRes: Int? = null,
    val defaultProfileImage: Int = R.drawable.avatar_image_placeholder
)
