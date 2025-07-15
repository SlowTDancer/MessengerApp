package com.ikhut.messengerapp.domain.model

import com.ikhut.messengerapp.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ConversationSummary(
    var addresseeName: String = "",
    var lastMessageTime: Long = System.currentTimeMillis(),
    var lastMessage: String = "",

    var profileImageUrl: String? = null,
    var defaultProfileImage: Int = R.drawable.avatar_image_placeholder
)

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

