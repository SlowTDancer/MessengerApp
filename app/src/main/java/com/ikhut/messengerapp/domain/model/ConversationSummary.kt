package com.ikhut.messengerapp.domain.model

import com.ikhut.messengerapp.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ConversationSummary(
    val addresseeName: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessage: String = "",

    /** I don't know yet how we are going to fetch images. */
    val profileImageUrl: String? = null,
    val profileImageRes: Int? = null,
    val defaultProfileImage: Int = R.drawable.avatar_image_placeholder
)

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

