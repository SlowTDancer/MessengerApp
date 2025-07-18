package com.ikhut.messengerapp.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ConversationSummary(
    var addresseeName: String = "",
    var lastMessageTime: Long = System.currentTimeMillis(),
    var lastMessage: String = "",
    var imageRes: Int = 0,
    var imageUrl: String? = null,
    var localImagePath: String? = null
)

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

